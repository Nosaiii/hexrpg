package me.cheesyfreezy.hexrpg.rpg.quests.reward.factory.mappers;

import me.cheesyfreezy.hexrpg.rpg.quests.reward.IQuestReward;
import org.json.simple.JSONObject;

public interface IQuestRewardMapper {
    IQuestReward map(JSONObject jsonData);
}