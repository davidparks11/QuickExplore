package dev.ad585.spigot.quickexplore;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;
import dev.ad585.spigot.quickexplore.commands.QuickExploreCommandExecutor;
import dev.ad585.spigot.quickexplore.listeners.EntityDeathListener;

public class QuickExplore extends JavaPlugin {

    public final Logger logger = Logger.getLogger("Minecraft");
    private HashMap<UUID, Explorer> idExplorerMap;

    @Override
    public void onEnable() {
        idExplorerMap = new HashMap<UUID, Explorer>();
        new QuickExploreCommandExecutor(this, idExplorerMap);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(idExplorerMap), this);
        this.logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        this.logger.info("Quick Explore version enabled!");
        this.logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    @Override
    public void onDisable() {
        idExplorerMap.forEach((id, explorer) -> {
            explorer.refund();
            explorer.sendHome();
        });
        this.logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        this.logger.info("Quick Explore version disabled!");
        this.logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
}
