package dev.patatje.hideandseek.listeners;

import dev.patatje.hideandseek.HideAndSeek;
import dev.patatje.hideandseek.instances.Arena;
import dev.patatje.hideandseek.managers.ConfigManager;
import dev.patatje.hideandseek.utils.BlockUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class onQuit implements Listener {
    private final HideAndSeek plugin;

    public onQuit(HideAndSeek plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Arena arena = plugin.getArenaManager().getArena(player);

        BlockUtils.removeFakeFallingBlock(player.getUniqueId());

        if(arena != null) {
            arena.removePlayer(player);
            player.teleport(ConfigManager.getLobbyLocation());
        }
    }
}
