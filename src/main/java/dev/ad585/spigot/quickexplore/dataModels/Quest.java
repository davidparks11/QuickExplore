package dev.ad585.spigot.quickexplore.dataModels;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import net.md_5.bungee.api.ChatColor;

public class Quest {
    /**
     *
     */
    private static final boolean USE_LEGACY = false;
    private static final int MINIMUM_TIME_LIMIT = 30;
    private static final int MINIMUM_ITEM_AMOUNT = 1;
    private EntityType target;
    private int timeLimit;
    private boolean feeRequired;
    private Material feeCurrency;
    private int feeAmount;
    private Material rewardCurrency;
    private int rewardAmount;

    public Quest(EntityType target, int timeLimit, Material feeCurrency, int feeAmount, Material rewardCurrency,
            int rewardAmount) {
        this.target = target;

        this.timeLimit = timeLimit;
        this.feeCurrency = feeCurrency;
        this.feeAmount = feeAmount;
        this.rewardCurrency = rewardCurrency;
        this.rewardAmount = rewardAmount;
    }

    /**
     * Constructor used by configuration to read quests in from config.yml
     * 
     * @param questString string in config.yml that contains all info to create a
     *                    quest. The Order of questString's info matters greatly and
     *                    will fail if it does not look like the following:
     *                    "target_type,timelimit,fee_currency,fee_amount,reward_currency,reward_amount".
     *                    This means that if the string does not contain that
     *                    information in the order
     *                    "String,int,String,int,String,int" then that quest will
     *                    not be parsed.
     * @throws IllegalArgumentException if a number cannot be parsed or a
     *                                  material/entityType cannot be foundddddd
     */
    public Quest(String questString) throws IllegalArgumentException {
        // remove everything thats not a comma, underscore, letter, or number
        String filteredQuest = questString.replaceAll("[^a-zA-Z0-9_,]", "");
        String[] questInfo = filteredQuest.split(",");

        try {
            if (questInfo.length == 4) {
                feeRequired = false;
            } else if (questInfo.length == 6) {
                feeRequired = true;
            } else {
                throw new IllegalArgumentException("Info Length: " + questInfo.length
                        + ". quest string must contain 6 or 4 values to be considered valid. Only the Fee"
                        + "Currency and Fee Amount are optional when \"require-payment\" is set to \"false\"");
            }

            target = getEntity(questInfo[0].toUpperCase());
            if (target == null) {
                throw new IllegalArgumentException(
                        "Could not find entity in list, check to make sure your entity exists");
            }

            timeLimit = (int) Integer.valueOf(questInfo[1]);
            if (rewardAmount < MINIMUM_TIME_LIMIT) {
                throw new IllegalArgumentException(
                        "Invalid reward amount: " + rewardAmount + ". Reward amount must be a positive integer");
            }

            rewardCurrency = Material.matchMaterial(questInfo[2].toUpperCase(), USE_LEGACY);
            if (rewardCurrency == null) {
                throw new IllegalArgumentException("Could not find reward currency: " + questInfo[2]
                        + " in list, check to make sure your material exists");
            }
            rewardAmount = (int) Integer.valueOf(questInfo[3]);
            if (rewardAmount < MINIMUM_ITEM_AMOUNT) {
                throw new IllegalArgumentException(
                        "Invalid reward amount: " + rewardAmount + ". Reward amount must be a positive integer");
            }

            if (feeRequired) {
                feeCurrency = Material.matchMaterial(questInfo[4].toUpperCase(), USE_LEGACY);
                if (feeCurrency == null) {
                    throw new IllegalArgumentException("Could not find reward currency: " + questInfo[4]
                            + " in list, check to make sure your material exists");
                }
                feeAmount = (int) Integer.valueOf(questInfo[5]);
                if (feeAmount < MINIMUM_ITEM_AMOUNT) {
                    throw new IllegalArgumentException(
                            "Invalid fee amount: " + feeAmount + ". Fee amount must be a positive integer");
                }
            }

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid string representation for number in quest string: \""
                    + filteredQuest + "\"" + e.toString());
        }
    }

    /**
     * @return the entity type that the explorer has to conquer to complete the
     *         quest
     */
    public EntityType getTarget() {
        return target;
    }

    /**
     * @return returns the time allowed to complete this quest
     */
    public int getTimeLimit() {
        return timeLimit;
    }

    public Boolean getFeeRequired() {
        return feeRequired;
    }

    /**
     * @return the type of material it costs to explore this quest
     */
    public Material getFeeCurrency() {
        return feeCurrency;
    }

    /**
     * @return the amount of material it costs to explore this quest
     */
    public int getFeeAmount() {
        return feeAmount;
    }

    /**
     * @return the type of material the quest rewards
     */
    public Material getRewardCurrency() {
        return rewardCurrency;
    }

    /**
     * @return amount of material the quest rewards
     */
    public int getRewardAmount() {
        return rewardAmount;
    }

    /**
     * Loops through all entities and checks if the name matches that of the input
     * string. If found, the method returns the entity type with the matching name,
     * other wise it returns null.
     * 
     * @param entityName string of entity to find
     * @return entity type with matching name if found, null if not found
     */
    public EntityType getEntity(String entityName) {
        EntityType[] entities = EntityType.values();
        for (int i = 0; i < entities.length; i++) {
            if (entities[i].name().equals(entityName)) {
                return entities[i];
            }
        }
        return null;
    }

    @Override
    public String toString() {
        String str = "If you";
        if (feeRequired) {
            str += " pay " + ChatColor.RED + feeAmount + " " + feeCurrency.toString().toLowerCase() + "(s)"
                    + ChatColor.WHITE + " and";
        }
        str += " conquers a " + ChatColor.YELLOW + target.toString().toLowerCase();
        str += ChatColor.DARK_PURPLE + " in " + timeLimit + " seconds,";
        str += ChatColor.WHITE + " you will be rewarded with " + ChatColor.GREEN + rewardAmount;
        str += " " + rewardCurrency.toString().toLowerCase() + "(s)";
        return str;
    }
}