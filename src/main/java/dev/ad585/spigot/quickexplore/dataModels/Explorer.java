package dev.ad585.spigot.quickexplore.dataModels;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * This class encapsulates a player who calls the explore command. Any
 * information needed to facilitate the command is stored in this object.
 */
public class Explorer {

    private Player player;
    private Quest quest;
    private long callTime;
    private Location callLocation;
    private boolean taskCompleted;

    /**
     * @param player player to store and pull values from
     */
    public Explorer(Player player, Quest quest) {
        this.player = player;
        this.quest = quest;
        callTime = System.currentTimeMillis();
        callLocation = player.getLocation().clone();
        taskCompleted = false;
    }

    /**
     * 
     * @return UUID of player stored in this explorer
     */
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    /**
     * Check to see if the current time is later than the call time plus the
     * explorers time limit
     * 
     * @return true if player has ran out of time false if not
     */
    public boolean isOutOfTime() {
        return getTimeRemaining() <= 0;
    }

    /**
     * sends message only visible to the explorer
     * 
     * @param message string to send to player
     */
    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    /**
     * returns the explorer to the location that they called the explore command
     * 
     * @return if the teleport was successful
     */
    public boolean sendHome() {
        return player.teleport(callLocation);
    }

    /**
     * gives player diamonds of p.rewardAmount
     */
    public void rewardPlayer() {
        giveExplorerItems(quest.getRewardCurrency(), quest.getRewardAmount());
    }

    /**
     * Gives the player what they paid to explore
     */
    public void refund() {
        giveExplorerItems(quest.getFeeCurrency(), quest.getFeeAmount());
    }

    /**
     * Gives the player a specific number of material. Drops the material on the
     * ground near player if they cannot take it.
     * 
     * @param m      material to give to player
     * @param amount number of material to give to player
     */
    private void giveExplorerItems(Material m, int amount) {
        ItemStack itemStack = new ItemStack(m, amount);
        HashMap<Integer, ItemStack> failedItems = player.getInventory().addItem(itemStack);
        if (!failedItems.isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
        }
    }

    /**
     * attempts to take payment from explorer
     * 
     * @return false if the payment cannot be collected from player
     */
    public boolean collectPayment() {
        ItemStack fee = new ItemStack(quest.getFeeCurrency(), quest.getFeeAmount());
        HashMap<Integer, ItemStack> failedItems = player.getInventory().removeItem(fee);
        return failedItems.isEmpty();
    }

    /**
     * @return the entity type that the explorer has to conquer to complete the
     *         quest
     */
    public EntityType getTarget() {
        return quest.getTarget();
    }

    /**
     * Sets taskCompleted to true
     */
    public void taskComplete() {
        taskCompleted = true;
    }

    /**
     * @return true if the task is complete
     */
    public boolean isTaskCompleted() {
        return taskCompleted;
    }

    /**
     * 
     * @return the number of time left in seconds. Cannot be negative
     */
    public int getTimeRemaining() {
        int timeRemaining = (int) (callTime / 1000 + quest.getTimeLimit() - System.currentTimeMillis() / 1000);
        if (timeRemaining < 0)
            return 0;
        return timeRemaining;
    }

    /**
     * Tells player the remaining minutes and seconds
     */
    public void sendPlayerTimeRemaining() {
        player.sendMessage("You have " + getTimeRemaining() / 60 + " minutes and " + getTimeRemaining() % 60
                + " seconds remaining.");
    }

    /**
     * Tells player how much and what they are being refunded
     */
    public void messageRefund() {
        player.sendMessage(
                "You've been refunded" + quest.getFeeAmount() + " " + quest.getFeeCurrency() + ". sending you back!");
    }

    /**
     * Tells player they do not have the require amount of material to go on quest
     */
    public void messagePaymentFailure() {
        player.sendMessage("QuickExplore failed! You must pay " + quest.getFeeAmount() + " " + quest.getFeeCurrency()
                + " to go on a quest!");
    }
}
