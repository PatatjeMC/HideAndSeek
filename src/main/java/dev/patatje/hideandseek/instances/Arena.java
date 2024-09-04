package dev.patatje.hideandseek.instances;

import dev.patatje.hideandseek.HideAndSeek;
import dev.patatje.hideandseek.enums.GameState;
import dev.patatje.hideandseek.guis.BlockPickerGUI;
import dev.patatje.hideandseek.managers.ConfigManager;
import dev.patatje.hideandseek.utils.ChatUtils;
import dev.patatje.hideandseek.utils.BlockUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Arena {
    private final HideAndSeek plugin;

    private final String name;
    private final String description;
    private final Material icon;

    private final Location waitingLocation;
    private final List<Location> spawnPoints;
    private final List<Material> allowedDisguises;

    private GameState state;
    private final List<UUID> players;
    private LobbyCountdown lobbyCountdown;
    private Game game;

    public Arena(HideAndSeek plugin, String name, String description, Material icon, Location waitingLocation, List<Location> spawnPoints, List<Material> allowedDisguises) {
        this.plugin = plugin;

        this.name = name;
        this.description = description;
        this.icon = icon;
        this.waitingLocation = waitingLocation;
        this.spawnPoints = spawnPoints;
        this.allowedDisguises = allowedDisguises;

        this.state = GameState.LOBBY;
        this.players = new ArrayList<>();
        this.lobbyCountdown = new LobbyCountdown(plugin, this);
        this.game = new Game(plugin, this);
    }

    public void start() {
        game.start();
    }

    public void stop(boolean kickPlayers) {
        if(kickPlayers) {
            players.forEach(uuid -> {
                Player player = Bukkit.getPlayer(uuid);
                BlockUtils.removeFakeFallingBlock(uuid);
                BlockUtils.removeBlock(player, plugin);

                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                player.removePotionEffect(PotionEffectType.SATURATION);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);

                player.closeInventory();
                player.setHealth(20);

                player.teleport(ConfigManager.getLobbyLocation());
            });
            players.clear();
        } else {
            players.forEach(uuid -> {
                Player player = Bukkit.getPlayer(uuid);
                BlockUtils.removeFakeFallingBlock(uuid);
                BlockUtils.removeBlock(player, plugin);

                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);

                player.closeInventory();
                player.setHealth(20);

                player.teleport(waitingLocation);
            });
        }
        state = GameState.LOBBY;
        lobbyCountdown.cancel();
        lobbyCountdown = new LobbyCountdown(plugin, this);
        game.stopRunnables();
        game = new Game(plugin, this);
    }

    public void sendMessage(String message) {
        for (UUID uuid : players) {
            Bukkit.getPlayer(uuid).sendMessage(ChatUtils.colorize(message));
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Material getIcon() {
        return icon;
    }

    public Location getWaitingLocation() {
        return waitingLocation;
    }

    public List<Location> getSpawnPoints() {
        return spawnPoints;
    }

    public List<Material> getAllowedDisguises() {
        return allowedDisguises;
    }

    public GameState getState() {
        return state;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public Game getGame() {
        return game;
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
        player.teleport(getWaitingLocation());
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, true, false));
        player.setHealth(20);

        new BlockPickerGUI(plugin, player).open();

        if (state.equals(GameState.LOBBY) && players.size() >= ConfigManager.getRequiredPlayers()) {
            lobbyCountdown.start();
        }
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        game.getSeekers().remove(player.getUniqueId());
        game.getHiders().remove(player.getUniqueId());
        player.teleport(ConfigManager.getLobbyLocation());

        BlockUtils.removeFakeFallingBlock(player.getUniqueId());
        BlockUtils.removeBlock(player, plugin);

        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.SATURATION);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);

        player.closeInventory();
        player.setHealth(20);

        if(game.getTransformCountdown(player) != null) {
            game.getTransformCountdown(player).cancel();
        }

        if(state == GameState.COUNTDOWN && players.size() < ConfigManager.getRequiredPlayers()) {
            stop(false);
            return;
        }

        if(state == GameState.INGAME) {
            if(game.getSeekers().isEmpty()) {
                sendMessage("&aAlle zoekers hebben het spel verlaten! De verstoppers hebben gewonnen!");
                game.winHiders(false);
            } else if(game.getHiders().isEmpty()) {
                game.winSeekers();
            }
        }
    }

    public void setState(GameState state) {
        this.state = state;
    }
}
