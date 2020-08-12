package dev.ad585.spigot.quickexplore;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import dev.ad585.spigot.quickexplore.commands.QEListCommandExecutor;
import dev.ad585.spigot.quickexplore.commands.QEQuitCommandExecutor;
import dev.ad585.spigot.quickexplore.commands.QETimeCommandExecutor;
import dev.ad585.spigot.quickexplore.commands.QuickExploreCommandExecutor;
import dev.ad585.spigot.quickexplore.dataModels.Explorer;
import dev.ad585.spigot.quickexplore.dataModels.Quest;
import dev.ad585.spigot.quickexplore.listeners.EntityDeathListener;
import net.md_5.bungee.api.ChatColor;

public class QuickExplore extends JavaPlugin {

    private static final String QUEST_CONFIG_PATH = "quests";
    private Quest[] quests;
    private HashMap<UUID, Explorer> idExplorerMap;

    @Override
    public void onEnable() {
        idExplorerMap = new HashMap<UUID, Explorer>();
        if (loadConfig()) {
            getLogger().info(ChatColor.GREEN + "" + quests.length + " quests found!");
            new QuickExploreCommandExecutor(this, quests, idExplorerMap);
            new QEListCommandExecutor(this, quests);
            new QEQuitCommandExecutor(this, idExplorerMap);
            new QETimeCommandExecutor(this, idExplorerMap);
            getServer().getPluginManager().registerEvents(new EntityDeathListener(idExplorerMap), this);
            getLogger().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            getLogger().info("Quick Explore version enabled!");
            getLogger().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        } else {
            // disable plugin if
            setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        idExplorerMap.forEach((id, explorer) -> {
            explorer.refund();
            explorer.sendHome();
        });

        getLogger().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        getLogger().info("Quick Explore version disabled!");
        getLogger().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    /**
     * loads any configuration items and returns whether or not the loading was
     * successful
     * 
     * @return true if configuration was loaded correctly, and false if something
     *         went wrong
     */
    private boolean loadConfig() {
        saveDefaultConfig();
        quests = loadQuests();
        return quests.length > 0;
    }

    /**
     * Reads list of strings from config.yml then parses those strings into Quests
     * objects. Any failed quests are logged.
     * 
     * @return array of quest objects that the player can choose
     */
    public Quest[] loadQuests() {
        List<String> questStrings = getConfig().getStringList(QUEST_CONFIG_PATH);

        ArrayList<Quest> questList = new ArrayList<Quest>(0);
        // Attempt to parse quest strings into quest objects
        questStrings.forEach((qString) -> {
            try {
                questList.add(new Quest(qString));
            } catch (IllegalArgumentException e) {
                // All errors gathered from quest constructor logged here
                getLogger().warning(ChatColor.RED + e.getMessage());
            }
        });

        int unparsedQuests = questStrings.size() - questList.size();
        if (unparsedQuests > 0) {
            getLogger().warning(ChatColor.RED + "Unable to parse " + unparsedQuests + " quests!!!");
        }

        Quest[] questArr = new Quest[questList.size()];
        return questList.toArray(questArr);
    }
}
