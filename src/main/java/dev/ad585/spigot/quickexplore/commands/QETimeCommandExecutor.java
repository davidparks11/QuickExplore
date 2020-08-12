package dev.ad585.spigot.quickexplore.commands;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.ad585.spigot.quickexplore.QuickExplore;
import dev.ad585.spigot.quickexplore.dataModels.Explorer;
import net.md_5.bungee.api.ChatColor;

public class QETimeCommandExecutor implements CommandExecutor {

    private HashMap<UUID, Explorer> idExplorerMap;
    private static final String PERMISSION = "explore.time";

    public QETimeCommandExecutor(QuickExplore plugin, HashMap<UUID, Explorer> idExplorerMap) {
        plugin.getCommand("qetime").setExecutor(this);
        this.idExplorerMap = idExplorerMap;
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
        if (!player.hasPermission(PERMISSION)) {
            player.sendMessage(ChatColor.RED + "You do not have the permission to execute this command!");
            return false;
        }

        Explorer  explorer = idExplorerMap.get(player.getUniqueId());
        if (explorer == null) {
            player.sendMessage("You can't call this command when you're not exploring!");
            return false;
        }

        explorer.sendPlayerTimeRemaining();
        return true;
    }

}