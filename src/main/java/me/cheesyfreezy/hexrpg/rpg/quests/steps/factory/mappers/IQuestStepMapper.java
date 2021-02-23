package me.cheesyfreezy.hexrpg.rpg.quests.steps.factory.mappers;

import me.cheesyfreezy.hexrpg.rpg.quests.steps.QuestStep;
import org.json.simple.JSONObject;

public interface IQuestStepMapper {
    QuestStep map(int stepId, JSONObject jsonData) throws NullPointerException;
}