package dev.patatje.hideandseek.instances;

import dev.patatje.hideandseek.HideAndSeek;
import dev.patatje.hideandseek.managers.ConfigManager;
import org.bukkit.scheduler.BukkitRunnable;

public class HeadstartCountdown extends BukkitRunnable {

    private final HideAndSeek plugin;
    private final Arena arena;
    private int countdownTime;
    private boolean isRunning = false;

    public HeadstartCountdown(HideAndSeek plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.countdownTime = ConfigManager.getHeadStartTimer();
    }

    public void start() {
        runTaskTimer(plugin, 0, 20);
        isRunning = true;
    }

    @Override
    public void run() {
        if (countdownTime == 0) {
            cancel();
            isRunning = false;
            arena.getGame().releaseSeekers();
            return;
        }

        if (countdownTime <= 5 || countdownTime % 10 == 0) {
            arena.sendMessage("&7De zoekers mogen zoeken over &a" + countdownTime + "&7 seconde" + (countdownTime == 1 ? "!" : "n!"));
        }

        countdownTime--;
    }

    @Override
    public void cancel() {
        if(isRunning) {
            super.cancel();
        }
    }
}
