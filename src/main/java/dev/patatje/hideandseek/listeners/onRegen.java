package dev.patatje.hideandseek.listeners;

import dev.patatje.hideandseek.HideAndSeek;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class onRegen implements Listener {
    private final HideAndSeek plugin;

    public onRegen(HideAndSeek plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {
        if(event.getEntity() instanceof Player player) {
            if(plugin.getArenaManager().getArena(player) == null) return;

            if (plugin.getArenaManager().getArena(player).getGame().getHiders().contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }
}
