package dev.patatje.hideandseek.guis;

import dev.patatje.hideandseek.HideAndSeek;
import dev.patatje.hideandseek.enums.GameState;
import dev.patatje.hideandseek.instances.Arena;
import dev.patatje.hideandseek.managers.ConfigManager;
import dev.patatje.hideandseek.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class ArenaPickerGUI implements Listener {
    private final HideAndSeek plugin;
    private final Player player;
    private final Inventory inventory;
    private final HashMap<ItemStack, Arena> arenas = new HashMap<>();

    public ArenaPickerGUI(HideAndSeek plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        int size = Math.max((int) (Math.ceil(plugin.getArenaManager().getArenas().size() / 9d) * 9), 9);

        inventory = Bukkit.createInventory(null, size, ChatUtils.colorize("&4Kies een arena"));

        for(String arenaKey : plugin.getArenaManager().getArenas().keySet()) {
            Arena arena = plugin.getArenaManager().getArena(arenaKey);

            if(arena.getState() != GameState.LOBBY && arena.getState() != GameState.COUNTDOWN) continue;

            if(arena.getPlayers().size() >= ConfigManager.getMaxPlayers()) continue;

            ItemStack item = new ItemStack(arena.getIcon());
            ItemMeta itemMeta = item.getItemMeta();

            itemMeta.setDisplayName(ChatUtils.colorize(arena.getName()));

            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatUtils.colorize(arena.getDescription()));
            itemMeta.setLore(lore);

            item.setItemMeta(itemMeta);

            arenas.put(item, arena);
            inventory.addItem(item);
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open() {
        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(event.getInventory().equals(inventory)) {
            event.setCancelled(true);
        }

        if(event.getClickedInventory().equals(inventory)) {
            if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

            if(!arenas.containsKey(event.getCurrentItem())) return;

            player.closeInventory();

            Arena arena = arenas.get(event.getCurrentItem());

            if(arena.getPlayers().size() >= ConfigManager.getMaxPlayers()) {
                player.sendMessage(ChatUtils.colorize("&cDeze arena is vol."));
                return;
            }

            if(arena.getState() != GameState.LOBBY && arena.getState() != GameState.COUNTDOWN) {
                player.sendMessage(ChatUtils.colorize("&cDeze arena is al begonnen."));
                return;
            }

            if(plugin.getArenaManager().getArena(player) != null) {
                player.sendMessage(ChatUtils.colorize("&cJe zit al in een arena."));
                return;
            }

            arena.addPlayer(player);
        }
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event) {
        if(event.getPlayer().equals(player) && event.getInventory().equals(inventory)) {
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        if(event.getPlayer().equals(player)) {
            HandlerList.unregisterAll(this);
        }
    }
}
