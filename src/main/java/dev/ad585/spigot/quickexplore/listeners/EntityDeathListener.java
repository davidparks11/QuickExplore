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
                Explorer e = explorers.get(p.getUniqueId());
                if (e.getTarget() == event.getEntityType()) {
                    if (!e.isOutOfTime()) {
                        e.sendMessage("Congrats you've completed your task! Sending you home soon!");
                        e.taskComplete();
                    }
                } else {
                    e.sendMessage("Thats the wrong mob type! You need to defeat a "
                            + e.getTarget().toString().toLowerCase() + " in order to recieve your reward!");
                }
            }
        }
    }
}