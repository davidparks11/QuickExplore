package dev.ad585.spigot.quickexplore.commands;

import dev.ad585.spigot.quickexplore.QuickExplore;
import dev.ad585.spigot.quickexplore.runnables.ExplorerStatusChecker;
import dev.ad585.spigot.quickexplore.util.LocationUtil;
import dev.ad585.spigot.quickexplore.Explorer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import java.util.HashMap;
import java.util.UUID;

public class QuickExploreCommandExecutor implements CommandExecutor {

    private final double MAX_DISTANCE = 20000;
    private final double MIN_DISTANCE = 800;
    private static final int TICKS_PER_SECOND = 20;

    private QuickExplore plugin;
    private HashMap<UUID, Explorer> explorers;
    private int taskId = 0;
    private ExplorerStatusChecker statusChecker;

    public QuickExploreCommandExecutor(QuickExplore plugin, HashMap<UUID, Explorer> explorers) {
        this.plugin = plugin;
        plugin.getCommand("explore").setExecutor(this);
        this.explorers = explorers;
        statusChecker = new ExplorerStatusChecker(plugin, explorers);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // checks that caller is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command!");
            return false;
        }
        Player p = (Player) sender;

        // check permission
        if (!p.hasPermission("explore.use")) {
            p.sendMessage("You do not have the permission to execute this command!");
            return false;
        }

        // cancel command if player is currently exploring
        if (explorers.containsKey(p.getUniqueId())) {
            if (args.length == 1) {
                Explorer caller = explorers.get(p.getUniqueId());
                if (args[0].equals("quit")) {
                    if (caller.sendHome()) {
                        explorers.remove(caller.getUniqueId());
                        caller.refund();
                        caller.sendMessage("You've been refunded, sending you back!");
                        return true;
                    }
                } else if (args[0].equals("time")) {
                    caller.sendPlayerTimeRemaining();
                    return true;
                } else {
                    caller.sendMessage("Unknown arguments, try 'explore quit' or 'explore time'");
                    return false;
                }
            } else {
                p.sendMessage(ChatColor.RED
                        + "You are already exploring! Complete the current task or type \"/explore quit\"");
                return false;
            }
        }

        // collectPayment
        Explorer e = new Explorer(p);
        if (!e.collectPayment()) {
            e.sendMessage("QuickExplore failed! You must pay " + e.getFeeAmount() + " " + e.getFeeCurrency()
                    + " to go on a quest!");
            return false;
        }

        // get new location and check it's safety
        Location targetLocation = LocationUtil.getNextRandLocation(p.getLocation(), MIN_DISTANCE, MAX_DISTANCE);
        if (LocationUtil.isFloorDangerous(targetLocation) || !LocationUtil.inWorldBorder(targetLocation)) {
            e.sendMessage("Sorry! That location would've been dangerous. Try again!");
            return false;
        }

        // preload and teleport
        if (!LocationUtil.preLoadChunck(targetLocation)) {
            e.sendMessage("Couldn't preload location, try again!");
            return false;
        }

        if (!p.teleport(targetLocation)) {
            return false;
        }
        explorers.put(e.getUniqueId(), e);

        if (!p.getServer().getScheduler().isCurrentlyRunning(taskId)
                && !p.getServer().getScheduler().isQueued(taskId)) {
            taskId = p.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, statusChecker,
                    10 * TICKS_PER_SECOND, 10 * TICKS_PER_SECOND);
            statusChecker.setTaskId(taskId);
        }

        return true;
    }
}
