package dev.patatje.hideandseek.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import dev.patatje.hideandseek.HideAndSeek;
import dev.patatje.hideandseek.enums.GameState;
import dev.patatje.hideandseek.instances.Arena;
import dev.patatje.hideandseek.utils.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class onHit extends PacketAdapter implements Listener {

    private final HideAndSeek plugin;

    public onHit(Plugin plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY);
        this.plugin = (HideAndSeek) plugin;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketContainer packet = event.getPacket();

        // Check if the packet is an attack packet
        if(!packet.getEnumEntityUseActions().read(0).getAction().equals(EnumWrappers.EntityUseAction.ATTACK)) return;

        // Check if the attacked entity is a tracked falling block
        if(!BlockUtils.fallingBlocks.containsValue(packet.getIntegers().read(0))) return;

        // Get the player that the falling block is attached to
        for(UUID uuid : BlockUtils.fallingBlocks.keySet()) {
            if(!BlockUtils.fallingBlocks.get(uuid).equals(packet.getIntegers().read(0))) continue;

            // Check if the player is not the attacker
            if(event.getPlayer().getUniqueId().equals(uuid)) return;

            // Modify the packet to attack the player instead of the falling block
            packet.getIntegers().write(0, Bukkit.getPlayer(uuid).getEntityId());
            return;
        }

    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player player) || !(event.getEntity() instanceof Player target)) return;

        Arena arena = plugin.getArenaManager().getArena(player);

        if(arena == null) return;

        if(!arena.getState().equals(GameState.INGAME)) {
            event.setCancelled(true);
            return;
        }

        if(!arena.getGame().getSeekers().contains(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if(!arena.getGame().getHiders().contains(target.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        event.setDamage(0);
        arena.getGame().damage(target, player);

        if(target.getVehicle() != null) {
            BlockUtils.removeBlock(target, plugin);
            BlockUtils.spawnFakeFallingBlock(target, plugin.getArenaManager().getArena(target).getGame().getDisguise(target));
        }
    }
}
