package dev.patatje.hideandseek.managers;

import dev.patatje.hideandseek.Main;
import dev.patatje.hideandseek.utils.Arena;
import dev.patatje.hideandseek.utils.SpawnPoint;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ArenaManager {
    public HashMap<String, Arena> arenas = new HashMap<>();

    private File arenasFile;
    private YamlConfiguration arenasConfig;
    private Main plugin;

    public ArenaManager(Main plugin) {
        this.plugin = plugin;
        setupArenasConfig();
        loadArenas();
    }

    private void setupArenasConfig() {
        arenasFile = new File(plugin.getDataFolder(), "maps.yml");

        if (!arenasFile.exists()) {
            arenasFile.getParentFile().mkdirs();
            plugin.saveResource("maps.yml", false);
        }

        arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
    }

    private void loadArenas() {
        for(String arenaKey : arenasConfig.getKeys(false)) {
            ConfigurationSection arenaConfig = arenasConfig.getConfigurationSection(arenaKey);

            // Check if the arena config has all the required fields
            if(
                    !arenaConfig.contains("name") ||
                    !arenaConfig.contains("description") ||
                    !arenaConfig.contains("world") ||
                    !arenaConfig.contains("waitingRoom") ||
                    !arenaConfig.contains("spawnPoints")
            ) {
                plugin.getLogger().severe("Invalid arena configuration for arena " + arenaKey);
                continue;
            }

            // Get some basic information about the arena
            String name = arenaConfig.getString("name");
            String description = arenaConfig.getString("description");
            World world = Bukkit.getWorld(arenaConfig.getString("world"));

            // Check if the world exists
            if(world == null) {
                plugin.getLogger().severe("Invalid world for arena " + arenaKey);
                continue;
            }

            // Check if the waiting room location has all the required fields
            if(
                    !arenaConfig.contains("waitingRoom.world") ||
                    !arenaConfig.contains("waitingRoom.x") ||
                    !arenaConfig.contains("waitingRoom.y") ||
                    !arenaConfig.contains("waitingRoom.z") ||
                    !arenaConfig.contains("waitingRoom.yaw") ||
                    !arenaConfig.contains("waitingRoom.pitch")
            ) {
                plugin.getLogger().severe("Invalid waiting room location for arena " + arenaKey);
                continue;
            }

            // Get the waiting room location
            Location waitingLocation = new Location(
                    Bukkit.getWorld(arenaConfig.getString("waitingRoom.world")),
                    arenaConfig.getDouble("waitingRoom.x"),
                    arenaConfig.getDouble("waitingRoom.y"),
                    arenaConfig.getDouble("waitingRoom.z"),
                    (float) arenaConfig.getDouble("waitingRoom.yaw"),
                    (float) arenaConfig.getDouble("waitingRoom.pitch")
            );

            // Check if the waiting room world exists
            if(waitingLocation.getWorld() == null) {
                plugin.getLogger().severe("Invalid world for waiting room location for arena " + arenaKey);
                continue;
            }

            ArrayList<SpawnPoint> spawnPoints = new ArrayList<>();

            // Loop through all the spawn points in the arena config and load them
            for(String spawnPointKey : arenaConfig.getConfigurationSection("spawnLocations").getKeys(false)) {
                ConfigurationSection spawnPointConfig = arenaConfig.getConfigurationSection("spawnLocations." + spawnPointKey);

                // Check if the spawn point has all the required fields
                if(
                        !spawnPointConfig.contains("name") ||
                        !spawnPointConfig.contains("description") ||
                        !spawnPointConfig.contains("x") ||
                        !spawnPointConfig.contains("y") ||
                        !spawnPointConfig.contains("z") ||
                        !spawnPointConfig.contains("yaw") ||
                        !spawnPointConfig.contains("pitch")
                ) {
                    plugin.getLogger().severe("Invalid spawn location for arena " + arenaKey);
                    continue;
                }

                // Get the spawn point location
                Location spawnLocation = new Location(
                        world,
                        spawnPointConfig.getDouble("x"),
                        spawnPointConfig.getDouble("y"),
                        spawnPointConfig.getDouble("z"),
                        (float) spawnPointConfig.getDouble("yaw"),
                        (float) spawnPointConfig.getDouble("pitch")
                );

                // Create a new spawn point object and add it to the list of spawn points
                SpawnPoint spawnPoint = new SpawnPoint(
                        spawnPointConfig.getString("name"),
                        spawnPointConfig.getString("description"),
                        spawnLocation
                );
                spawnPoints.add(spawnPoint);
            }

            // Create a new arena object and add it to the list of arenas
            Arena arena = new Arena(name, description, world, waitingLocation, spawnPoints);
            arenas.put(arenaKey, arena);
        }
    }

    public Arena getArena(String name) {
        return arenas.get(name);
    }

    public HashMap<String, Arena> getArenas() {
        return arenas;
    }
}
