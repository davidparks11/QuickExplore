package dev.ad585.spigot.quickexplore;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;
import dev.ad585.spigot.quickexplore.commands.QuickExploreCommandExecutor;
import dev.ad585.spigot.quickexplore.listeners.EntityDeathListener;

public class QuickExplore extends JavaPlugin {

    public final Logger logger = Logger.getLogger("Minecraft");

    @Override
    public void onEnable() {
        HashMap<UUID, Explorer> explorers = new HashMap<UUID, Explorer>();
        new QuickExploreCommandExecutor(this, explorers);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(explorers), this);
        this.logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        this.logger.info("Quick Explore version enabled!");
        this.logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    @Override
    public void onDisable() {
        this.logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        this.logger.info("Quick Explore version disabled!");
        this.logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
}
