package dev.patatje.hideandseek.instances;

import dev.patatje.hideandseek.HideAndSeek;
import dev.patatje.hideandseek.enums.GameState;
import dev.patatje.hideandseek.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class GameTimer extends BukkitRunnable {

    private final HideAndSeek plugin;
    private final Arena arena;
    private int countdownTime;
    private BossBar bossBar;
    private boolean isRunning = false;

    public GameTimer(HideAndSeek plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.countdownTime = ConfigManager.getGameTimer();
        bossBar = Bukkit.createBossBar("Tijd over: " + countdownTime + " seconden", org.bukkit.boss.BarColor.GREEN, org.bukkit.boss.BarStyle.SOLID);
        bossBar.setProgress(1.0);
    }

    public void start() {
        runTaskTimer(plugin, 0, 20);
        isRunning = true;
        for(UUID uuid : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            bossBar.addPlayer(player);
        }
    }

    @Override
    public void run() {
        if (countdownTime == 0) {
            cancel();
            isRunning = false;
            arena.sendMessage("&aDe zoekers zijn er niet in geslaagd om alle verstoppers op tijd te vinden! De verstoppers hebben gewonnen!");
            arena.stop(true);

            return;
        }

        bossBar.setTitle("Tijd over: " + countdownTime + " seconde" + (countdownTime == 1 ? "" : "n"));
        bossBar.setProgress((double) countdownTime / ConfigManager.getGameTimer());

        if(countdownTime == ConfigManager.getGameTimer()/2) {
            bossBar.setColor(BarColor.YELLOW);
        } else if(countdownTime == ConfigManager.getGameTimer()/4) {
            bossBar.setColor(BarColor.RED);
        }

        if (countdownTime <= 5 || countdownTime % 30 == 0) {
            arena.sendMessage("&7De zoekers hebben nog &a" + countdownTime + "&7 seconde" + (countdownTime == 1 ? "!" : "n om de verstoppers te vinden!"));
        }

        countdownTime--;
    }

    @Override
    public void cancel() {
        if(isRunning) {
            super.cancel();
        }
        bossBar.removeAll();
    }
}
