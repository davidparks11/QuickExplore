package dev.ad585.spigot.quickexplore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import dev.ad585.spigot.quickexplore.QuickExplore;
import dev.ad585.spigot.quickexplore.dataModels.Quest;
import net.md_5.bungee.api.ChatColor;

public class QEListCommandExecutor implements CommandExecutor {
    
    private Quest[] quests;
    private static final String PERMISSION = "explore.list";

    public QEListCommandExecutor(QuickExplore plugin, Quest[] quests) {
        plugin.getCommand("qelist").setExecutor(this);
        this.quests = quests;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // check permission
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(ChatColor.RED + "You do not have the permission to execute this command!");
            return false;
        }

        for (int i = 0; i < quests.length; i++)
            sender.sendMessage((i + 1) + ": " + quests[i].toString());
    
        return true;
    }

}