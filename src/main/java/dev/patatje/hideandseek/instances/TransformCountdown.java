package dev.patatje.hideandseek.instances;

import dev.patatje.hideandseek.HideAndSeek;
import dev.patatje.hideandseek.managers.ConfigManager;
import dev.patatje.hideandseek.utils.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TransformCountdown {

    private final HideAndSeek plugin;
    private final Arena arena;
    private final Player player;
    private final int countdownTime;
    private BukkitRunnable task;
    private boolean isRunning = false;

    public TransformCountdown(HideAndSeek plugin, Arena arena, Player player) {
        this.plugin = plugin;
        this.arena = arena;
        this.player = player;
        this.countdownTime = ConfigManager.getTransformTimer();
        start();
    }

    private void start() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                TransformCountdown.this.run();
            }
        };
        task.runTaskLater(plugin, countdownTime * 20L);
        isRunning = true;
    }

    private void run() {
        isRunning = false;
        Location location = player.getLocation();

        if(location.getBlock().getType() != Material.AIR) return;
        if(location.add(0, -1, 0).getBlock().getType() != Material.AIR) return;

        BlockUtils.spawnBlock(player, arena.getGame().getDisguise(player), plugin);

        BlockUtils.removeFakeFallingBlock(player.getUniqueId());
    }

    public void reset() {
        task.cancel();
        start();
    }

    public void cancel() {
        if(isRunning) {
            task.cancel();
        }
    }
}
