package dev.ad585.spigot.quickexplore.dataModels;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * This class encapsulates a player who calls the explore command. Any
 * information needed to facilitate the command is stored in this object.
 */
public class Explorer {

    private Player player;
    private long callTime;
    private Location callLocation;
    private int timeLimit;
    private int rewardAmount;
    private int feeAmount;
    private Material rewardCurrency;
    private Material feeCurrency;
    private boolean taskCompleted;

    /**
     * @param player player to store and pull values from
     */
    public Explorer(Player player) {
        this.player = player;
        callTime = System.currentTimeMillis();
        callLocation = player.getLocation().clone();
        timeLimit = 30;
        rewardAmount = 4;
        feeAmount = 1;
        rewardCurrency = Material.DIAMOND;
        feeCurrency = Material.DIAMOND;
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
     * Sends the player a message with the remaining minutes and seconds
     */
    public void sendPlayerTimeRemaining() {
        player.sendMessage("You have " + getTimeRemaining() / 60 + " minutes and " + getTimeRemaining() % 60
                + " seconds remaining.");
    }

    /**
     * 
     * @return the number of time left in seconds
     */
    public int getTimeRemaining() {
        return (int) (callTime / 1000 + timeLimit - System.currentTimeMillis() / 1000);
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
        giveExplorerItems(rewardCurrency, rewardAmount);
    }

    /**
     * Gives the player what they paid to explore
     */
    public void refund() {
        giveExplorerItems(feeCurrency, feeAmount);
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
        ItemStack fee = new ItemStack(feeCurrency, feeAmount);
        HashMap<Integer, ItemStack> failedItems = player.getInventory().removeItem(fee);
        return failedItems.isEmpty();
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
     * @return amount player has to pay to explore
     */
    public int getFeeAmount() {
        return feeAmount;
    }

    /**
     * @return material type player has to pay to explore
     */
    public String getFeeCurrency() {
        return feeCurrency.toString();
    }
}
