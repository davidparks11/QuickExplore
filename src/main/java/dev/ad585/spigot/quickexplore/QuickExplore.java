package dev.ad585.spigot.quickexplore;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;
import dev.ad585.spigot.quickexplore.commands.QuickExploreCommandExecutor;

public class QuickExplore extends JavaPlugin {

    public final Logger logger = Logger.getLogger("Minecraft");

    @Override
    public void onEnable() {
        new QuickExploreCommandExecutor(this);
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
