package dev.patatje.hideandseek.utils;

import org.bukkit.Location;

public class SpawnPoint {
    String name;
    String description;
    Location location;

    public SpawnPoint(String name, String description, Location location) {
        this.name = name;
        this.description = description;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Location getLocation() {
        return location;
    }
}
