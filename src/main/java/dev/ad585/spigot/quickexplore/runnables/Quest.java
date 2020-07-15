package dev.ad585.spigot.quickexplore.runnables;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import java.util.UUID;
import java.util.HashMap;
import java.util.Iterator;

public class Quest implements Runnable {

    private final int taskTime = 20;
    private final JavaPlugin plugin;
    private int taskId = -1;
    private HashMap<UUID, Location> playerLocations;
    private HashMap<UUID, Long> countDowns;

    /**
     * 
     * @param plugin
     * @param pL     hashmap of players and locations to teleport them back
     * @param cD     hashmap of players and times they called the command to time
     *               them
     */
    public Quest(JavaPlugin plugin, HashMap<UUID, Location> pL, HashMap<UUID, Long> cD) {
        this.plugin = plugin;
        this.playerLocations = pL;
        this.countDowns = cD;
    }

    @Override
    public void run() {
        // self cancels task if no player remain in countdown map
        if (countDowns.size() < 1) {
            if (!cancelSelf()) {
                plugin.getServer().getLogger().severe(
                        "Couldn't cancel QuickExplore task, but schedule anyways. Try reloading the plugin, or restarting the server.");
            } else {
                taskId = -1;
            }
        } else {
            long currentTime = System.currentTimeMillis();
            Iterator<HashMap.Entry<UUID, Long>> cdIterator = countDowns.entrySet().iterator();
            while (cdIterator.hasNext()) {
                HashMap.Entry<UUID, Long> player = cdIterator.next();
                if (player.getValue() / 1000 + taskTime < currentTime / 1000) {
                    plugin.getServer().getPlayer(player.getKey())
                            .sendMessage("Sorry! You've run out of time. Going back!");
                    // only remove from maps if teleport if successful
                    if (plugin.getServer().getPlayer(player.getKey()).teleport(playerLocations.get(player.getKey()))) {
                        playerLocations.keySet().remove(player.getKey());
                        cdIterator.remove();
                    }
                }
            }
        }
    }

    /**
     * @param taskId task id of this task
     */
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    /**
     * Remove this task from scheduler. task id is considered not set if task id
     * equals -1.
     * 
     * @return true if task could self-cancel, false if not
     */
    public boolean cancelSelf() {
        if (taskId == -1)
            return false;
        plugin.getServer().getScheduler().cancelTask(taskId);
        return true;
    }

}