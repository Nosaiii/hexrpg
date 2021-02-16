package me.cheesyfreezy.hexrpg.rpg.quests.reward;

import org.bukkit.entity.Player;

public interface IQuestReward {
    /**
     * Rewards the player with this object of {@link IQuestReward}
     * @param player The player to give the reward to
     */
    void reward(Player player);
    String getLabel();
}