package dev.patatje.hideandseek.commands;

import dev.patatje.hideandseek.HideAndSeek;
import dev.patatje.hideandseek.enums.GameState;
import dev.patatje.hideandseek.instances.Arena;
import dev.patatje.hideandseek.managers.ConfigManager;
import dev.patatje.hideandseek.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HideAndSeekCommand implements CommandExecutor {
    private final HideAndSeek plugin;

    public HideAndSeekCommand(HideAndSeek plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player player)) {
            sender.sendMessage("Je moet een speler zijn om dit commando uit te voeren.");
            return true;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("list")) {
            player.sendMessage(ChatUtils.colorize("&aArena's:"));
            for(Arena arena : plugin.getArenaManager().getArenas().values()) {
                //TODO: make the arena's clickable
                player.sendMessage(ChatUtils.colorize("&7- &a" + arena.getName() + " &7- &f" + arena.getDescription()));
            }

        } else if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
            //TODO: make this a gui instead
            Arena arena = plugin.getArenaManager().getArena(args[1]);
            if(arena == null) {
                player.sendMessage(ChatUtils.colorize("&cDeze arena bestaat niet."));
                return true;
            }

            if(arena.getPlayers().size() >= ConfigManager.getMaxPlayers()) {
                player.sendMessage(ChatUtils.colorize("&cDeze arena is vol."));
                return true;
            }

            if(arena.getState() != GameState.LOBBY && arena.getState() != GameState.COUNTDOWN) {
                player.sendMessage(ChatUtils.colorize("&cDeze arena is al begonnen."));
                return true;
            }

            if(plugin.getArenaManager().getArena(player) != null) {
                player.sendMessage(ChatUtils.colorize("&cJe zit al in een arena."));
                return true;
            }

            arena.addPlayer(player);
            player.sendMessage(ChatUtils.colorize("&aJe bent toegevoegd aan de arena " + arena.getName() + "."));
        } else if(args.length == 1 && args[0].equalsIgnoreCase("leave")) {
            Arena arena = plugin.getArenaManager().getArena(player);
            if(arena == null) {
                player.sendMessage(ChatUtils.colorize("&cJe zit niet in een arena."));
                return true;
            }

            arena.removePlayer(player);
            player.sendMessage(ChatUtils.colorize("&aJe hebt de arena verlaten."));
        } else {
            player.sendMessage(ChatUtils.colorize("&aHide and Seek Commandos:"));
            player.sendMessage(ChatUtils.colorize("&7- &a/hideandseek list &7- &fKrijg een lijst van alle arena's."));
            player.sendMessage(ChatUtils.colorize("&7- &a/hideandseek join <arena> &7- &fNeem deel aan een arena."));
            player.sendMessage(ChatUtils.colorize("&7- &a/hideandseek leave &7- &fVerlaat de arena waar je in zit."));
        }

        return true;
    }
}
