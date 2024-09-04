package dev.patatje.hideandseek.guis;

import dev.patatje.hideandseek.HideAndSeek;
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

public class BlockPickerGUI implements Listener {
    private final HideAndSeek plugin;
    private final Player player;
    private final Inventory inventory;

    public BlockPickerGUI(HideAndSeek plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        int size = (int) (Math.ceil(plugin.getArenaManager().getArena(player).getAllowedDisguises().size() / 9d) * 9);

        inventory = Bukkit.createInventory(null, size, ChatUtils.colorize("&4Kies een vermomming"));

        for(Material disguise : plugin.getArenaManager().getArena(player).getAllowedDisguises()) {
            ItemStack item = new ItemStack(disguise);

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

            if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

            Material material = event.getCurrentItem().getType();

            plugin.getArenaManager().getArena(player).getGame().setDisguise(player, material);

            player.closeInventory();
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
