package dev.patatje.hideandseek.listeners;

import dev.patatje.hideandseek.HideAndSeek;
import dev.patatje.hideandseek.utils.BlockUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

public class onDismount implements Listener {
    private final HideAndSeek plugin;

    public onDismount(HideAndSeek plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDismountEvent(EntityDismountEvent event) {
        if(event.getEntity() instanceof Player player) {
            if(event.getDismounted().hasMetadata("hideAndSeek")) {
                BlockUtils.removeBlock(player, plugin);
                if(plugin.getArenaManager().getArena(player) != null) {
                    BlockUtils.spawnFakeFallingBlock(player, plugin.getArenaManager().getArena(player).getGame().getDisguise(player));
                }
            }
        }
    }
}
