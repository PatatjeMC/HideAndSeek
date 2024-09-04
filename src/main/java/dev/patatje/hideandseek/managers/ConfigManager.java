package dev.patatje.hideandseek.managers;

import dev.patatje.hideandseek.HideAndSeek;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private static FileConfiguration config;

    // Setup and validate the config file
    public static void setupConfig(HideAndSeek plugin) {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        if(!config.contains("required-players")) {
            plugin.getLogger().severe("Missing required-players in config.yml");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        if(!config.contains("max-players")) {
            plugin.getLogger().severe("Missing max-players in config.yml");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        if(!config.contains("lobby-timer")) {
            plugin.getLogger().severe("Missing lobby-timer in config.yml");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        if(!config.contains("lobby-location")) {
            plugin.getLogger().severe("Missing lobby-location in config.yml");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        if(!config.contains("head-start-timer")) {
            plugin.getLogger().severe("Missing head-start-timer in config.yml");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        if(!config.contains("game-timer")) {
            plugin.getLogger().severe("Missing game-timer in config.yml");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        if(!config.contains("transform-timer")) {
            plugin.getLogger().severe("Missing transform-timer in config.yml");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        if(
                !config.contains("lobby-location.world") ||
                !config.contains("lobby-location.x") ||
                !config.contains("lobby-location.y") ||
                !config.contains("lobby-location.z") ||
                !config.contains("lobby-location.yaw") ||
                !config.contains("lobby-location.pitch")
        ){
            plugin.getLogger().severe("Invalid lobby-location in config.yml");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        if(Bukkit.getWorld(config.getString("lobby-location.world")) == null) {
            plugin.getLogger().severe("Invalid world in lobby-location in config.yml");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        if(
                !config.contains("request.enabled") ||
                !config.contains("request.url")
        ) {
            plugin.getLogger().severe("Invalid request settings in config.yml");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    public static int getRequiredPlayers() {
        return config.getInt("required-players");
    }

    public static int getMaxPlayers() {
        return config.getInt("max-players");
    }

    public static int getLobbyTimer() {
        return config.getInt("lobby-timer");
    }

    public static int getHeadStartTimer() {
        return config.getInt("head-start-timer");
    }

    public static int getGameTimer() {
        return config.getInt("game-timer");
    }

    public static int getTransformTimer() {
        return config.getInt("transform-timer");
    }

    public static Location getLobbyLocation() {
        return new Location(
                Bukkit.getWorld(config.getString("lobby-location.world")),
                config.getDouble("lobby-location.x"),
                config.getDouble("lobby-location.y"),
                config.getDouble("lobby-location.z"),
                (float) config.getDouble("lobby-location.yaw"),
                (float) config.getDouble("lobby-location.pitch")
        );
    }

    public static boolean isRequestEnabled() {
        return config.getBoolean("request.enabled");
    }

    public static String getRequestUrl() {
        return config.getString("request.url");
    }
}
