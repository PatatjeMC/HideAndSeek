package dev.patatje.hideandseek.listeners;

import dev.patatje.hideandseek.HideAndSeek;
import dev.patatje.hideandseek.enums.GameState;
import dev.patatje.hideandseek.utils.BlockUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class onMove implements Listener {

    private final HideAndSeek plugin;

    public onMove(HideAndSeek plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent event) {
        if(BlockUtils.fallingBlocks.containsKey(event.getPlayer().getUniqueId())) {
            BlockUtils.moveFakeFallingBlock(BlockUtils.fallingBlocks.get(event.getPlayer().getUniqueId()), event.getPlayer().getLocation());
        }

        if(event.getFrom().getBlock().equals(event.getTo().getBlock())) return;

        if(plugin.getArenaManager().getArena(event.getPlayer()) == null) return;

        if(plugin.getArenaManager().getArena(event.getPlayer()).getState() == GameState.INGAME) {
            if(BlockUtils.fallingBlocks.containsKey(event.getPlayer().getUniqueId())) {
                plugin.getArenaManager().getArena(event.getPlayer()).getGame().getTransformCountdown(event.getPlayer()).reset();
            }
        }
    }
}
