package dev.ad585.spigot.quickexplore.listeners;

import dev.ad585.spigot.quickexplore.dataModels.Explorer;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import java.util.HashMap;
import java.util.UUID;

public class EntityDeathListener implements Listener {

    private HashMap<UUID, Explorer> explorers;

    public EntityDeathListener(HashMap<UUID, Explorer> explorers) {
        this.explorers = explorers;
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player p = event.getEntity().getKiller();
            // check if player is exploring
            if (explorers.containsKey(p.getUniqueId())) {
                p.sendMessage("Congrats you've completed your task! Sending you home now!");
                explorers.get(p.getUniqueId()).taskComplete();
            }
        }
    }
}