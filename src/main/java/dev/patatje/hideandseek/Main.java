package dev.patatje.hideandseek;

import dev.patatje.hideandseek.managers.ArenaManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private ArenaManager arenaManager;

    @Override
    public void onEnable() {
        //Load the config file
        this.saveDefaultConfig();

        //Create the arena manager which will load the arenas
        arenaManager = new ArenaManager(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
