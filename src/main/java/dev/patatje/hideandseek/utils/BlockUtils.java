package dev.patatje.hideandseek.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import dev.patatje.hideandseek.HideAndSeek;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.block.data.CraftBlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BlockUtils {
    public static HashMap<UUID, Integer> fallingBlocks = new HashMap<>();
    public static HashMap<UUID, ArmorStand> blocks = new HashMap<>();

    public static void spawnFakeFallingBlock(Player player, Material material) {
        Integer id = Math.abs(player.getUniqueId().hashCode());

        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);

        packet.getEntityTypeModifier().write(0, EntityType.FALLING_BLOCK);

        packet.getIntegers().write(0, id);
        packet.getUUIDs().write(0, UUID.randomUUID());
        packet.getIntegers().write(1, 1);
        packet.getDoubles()
                .write(0, player.getLocation().getX())
                .write(1, player.getLocation().getY())
                .write(2, player.getLocation().getZ());
        packet.getIntegers().write(4, net.minecraft.world.level.block.Block.i(((CraftBlockData) material.createBlockData()).getState()));;

        fallingBlocks.put(player.getUniqueId(), id);

        ProtocolLibrary.getProtocolManager().broadcastServerPacket(packet);
    }

    public static void removeFakeFallingBlock(UUID player) {
        if (!fallingBlocks.containsKey(player)) return;

        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);

        packet.getIntLists().write(0, List.of(fallingBlocks.get(player)));

        fallingBlocks.remove(player);

        ProtocolLibrary.getProtocolManager().broadcastServerPacket(packet);
    }

    public static void moveFakeFallingBlock(int id, Location location) {
        PacketContainer teleportPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_TELEPORT);

        teleportPacket.getIntegers().write(0, id);
        teleportPacket.getDoubles()
                .write(0, location.getX())
                .write(1, location.getY())
                .write(2, location.getZ());

        PacketContainer velocityPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_VELOCITY);

        velocityPacket.getIntegers().write(0, id);
        velocityPacket.getIntegers().write(2, 0);

        ProtocolLibrary.getProtocolManager().broadcastServerPacket(teleportPacket);
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(velocityPacket);
    }

    public static void spawnBlock(Player player, Material material, HideAndSeek plugin) {
        Location location = player.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
        location.getBlock().setType(material);
        location.getBlock().setMetadata("hideAndSeek", new FixedMetadataValue(plugin, player.getUniqueId()));

        ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(location.subtract(0, 0.5, 0), EntityType.ARMOR_STAND);
        armorStand.addPassenger(player);

        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setSmall(true);
        armorStand.setMetadata("hideAndSeek", new FixedMetadataValue(plugin, true));

        blocks.put(player.getUniqueId(), armorStand);
    }

    public static void removeBlock(Player player, HideAndSeek plugin) {
        if (!blocks.containsKey(player.getUniqueId())) return;
        blocks.get(player.getUniqueId()).getLocation().add(0, 0.5, 0).getBlock().setType(Material.AIR);
        blocks.get(player.getUniqueId()).getLocation().add(0, 0.5, 0).getBlock().removeMetadata("hideAndSeek", plugin);
        blocks.get(player.getUniqueId()).remove();
        blocks.remove(player.getUniqueId());
        player.teleport(player.getLocation().add(0, 0.5, 0));
    }
}
