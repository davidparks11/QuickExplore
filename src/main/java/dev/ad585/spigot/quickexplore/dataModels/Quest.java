package dev.ad585.spigot.quickexplore.dataModels;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class Quest {
    private EntityType target;
    private int timeLimit;
    private Material feeCurrency;
    private int feeAmount;
    private Material rewardCurrency;
    private int rewardAmount;

    public Quest(EntityType t, int tl, Material fc, int fa, Material rc, int ra) {
        target = t;
        timeLimit = tl;
        feeCurrency = fc;
        feeAmount = fa;
        rewardCurrency = rc;
        rewardAmount = ra;
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

}