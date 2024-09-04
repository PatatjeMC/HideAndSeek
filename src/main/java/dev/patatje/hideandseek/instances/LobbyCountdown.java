package dev.patatje.hideandseek.instances;

import dev.patatje.hideandseek.HideAndSeek;
import dev.patatje.hideandseek.enums.GameState;
import dev.patatje.hideandseek.managers.ConfigManager;
import org.bukkit.scheduler.BukkitRunnable;

public class LobbyCountdown extends BukkitRunnable {

    private final HideAndSeek plugin;
    private final Arena arena;
    private int countdownTime;
    private boolean isRunning = false;

    public LobbyCountdown(HideAndSeek plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.countdownTime = ConfigManager.getLobbyTimer();
    }

    public void start() {
        arena.setState(GameState.COUNTDOWN);
        runTaskTimer(plugin, 0, 20);
        isRunning = true;
    }

    @Override
    public void run() {
        if (countdownTime == 0) {
            cancel();
            isRunning = false;
            arena.start();

            return;
        }

        if (countdownTime <= 5 || countdownTime % 10 == 0) {
            arena.sendMessage("&7De game gaat starten over &a" + countdownTime + "&7 seconde" + (countdownTime == 1 ? "!" : "n!"));
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
