package dev.ad585.spigot.quickexplore.commands;

import dev.ad585.spigot.quickexplore.Main;
import dev.ad585.spigot.quickexplore.runnables.Quest;
import dev.ad585.spigot.quickexplore.util.LocationUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import java.util.HashMap;
import java.util.UUID;

public class Exploration implements CommandExecutor {

    private Main plugin;
    private final double MAX_DISTANCE = 20000;
    private final double MIN_DISTANCE = 800;
    private static final int TicksPerSecond = 20;
    // private final long loadChunkDelay = 3;
    private HashMap<UUID, Location> playerLocations = new HashMap<UUID, Location>();

    public Exploration(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("explore").setExecutor(this);
    }

    // command method
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // checks that caller is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command!");
            return false;
        }
        Player p;
        try {
            p = (Player) sender;
        } catch (ClassCastException e) {
            sender.sendMessage(ChatColor.RED + "Failed Command. Report to Admin");
            return false;
        }

        // check permission
        if (!p.hasPermission("explore.use")) {
            p.sendMessage("You do not have the permission to execute this command!");
            return false;
        }

        if (playerLocations.containsKey(p.getUniqueId())) {
            p.sendMessage(
                    ChatColor.RED + "You are already exploring! Complete the current task or type \"/explore quit\"");
            return false;
        }
        playerLocations.put(p.getUniqueId(), p.getLocation());

        // get new location and check it's safety
        Location targetLocation = LocationUtil.getNextRandLocation(p.getLocation(), MIN_DISTANCE, MAX_DISTANCE);
        if (LocationUtil.isFloorDangerous(targetLocation) || !LocationUtil.inWorldBorder(targetLocation)) {
            p.sendMessage("Sorry! That location would've been dangerous. Try again!");
            return false;
        }

        // preload and teleport
        if (!LocationUtil.preLoadChunck(targetLocation)) {
            p.sendMessage("Couldn't preload location, try again!");
            return false;
        }
        p.teleport(targetLocation);

        Quest q = new Quest(plugin, p, playerLocations);
        // run timer as new async task
        p.getServer().getScheduler().runTaskAsynchronously(plugin, q);
        return true;
    }
}