package dev.ad585.spigot.quickexplore.commands;

import dev.ad585.spigot.quickexplore.App;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportPlayer implements CommandExecutor {
    private App plugin;
    
    public TeleportPlayer(App plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            
            return true;
        }

        return false;
    }
}