package dev.ad585.spigot.quickexplore.commands;

import dev.ad585.spigot.quickexplore.Main;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.Material;

public class Exploration implements CommandExecutor {
    // private App plugin;
    private final double MAX_DISTANCE = 20000;
    private final double MIN_DISTANCE = 800;
    private final double WORLD_BORDER = 29999999;

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
            if (isDangerous(targetLocation) || !inWorldBorder(targetLocation.getX(), targetLocation.getZ())) {
                p.sendMessage("Sorry! That location would've been dangerous. Try again!");
                return false;
            }
            p.teleport(targetLocation);
            return true;
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
        double nextY = location.getWorld().getHighestBlockYAt(location);
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
    //validate x and z for world border
    private boolean isXZInBounds(double x, double z) {
        return (x < WORLD_BORDER && x > -WORLD_BORDER
        && z < WORLD_BORDER && z > -WORLD_BORDER);
    }

    // checks if the location is a lava
    private boolean isDangerous(Location location) {
        if (location.getBlock().getType().equals(Material.LAVA)
                || location.getBlock().getType().equals(Material.STATIONARY_LAVA)) {
            return true;
        }
        return false;
    }
}