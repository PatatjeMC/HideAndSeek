package dev.patatje.hideandseek.instances;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.patatje.hideandseek.HideAndSeek;
import dev.patatje.hideandseek.enums.GameState;
import dev.patatje.hideandseek.managers.ConfigManager;
import dev.patatje.hideandseek.utils.ChatUtils;
import dev.patatje.hideandseek.utils.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class Game {

    private final HideAndSeek plugin;

    private final Arena arena;
    private UUID originalSeeker;
    private final List<UUID> seekers;
    private List<UUID> originalHiders;
    private final List<UUID> hiders;
    private final HashMap<UUID, Material> playerDisguises;
    private final HashMap<UUID, Integer> playerHealth;

    private final HeadstartCountdown headstartCountdown;

    private final GameTimer gameTimer;

    public final HashMap<UUID, TransformCountdown> transformCountdowns = new HashMap<>();

    public Game(HideAndSeek plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;

        seekers = new ArrayList<>();
        hiders = new ArrayList<>();
        playerDisguises = new HashMap<>();
        playerHealth = new HashMap<>();

        headstartCountdown = new HeadstartCountdown(plugin, arena);
        gameTimer = new GameTimer(plugin, arena);
    }

    public void start() {
        arena.setState(GameState.INGAME);
        headstartCountdown.start();

        seekers.add(arena.getPlayers().get(new Random().nextInt(arena.getPlayers().size())));
        originalSeeker = seekers.get(0);

        for (UUID uuid : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);

            player.closeInventory();

            if (seekers.contains(uuid)) {
                player.sendMessage(ChatUtils.colorize("&aJe bent een zoeker! Je moet nog even wachten tot de verstoppers zich hebben verstopt."));
            } else {
                hiders.add(uuid);
                player.sendMessage(ChatUtils.colorize("&aJe bent een verstopper! Je moet je nu gaan verstoppen voordat de zoeker je vindt!"));
                player.teleport(arena.getSpawnPoints().get(new Random().nextInt(arena.getSpawnPoints().size())));

                TransformCountdown transformCountdown = new TransformCountdown(plugin, arena, player);
                transformCountdowns.put(player.getUniqueId(), transformCountdown);

                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(6);

                BlockUtils.spawnFakeFallingBlock(player, getDisguise(player));

                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, false));

            }
        }

        originalHiders = new ArrayList<>(hiders);
    }

    public void setDisguise(Player player, Material material) {
        playerDisguises.put(player.getUniqueId(), material);
    }

    public Material getDisguise(Player player) {
        if(playerDisguises.containsKey(player.getUniqueId())) {
            return playerDisguises.get(player.getUniqueId());
        } else {
            Material material = arena.getAllowedDisguises().get(new Random().nextInt(arena.getAllowedDisguises().size()));
            playerDisguises.put(player.getUniqueId(), material);
            return material;
        }
    }

    public TransformCountdown getTransformCountdown(Player player) {
        return transformCountdowns.get(player.getUniqueId());
    }

    public void releaseSeekers() {
        for (UUID uuid : seekers) {
            Player player = Bukkit.getPlayer(uuid);
            arena.sendMessage("&aDe game is begonnen! De zoekers mogen nu op zoek naar de verstoppers!");
            player.teleport(arena.getSpawnPoints().get(new Random().nextInt(arena.getSpawnPoints().size())));
        }
        gameTimer.start();
    }

    public void findHider(Player seeker, Player hider) {
        hiders.remove(hider.getUniqueId());
        seekers.add(hider.getUniqueId());

        BlockUtils.removeBlock(hider, plugin);
        getTransformCountdown(hider).cancel();
        BlockUtils.removeFakeFallingBlock(hider.getUniqueId());
        hider.removePotionEffect(PotionEffectType.INVISIBILITY);
        hider.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        hider.setHealth(20);

        if(seeker != null) {
            arena.sendMessage("&a" + seeker.getName() + " heeft " + hider.getName() + " gevonden!");
        } else {
            arena.sendMessage("&a" + hider + " is gevonden!");
        }

        if(hiders.isEmpty()) {
            winSeekers();
        }

        hider.sendMessage(ChatUtils.colorize("&aJe bent gevonden! Je bent nu een zoeker!"));
        hider.teleport(arena.getSpawnPoints().get(new Random().nextInt(arena.getSpawnPoints().size())));
    }

    public void damage(Player hider, Player seeker) {
        if(playerHealth.containsKey(hider.getUniqueId())) {
            playerHealth.put(hider.getUniqueId(), playerHealth.get(hider.getUniqueId()) - 2);
        } else {
            playerHealth.put(hider.getUniqueId(), 4);
        }

        if(playerHealth.get(hider.getUniqueId()) <= 0) {
            hider.sendTitle(ChatUtils.colorize("&cJe bent gevonden!"), "", 10, 40, 10);
            findHider(seeker, hider);
        } else {
            hider.setHealth(playerHealth.get(hider.getUniqueId()));
        }
    }

    public void winSeekers() {
        arena.sendMessage("&aAlle verstoppers zijn gevonden! De zoekers hebben gewonnen!");
        arena.setState(GameState.END);

        for(UUID uuid : arena.getPlayers()) {
            if(originalSeeker.equals(uuid)) {
                Player player = Bukkit.getPlayer(uuid);
                player.sendTitle(ChatUtils.colorize("&aJe hebt gewonnen!"), "", 10, 40, 10);
            } else {
                Player player = Bukkit.getPlayer(uuid);
                player.sendTitle(ChatUtils.colorize("&cJe hebt verloren!"), "", 10, 40, 10);
            }
        }

        stopRunnables();

        sendRequest("seekers");

        Bukkit.getScheduler().runTaskLater(plugin, () -> arena.stop(true), 100L);
    }

    public void winHiders(boolean sendMessage) {
        if(sendMessage) {
            arena.sendMessage("&aDe tijd is om! De verstoppers hebben gewonnen!");
        }
        arena.setState(GameState.END);

        for(UUID uuid : arena.getPlayers()) {
            if(originalHiders.contains(uuid)) {
                Player player = Bukkit.getPlayer(uuid);
                player.sendTitle(ChatUtils.colorize("&aJe hebt gewonnen!"), "", 10, 40, 10);
            } else {
                Player player = Bukkit.getPlayer(uuid);
                player.sendTitle(ChatUtils.colorize("&cJe hebt verloren!"), "", 10, 40, 10);
            }
        }

        stopRunnables();

        sendRequest("hiders");

        Bukkit.getScheduler().runTaskLater(plugin, () -> arena.stop(true), 100L);
    }

    private void sendRequest(String winner) {
        if(!ConfigManager.isRequestEnabled()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            JsonObject body = new JsonObject();

            body.addProperty("winners", winner);
            JsonArray hiders = new JsonArray();
            for(UUID uuid : originalHiders) {
                JsonObject hider = new JsonObject();
                hider.addProperty("uuid", uuid.toString());
                hider.addProperty("name", Bukkit.getPlayer(uuid).getName());
                hiders.add(hider);
            }
            body.add("hiders", hiders);

            JsonObject seeker = new JsonObject();
            seeker.addProperty("uuid", originalSeeker.toString());
            seeker.addProperty("name", Bukkit.getPlayer(originalSeeker).getName());
            body.add("seeker", seeker);

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(ConfigManager.getRequestUrl()))
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()));

            HttpRequest request = requestBuilder.build();

            try {
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                plugin.getLogger().severe("Failed to send request to " + ConfigManager.getRequestUrl());
            }
        });
    }

    public List<UUID> getSeekers() {
        return seekers;
    }

    public List<UUID> getHiders() {
        return hiders;
    }

    public void stopRunnables() {
        headstartCountdown.cancel();
        gameTimer.cancel();
        for(TransformCountdown transformCountdown : transformCountdowns.values()) {
            transformCountdown.cancel();
        }
    }
}
