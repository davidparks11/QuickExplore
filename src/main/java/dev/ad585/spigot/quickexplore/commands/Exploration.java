package dev.ad585.spigot.quickexplore.commands;

import dev.ad585.spigot.quickexplore.Main;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;

public class Exploration implements CommandExecutor {
    // private App plugin;
    private final double MAX_DISTANCE = 20000;
    private final double MIN_DISTANCE = 800;

    public Exploration(Main plugin) {
        // this.plugin = plugin;
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
            Location targetLocation = getNextRandLocation(p.getLocation());
            if (isFloorDangerous(targetLocation) || !inWorldBorder(targetLocation)) {
                p.sendMessage("Sorry! That location would've been dangerous. Try again!");
                return false;
            }
            if (preLoadChunck(targetLocation)) {
            p.teleport(targetLocation);
                String message = "You've been sent to " + targetLocation.toString();
                p.sendMessage(message);
            return true;
            }
        } else {
            p.sendMessage("You do not have the permission to execute this command!");
        }

        return false;
    }

    // sets x and z to random location between min and max range
    private Location getNextRandLocation(Location location) {
        double nextX = location.getX() + getMinimumRandInRange(MIN_DISTANCE, MAX_DISTANCE);
        double nextZ = location.getZ() + getMinimumRandInRange(MIN_DISTANCE, MAX_DISTANCE);
        location.setX(nextX);
        location.setZ(nextZ);
        double nextY = location.getWorld().getHighestBlockYAt(location) + 1;
        location.setY(nextY);
        return location;
    }

    /**
     * 
     * @param min minimum distance from location
     * @param max maximum distance from location
     * @return value between min and max or -min and -max if give min=2 and max=5,
     *         the possible values are: 2<x<5 and -2>x>-5 Odd Cases: min is less
     *         then max: they will be switched min and max are equal: your minimum
     *         will be zero
     */
    private double getMinimumRandInRange(double min, double max) {
        // check odd cases
        if (min == max) {
            min = 0.0;
        } else if (min > max) {
            double temp = min;
            min = max;
            max = temp;
        }

        double scale = (Math.random() - 0.5) * 2;
        if (scale < 0) {
            return scale * (max - min) - min;
        } else {
            return scale * (max - min) + min;
        }
    }
    private boolean preLoadChunck(Location location) {
        return location.getWorld().loadChunk((int) location.getX(), (int) location.getY(), true);
    }

    /**
     * 
     * @param location will check if this location is inside or outside the world border
     * @return true if inside, false if out
     */
    private boolean inWorldBorder(Location location) {
        WorldBorder border = location.getWorld().getWorldBorder();
        double borderRadius = border.getSize() / 2;
        Location center = border.getCenter();
        return  center.distanceSquared(location) <= (borderRadius * borderRadius);
    }

    /**
     * 
     * @param floor
     * @return true if being on this block could hurt the player, false otherwise
     */
    private boolean isFloorDangerous(Location location) {
        //must check block underneath player for lava
        Location floor = location.clone().subtract(0,0,1);
        if (floor.getBlock().getType().equals(Material.LAVA)
                || floor.getBlock().getType().equals(Material.STATIONARY_LAVA)) {
            return true;
        }
        return false;
    }
}