package me.cheesyfreezy.hexrpg.rpg.quests;

import me.cheesyfreezy.hexrpg.rpg.quests.constants.QuestDifficulty;
import me.cheesyfreezy.hexrpg.rpg.quests.constants.QuestLength;
import me.cheesyfreezy.hexrpg.rpg.quests.reward.IQuestReward;
import org.json.simple.JSONObject;

public class Quest {
    private int id;
    private String name;
    private QuestDifficulty difficulty;
    private QuestLength length;
    private int[] questRequirements;
    private IQuestReward[] questRewards;

    private JSONObject questJson;

    public Quest(int id, String name, QuestDifficulty difficulty, QuestLength length, int[] questRequirements, IQuestReward[] questRewards, JSONObject questJson) {
        this.id = id;
        this.name = name;
        this.difficulty = difficulty;
        this.length = length;
        this.questRequirements = questRequirements;
        this.questRewards = questRewards;

        this.questJson = questJson;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public QuestDifficulty getDifficulty() {
        return difficulty;
    }

    public QuestLength getLength() {
        return length;
    }

    public IQuestReward[] getQuestRewards() {
        return questRewards;
    }

    public JSONObject getQuestJson() {
        return questJson;
    }
}