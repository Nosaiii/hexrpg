package me.cheesyfreezy.hexrpg.rpg.quests.reward.factory;

import me.cheesyfreezy.hexrpg.rpg.quests.reward.IQuestReward;
import me.cheesyfreezy.hexrpg.rpg.quests.reward.factory.mappers.IQuestRewardMapper;
import me.cheesyfreezy.hexrpg.rpg.quests.reward.factory.mappers.QuestRewardItemMapper;
import org.json.simple.JSONObject;

import java.util.HashMap;

public class QuestRewardFactory {
    private final HashMap<String, IQuestRewardMapper> mappers = new HashMap<>();

    public QuestRewardFactory() {
        mappers.put("item", new QuestRewardItemMapper());
    }

    public IQuestReward map(String key, JSONObject jsonData) throws NullPointerException {
        return mappers.get(key).map(jsonData);
    }
}
