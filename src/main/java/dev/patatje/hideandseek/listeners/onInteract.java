package dev.patatje.hideandseek.listeners;

import dev.patatje.hideandseek.HideAndSeek;
import dev.patatje.hideandseek.utils.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class onInteract implements Listener {
    private final HideAndSeek plugin;

    public onInteract(HideAndSeek plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        if(plugin.getArenaManager().getArena(event.getPlayer()) == null) return;

        if(!plugin.getArenaManager().getArena(event.getPlayer()).getGame().getSeekers().contains(event.getPlayer().getUniqueId())) return;

        if(!event.getClickedBlock().hasMetadata("hideAndSeek")) return;

        Player target = Bukkit.getPlayer(UUID.fromString(event.getClickedBlock().getMetadata("hideAndSeek").get(0).asString()));

        if(target == null) {
            event.getClickedBlock().removeMetadata("hideAndSeek", plugin);
            return;
        }

        BlockUtils.removeBlock(target, plugin);
        BlockUtils.spawnFakeFallingBlock(target, plugin.getArenaManager().getArena(target).getGame().getDisguise(target));

        plugin.getArenaManager().getArena(target).getGame().damage(target, event.getPlayer());
    }
}
