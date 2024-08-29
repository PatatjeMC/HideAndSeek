package dev.patatje.hideandseek.utils;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;

public class Arena {
    private String name;
    private String Description;
    private World world;

    private Location waitingLocation;
    private ArrayList<SpawnPoint> spawnPoints;

    public Arena(String name , String Description, World world, Location waitingLocation, ArrayList<SpawnPoint> spawnPoints) {
        this.name = name;
        this.Description = Description;
        this.world = world;
        this.waitingLocation = waitingLocation;
        this.spawnPoints = spawnPoints;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return Description;
    }

    public World getWorld() {
        return world;
    }

    public Location getWaitingLocation() {
        return waitingLocation;
    }

    public ArrayList<SpawnPoint> getSpawnPoints() {
        return spawnPoints;
    }
}
