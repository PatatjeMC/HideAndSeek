package dev.patatje.hideandseek.listeners;

import dev.patatje.hideandseek.HideAndSeek;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class onJoin implements Listener {
    private final HideAndSeek plugin;

    public onJoin(HideAndSeek plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

    }
}