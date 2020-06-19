package dev.ad585.spigot.quickexplore.util;

import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.Material;

public final class LocationUtil {

    private LocationUtil() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and connot be instantiated");
    }

    // sets x and z to random location between min and max range
    public static Location getNextRandLocation(Location location, double minDistance, double maxDistance) {
        double nextX = location.getX() + getMinimumRandInRange(minDistance, maxDistance);
        double nextZ = location.getZ() + getMinimumRandInRange(minDistance, maxDistance);
        location.setX(nextX);
        location.setZ(nextZ);
        double nextY = location.getWorld().getHighestBlockYAt(location) + 1;
        location.setY(nextY);
        tidyLocation(location);
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
    public static double getMinimumRandInRange(double min, double max) {
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

    /**
     * 
     * @param location
     * edits the location to center on the x and z axis in the middle of a block
     */
    private static void tidyLocation(Location location) {
        double centerX = Math.floor(location.getX())+0.5;
        double centerZ = Math.floor(location.getZ())+0.5;
        location.setX(centerX);
        location.setZ(centerZ);
    }

    public static boolean preLoadChunck(Location location) {
        return location.getWorld().loadChunk((int) location.getX(), (int) location.getY(), true);
    }
    
    /**
     * 
     * @param location will check if this location is inside or outside the world border
     * @return true if inside, false if out
     */
    public static boolean inWorldBorder(Location location) {
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
    public static boolean isFloorDangerous(Location location) {
        //must check block underneath player for lava
        Location floor = location.clone().subtract(0,0,1);
        if (floor.getBlock().getType().equals(Material.LAVA)
                || floor.getBlock().getType().equals(Material.STATIONARY_LAVA)) {
            return true;
        }
        return false;
    }
}
