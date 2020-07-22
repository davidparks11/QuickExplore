package dev.ad585.spigot.quickexplore.runnables;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
import java.util.Iterator;
import dev.ad585.spigot.quickexplore.Explorer;

public class ExplorerStatusChecker implements Runnable {

    private final JavaPlugin plugin;
    private int taskId = -1;
    private HashMap<UUID, Explorer> idExplorerMap;

    public ExplorerStatusChecker(JavaPlugin plugin, HashMap<UUID, Explorer> idExplorerMap) {
        this.plugin = plugin;
        this.idExplorerMap = idExplorerMap;
    }

    /**
     * If explorers exist, then the task self cancels otherwise, checks for
     * explorers out of time, then teleports them back to call location
     */
    @Override
    public void run() {
        // self cancels task if no player remain in countdown map
        if (idExplorerMap.size() < 1) {
            if (!cancelSelf()) {
                plugin.getServer().getLogger().severe(
                        "Couldn't cancel QuickExplore task, but schedule anyways. Try reloading the plugin, or restarting the server.");
            } else {
                taskId = -1;
            }
        } else {
            Iterator<HashMap.Entry<UUID, Explorer>> explorerIterator = idExplorerMap.entrySet().iterator();
            while (explorerIterator.hasNext()) {
                Explorer player = explorerIterator.next().getValue();
                if (player.isOutOfTime() || player.isTaskCompleted()) {
                    // on successful teleport
                    if (player.sendHome()) {
                        if (player.isTaskCompleted()) {
                            player.sendMessage("You did it! Take this reward!");
                            player.rewardPlayer();
                        } else {
                            player.sendMessage("Sorry! You've run out of time. Going back!");
                        }
                        explorerIterator.remove();
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