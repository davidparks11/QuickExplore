package dev.ad585.spigot.quickexplore.runnables;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;

import java.util.UUID;
import java.util.HashMap;
import net.md_5.bungee.api.ChatColor;

public class Quest implements Runnable {

    private final int taskTime = 20;
    private final JavaPlugin plugin;
    private final Player p;
    private HashMap<UUID, Location> playerLocations;

    public Quest(JavaPlugin plugin, Player p, HashMap<UUID, Location> pL) {
        this.plugin = plugin;
        this.p = p;
        this.playerLocations = pL;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(taskTime * 1000);
        } catch (InterruptedException e) {
            // safe access to bucket api through sync task
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                p.sendMessage(
                        ChatColor.RED + "Something went wrong! We're sending you back. Be sure to contact an Admin");
            });
        }
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            p.teleport(playerLocations.get(p.getUniqueId()));
            playerLocations.remove(p.getUniqueId());
        });
    }
}