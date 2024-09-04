package dev.patatje.hideandseek.listeners;

import dev.patatje.hideandseek.HideAndSeek;
import dev.patatje.hideandseek.enums.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class onDamage implements Listener {
    private final HideAndSeek plugin;

    public onDamage(HideAndSeek plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;

        if(plugin.getArenaManager().getArena(player) == null) return;

        if(plugin.getArenaManager().getArena(player).getState() != GameState.INGAME) {
            event.setCancelled(true);
        }

        if(!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            event.setCancelled(true);
        }
    }
}
