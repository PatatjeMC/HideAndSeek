package dev.patatje.hideandseek.managers;

import dev.patatje.hideandseek.HideAndSeek;
import dev.patatje.hideandseek.instances.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ArenaManager {
    public HashMap<String, Arena> arenas = new HashMap<>();

    private File arenasFile;
    private YamlConfiguration arenasConfig;
    private HideAndSeek plugin;

    public ArenaManager(HideAndSeek plugin) {
        this.plugin = plugin;
        setupArenasConfig();
        loadArenas();
    }

    private void setupArenasConfig() {
        arenasFile = new File(plugin.getDataFolder(), "maps.yml");

        // Create the arenas config file if it doesn't exist
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
                    !arenaConfig.contains("icon") ||
                    !arenaConfig.contains("world") ||
                    !arenaConfig.contains("waiting-room") ||
                    !arenaConfig.contains("spawn-points")
            ) {
                plugin.getLogger().severe("Invalid arena configuration for arena " + arenaKey);
                continue;
            }

            // Get some basic information about the arena
            String name = arenaConfig.getString("name");
            String description = arenaConfig.getString("description");

            Material icon = Material.getMaterial(arenaConfig.getString("icon"));

            // Check if the icon is a valid material
            if(icon == null) {
                plugin.getLogger().severe("Invalid icon for arena " + arenaKey);
                continue;
            }

            World world = Bukkit.getWorld(arenaConfig.getString("world"));

            // Check if the world exists
            if(world == null) {
                plugin.getLogger().severe("Invalid world for arena " + arenaKey);
                continue;
            }

            // Check if the waiting room location has all the required fields
            if(
                    !arenaConfig.contains("waiting-room.world") ||
                    !arenaConfig.contains("waiting-room.x") ||
                    !arenaConfig.contains("waiting-room.y") ||
                    !arenaConfig.contains("waiting-room.z") ||
                    !arenaConfig.contains("waiting-room.yaw") ||
                    !arenaConfig.contains("waiting-room.pitch")
            ) {
                plugin.getLogger().severe("Invalid waiting room location for arena " + arenaKey);
                continue;
            }

            // Get the waiting room location
            Location waitingLocation = new Location(
                    Bukkit.getWorld(arenaConfig.getString("waiting-room.world")),
                    arenaConfig.getDouble("waiting-room.x"),
                    arenaConfig.getDouble("waiting-room.y"),
                    arenaConfig.getDouble("waiting-room.z"),
                    (float) arenaConfig.getDouble("waiting-room.yaw"),
                    (float) arenaConfig.getDouble("waiting-room.pitch")
            );

            // Check if the waiting room world exists
            if(waitingLocation.getWorld() == null) {
                plugin.getLogger().severe("Invalid world for waiting room location for arena " + arenaKey);
                continue;
            }

            ArrayList<Location> spawnPoints = new ArrayList<>();

            // Loop through all the spawn points in the arena config and load them
            for(String spawnPointKey : arenaConfig.getConfigurationSection("spawn-points").getKeys(false)) {
                ConfigurationSection spawnPointConfig = arenaConfig.getConfigurationSection("spawn-points." + spawnPointKey);

                // Check if the spawn point has all the required fields
                if(
                        !spawnPointConfig.contains("x") ||
                        !spawnPointConfig.contains("y") ||
                        !spawnPointConfig.contains("z") ||
                        !spawnPointConfig.contains("yaw") ||
                        !spawnPointConfig.contains("pitch")
                ) {
                    plugin.getLogger().severe("Invalid spawn point " + spawnPointKey + " for arena " + arenaKey);
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

                spawnPoints.add(spawnLocation);
            }

            ArrayList<Material> allowedBlocks = new ArrayList<>();

            for(String allowedBlock : arenaConfig.getStringList("allowed-blocks")) {
                // Check if the block is a valid block
                if(Material.getMaterial(allowedBlock) == null) {
                    plugin.getLogger().severe("Invalid block " + allowedBlock + " for arena " + arenaKey);
                    continue;
                }

                allowedBlocks.add(Material.getMaterial(allowedBlock));
            }

            // Create a new arena object and add it to the list of arenas
            Arena arena = new Arena(plugin, name, description, icon, waitingLocation, spawnPoints, allowedBlocks);
            arenas.put(arenaKey, arena);
        }
    }

    public Arena getArena(String name) {
        return arenas.get(name);
    }

    public Arena getArena(Player player) {
        for(Arena arena : arenas.values()) {
            if(arena.getPlayers().contains(player.getUniqueId())) {
                return arena;
            }
        }
        return null;
    }

    public HashMap<String, Arena> getArenas() {
        return arenas;
    }
}
