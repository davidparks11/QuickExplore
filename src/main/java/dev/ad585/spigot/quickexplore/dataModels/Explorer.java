package dev.ad585.spigot.quickexplore.dataModels;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import dev.ad585.spigot.quickexplore.util.LocationUtil;
import net.md_5.bungee.api.ChatColor;

/**
 * This class encapsulates a player who calls the explore command. Any
 * information needed to facilitate the command is stored in this object.
 */
public class Explorer {

    private static final String REFUND_TIME_LIMIT_PATH = "refund-time-limit";
    private static final String INFORM_PLAYER_PATH = "inform-player";
    private static final String MAX_DISTANCE_PATH = "max-distance";
    private static final String MIN_DISTANCE_PATH = "min-distance";
    private Player player;
    private Quest quest;
    private long callTime;
    private Location callLocation;
    private boolean taskCompleted;
    private int refundTime;
    private boolean inform;
    private int maxDistance;
    private int minDistance;

    /**
     * @param player player to store and pull values from
     */
    public Explorer(JavaPlugin plugin, Player player, Quest quest) {
        this.player = player;
        this.quest = quest;
        callTime = System.currentTimeMillis();
        callLocation = player.getLocation().clone();
        taskCompleted = false;

        refundTime = plugin.getConfig().getInt(REFUND_TIME_LIMIT_PATH, 30);
        inform = plugin.getConfig().getBoolean(INFORM_PLAYER_PATH, true);
        maxDistance = plugin.getConfig().getInt(MAX_DISTANCE_PATH, 15000);
        minDistance = plugin.getConfig().getInt(MIN_DISTANCE_PATH, 800);
        if (maxDistance < 0)
            maxDistance = 0;
        if (minDistance < 0)
            minDistance = 0;
    }

    /**
     * 
     * @return UUID of player stored in this explorer
     */
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    /**
     * 
     * 
     * @return false if send failed, true otherwise
     */
    public Boolean sendOnQuest() {
        // send quest
        sendMessage(quest.toString(), false);

        // skip teleport logic if rand location would equal current location
        if (maxDistance == 0 && minDistance == 0) {
            return collectPayment();
        }
        // get new location and check it's safety
        Location targetLocation = LocationUtil.getNextRandLocation(player.getLocation(), maxDistance, minDistance);
        if (LocationUtil.isFloorDangerous(targetLocation) || !LocationUtil.inWorldBorder(targetLocation)) {
            sendMessage(ChatColor.RED + "Sorry! That location would've been dangerous. Try again!", true);
            return false;
        }

        // preload and teleport
        if (!LocationUtil.preLoadChunck(targetLocation)) {
            sendMessage(ChatColor.RED + "Couldn't preload location, try again!", true);
            return false;
        }

        // collect payment
        if (!collectPayment()) {
            return false;
        }

        if (!player.teleport(targetLocation)) {
            refund();
            return false;
        }

        return true;
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
    public void sendMessage(String message, Boolean override) {
        if (inform || override)
            player.sendMessage(message);
    }

    /**
     * returns the explorer to the location that they called the explore command
     * 
     * @return if the teleport was successful
     */
    public boolean sendHome() {
        if (maxDistance != 0 || minDistance != 0)
            return player.teleport(callLocation);
        return true;
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
        if (refundTime > getTimeElapsed() && quest.getFeeRequired()) {
            messageRefund();
            giveExplorerItems(quest.getFeeCurrency(), quest.getFeeAmount());
        }
    }

    /**
     * Gives the player a specific number of material. Drops the material on the
     * ground near player if they cannot take it.
     * 
     * @param material material to give to player
     * @param amount   number of material to give to player
     */
    private void giveExplorerItems(Material material, int amount) {
        ItemStack itemStack = new ItemStack(material, amount);
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
    private boolean collectPayment() {
        if (quest.getFeeRequired()) {
            ItemStack fee = new ItemStack(quest.getFeeCurrency(), quest.getFeeAmount());
            HashMap<Integer, ItemStack> failedItems = player.getInventory().removeItem(fee);
            if (!failedItems.isEmpty()) {
                messagePaymentFailure();
                return false;
            }
        }
        return true;
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
     * 
     * @return time explorer has explored in seconds
     */
    private int getTimeElapsed() {
        return (int) (System.currentTimeMillis() / 1000 - callTime / 1000);
    }

    /**
     * Tells player the remaining minutes and seconds
     */
    public void sendPlayerTimeRemaining() {
        int time = getTimeRemaining();
        int questTime = quest.getTimeLimit();
        ChatColor color = ChatColor.GREEN;
        if (time < questTime / 3) {
            color = ChatColor.RED;
        } else if (time < questTime / 2) {
            color = ChatColor.YELLOW;
        }
        sendMessage(color + "You have " + secondToMinuteSecond(getTimeRemaining()) + " seconds remaining.", true);
    }

    /**
     * given a time in seconds, this returns a string "NUM_MINUTES minutes and
     * NUM_SECONDS seconds"
     * 
     * @param seconds
     * @return string containing minutes and seconds from param
     */
    private String secondToMinuteSecond(int seconds) {
        return seconds / 60 + " minutes and " + seconds % 60 + " seconds";
    }

    /**
     * Tells player how much and what they are being refunded
     */
    private void messageRefund() {
        sendMessage("You've been refunded " + ChatColor.GREEN + quest.getFeeAmount() + " " + quest.getFeeCurrency()
                + ChatColor.WHITE + ". sending you back!", true);
    }

    /**
     * Tells player they do not have the require amount of material to go on quest
     */
    private void messagePaymentFailure() {
        sendMessage(ChatColor.YELLOW + "Failed to begin quest! You must pay " + quest.getFeeAmount() + " "
                + quest.getFeeCurrency() + " to go on a quest!", true);
    }
}
