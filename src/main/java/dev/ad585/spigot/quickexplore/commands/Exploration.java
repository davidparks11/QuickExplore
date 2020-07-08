package dev.ad585.spigot.quickexplore.commands;

import dev.ad585.spigot.quickexplore.Main;
import dev.ad585.spigot.quickexplore.util.LocationUtil;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;

public class Exploration implements CommandExecutor {
    private Main plugin;
    private final double MAX_DISTANCE = 20000;
    private final double MIN_DISTANCE = 800;
    private int taskTime = 20;
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

        Player p = (Player) sender;

        // check permission
        if (p.hasPermission("explore.use")) {
            if (playerLocations.containsKey(p.getUniqueId())) {
                p.sendMessage(ChatColor.RED
                        + "You are already exploring! Complete the current task or type \"/explore quit\"");
                return false;
                // }
            } else {
                playerLocations.put(p.getUniqueId(), p.getLocation());
            }
            Location targetLocation = LocationUtil.getNextRandLocation(p.getLocation(), MIN_DISTANCE, MAX_DISTANCE);
            if (LocationUtil.isFloorDangerous(targetLocation) || !LocationUtil.inWorldBorder(targetLocation)) {
                p.sendMessage("Sorry! That location would've been dangerous. Try again!");
                return false;
            }
            if (LocationUtil.preLoadChunck(targetLocation)) {
                p.teleport(targetLocation);
                String message = "You've been sent to " + targetLocation.toString();
                p.sendMessage(message);
                p.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(taskTime * 1000);
                        } catch (InterruptedException e) {
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                p.sendMessage(ChatColor.RED
                                        + "Something went wrong! We're sending you back. Be sure to contact an Admin");
                            });
                        }
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                            p.teleport(playerLocations.get(p.getUniqueId()));
                        });
                    }
                });

                return true;
            }
        } else {
            p.sendMessage("You do not have the permission to execute this command!");
        }

        return false;
    }
}