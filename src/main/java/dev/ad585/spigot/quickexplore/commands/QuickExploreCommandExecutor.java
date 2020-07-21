package dev.ad585.spigot.quickexplore.commands;

import dev.ad585.spigot.quickexplore.QuickExplore;
import dev.ad585.spigot.quickexplore.runnables.Quest;
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

    private QuickExplore plugin;
    private final double MAX_DISTANCE = 20000;
    private final double MIN_DISTANCE = 800;
    private static final int TicksPerSecond = 20;
    private HashMap<UUID, Explorer> explorers;
    private int taskId = 0;
    private Quest quest;

    public QuickExploreCommandExecutor(QuickExplore plugin, HashMap<UUID, Explorer> explorers) {
        this.plugin = plugin;
        plugin.getCommand("explore").setExecutor(this);
        this.explorers = explorers;
        quest = new Quest(plugin, explorers);
    }

    // command method
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
            p.sendMessage(
                    ChatColor.RED + "You are already exploring! Complete the current task or type \"/explore quit\"");
            return false;
        }
        explorers.put(p.getUniqueId(), new Explorer(p));

        // get new location and check it's safety
        Location targetLocation = LocationUtil.getNextRandLocation(p.getLocation(), MIN_DISTANCE, MAX_DISTANCE);
        if (LocationUtil.isFloorDangerous(targetLocation) || !LocationUtil.inWorldBorder(targetLocation)) {
            p.sendMessage("Sorry! That location would've been dangerous. Try again!");
            return false;
        }

        // preload and teleport
        if (!LocationUtil.preLoadChunck(targetLocation)) {
            p.sendMessage("Couldn't preload location, try again!");
            explorers.remove(p.getUniqueId());
            return false;
        }

        p.teleport(targetLocation);
        if (!p.getServer().getScheduler().isCurrentlyRunning(taskId)
                && !p.getServer().getScheduler().isQueued(taskId)) {
            taskId = p.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, quest, 10 * TicksPerSecond,
                    10 * TicksPerSecond);
            quest.setTaskId(taskId);
        }

        return true;
    }
}
