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
    //private App plugin;
    private final double MAX_RANGE = 20000;
    private final double MIN_RANGE = 800;
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
        double nextX = Math.floor((Math.random()*(MAX_RANGE-MIN_RANGE)+MIN_RANGE));
        double nextZ = Math.floor((Math.random()*(MAX_RANGE-MIN_RANGE)+MIN_RANGE));
        location.setX(nextX);
        location.setZ(nextZ);
        double nextY = location.getWorld().getHighestBlockYAt(location);
        location.setY(nextY);
        return location;
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