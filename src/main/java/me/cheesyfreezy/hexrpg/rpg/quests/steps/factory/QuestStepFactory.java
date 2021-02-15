package me.cheesyfreezy.hexrpg.rpg.quests.steps.factory;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.QuestStep;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.factory.mappers.IQuestStepMapper;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.factory.mappers.QuestDialogueStepMapper;
import org.json.simple.JSONObject;

import java.util.HashMap;

public class QuestStepFactory {
    @Inject private QuestDialogueStepMapper questDialogueStepMapper;

    private final HashMap<String, IQuestStepMapper> mappers = new HashMap<>();

    public QuestStepFactory() {
        mappers.put("talk_with_npc", questDialogueStepMapper);
    }

    public QuestStep map(String key, int stepId, JSONObject jsonData) {
        return mappers.get(key).map(stepId, jsonData);
    }
}