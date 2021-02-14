package me.cheesyfreezy.hexrpg.rpg.quests.reward.factory.mappers;

import me.cheesyfreezy.hexrpg.rpg.quests.reward.IQuestReward;
import me.cheesyfreezy.hexrpg.rpg.quests.reward.QuestRewardItem;
import org.json.simple.JSONObject;

public class QuestRewardItemMapper implements IQuestRewardMapper {
    @Override
    public IQuestReward map(JSONObject jsonData) {
        String materialName = (String) jsonData.get("name");
        int amount = (int) jsonData.get("amount");

        return new QuestRewardItem(materialName, amount);
    }
}