package dev.patatje.hideandseek;

import com.comphenix.protocol.ProtocolLibrary;
import dev.patatje.hideandseek.commands.HideAndSeekCommand;
import dev.patatje.hideandseek.listeners.*;
import dev.patatje.hideandseek.managers.ArenaManager;
import dev.patatje.hideandseek.managers.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class HideAndSeek extends JavaPlugin {
    private ArenaManager arenaManager;

    @Override
    public void onEnable() {
        getLogger().info("Hide and Seek plugin is loading...");

        // Load the config file
        ConfigManager.setupConfig(this);

        // Create the arena manager which will load the arenas
        arenaManager = new ArenaManager(this);

        // Used for both ProtocolLib and Bukkit listeners so needs to be instantiated here
        onHit onHit = new onHit(this);

        // Load the Bukkit listeners
        getServer().getPluginManager().registerEvents(new onJoin(this), this);
        getServer().getPluginManager().registerEvents(new onQuit(this), this);
        getServer().getPluginManager().registerEvents(new onMove(this), this);
        getServer().getPluginManager().registerEvents(new onDismount(this), this);
        getServer().getPluginManager().registerEvents(new onDamage(this), this);
        getServer().getPluginManager().registerEvents(new onRegen(this), this);
        getServer().getPluginManager().registerEvents(new onInteract(this), this);
        getServer().getPluginManager().registerEvents(onHit, this);

        // Load the ProtocolLib listeners
        ProtocolLibrary.getProtocolManager().addPacketListener(onHit);

        // Load the commands
        getCommand("hideandseek").setExecutor(new HideAndSeekCommand(this));

        getLogger().info("Hide and Seek plugin has been loaded!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Hide and Seek plugin is unloading...");

        arenaManager.getArenas().values().forEach((arena) -> arena.stop(true));

        getLogger().info("Hide and Seek plugin has been unloaded!");
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }
}
