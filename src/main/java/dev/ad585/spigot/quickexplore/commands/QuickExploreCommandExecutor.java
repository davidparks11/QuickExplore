package dev.ad585.spigot.quickexplore.commands;

import dev.ad585.spigot.quickexplore.QuickExplore;
import dev.ad585.spigot.quickexplore.runnables.ExplorerStatusChecker;
import dev.ad585.spigot.quickexplore.dataModels.Explorer;
import dev.ad585.spigot.quickexplore.dataModels.Quest;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.UUID;

public class QuickExploreCommandExecutor implements CommandExecutor {

    private static final int CHECK_INTERVAL = 10;
    private static final int TICKS_PER_SECOND = 20;
    private static final String[] USAGE = new String[] { "usage: qe <arg>", "arg options:",
            "<number> - chooses a quest to start", "list - lists available quests",
            "quit - returns explorer home, refunds payment if possible (while exploring)",
            "time - returns time remaining to complete current quest (while exploring)" };

    private QuickExplore plugin;
    private Quest[] quests;
    private HashMap<UUID, Explorer> explorers;
    private int taskId = 0;
    private ExplorerStatusChecker statusChecker;

    public QuickExploreCommandExecutor(QuickExplore plugin, Quest[] quests, HashMap<UUID, Explorer> explorers) {
        this.plugin = plugin;
        plugin.getCommand("explore").setExecutor(this);
        this.quests = quests;
        this.explorers = explorers;
        statusChecker = new ExplorerStatusChecker(plugin, explorers);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // checks that caller is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command!");
            return false;
        }
        Player player = (Player) sender;

        // check permission
        if (!player.hasPermission("explore.use")) {
            player.sendMessage(ChatColor.RED + "You do not have the permission to execute this command!");
            return false;
        }

        if (args.length == 1) {
            String arg = args[0];
            Explorer currentExplorer = explorers.get(player.getUniqueId());
            // explorers or player can call
            if (arg.equals("list")) {
                listQuests(player);
                return true;
            }
            // must be exploring to call these subcommands
            if (currentExplorer != null) {
                if (arg.equals("quit")) {
                    return cancelQuest(currentExplorer);
                } else if (arg.equals("time")) {
                    currentExplorer.sendPlayerTimeRemaining();
                    return true;
                }
            }

            // start new quest
            try {
                int questIndex = (int) Integer.valueOf(arg) - 1;

                // is player is exploring, do not send on quest
                if (currentExplorer != null) {
                    player.sendMessage(ChatColor.RED
                            + "You are already exploring! Complete the current task or type \"/explore quit\"");
                    return false;
                }

                Explorer explorer = new Explorer(plugin, player, quests[questIndex]);
                if (!explorer.sendOnQuest()) {
                    return false;
                }

                explorers.put(explorer.getUniqueId(), explorer);

                // start quest checker if it's not currently running
                scheduleExplorerStatusChecker(player.getServer().getScheduler());
                return true;
            } catch (ArrayIndexOutOfBoundsException e) {
                player.sendMessage(ChatColor.YELLOW
                        + "The number you entered exceeds the number of available quests! There are only "
                        + quests.length + " quests.");
                return false;
            } catch (NumberFormatException e) {
                // player entered no subcommands or numbers, invalid input
                player.sendMessage(
                        ChatColor.YELLOW + "Invalid subcommand. Certain subcommands can only be used while exploring");
                player.sendMessage(USAGE);
                return false;
            }
        } else

        {
            player.sendMessage(ChatColor.YELLOW + "Invalid amount of args");
            player.sendMessage(USAGE);
            return false;
        }
    }

    /**
     * 
     * @param player player to send message to
     */
    public void listQuests(Player player) {
        for (int i = 0; i < quests.length; i++)
            player.sendMessage((i + 1) + ": " + quests[i].toString());
    }

    private Boolean cancelQuest(Explorer explorer) {
        if (explorer.sendHome()) {
            explorers.remove(explorer.getUniqueId());
            explorer.refund();
            return true;
        }
        return false;
    }

    private void scheduleExplorerStatusChecker(BukkitScheduler scheduler) {
        if (!scheduler.isCurrentlyRunning(taskId) && !scheduler.isQueued(taskId)) {
            taskId = scheduler.scheduleSyncRepeatingTask(plugin, statusChecker, CHECK_INTERVAL * TICKS_PER_SECOND,
                    CHECK_INTERVAL * TICKS_PER_SECOND);
            statusChecker.setTaskId(taskId);
        }
    }
}
