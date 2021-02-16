package me.cheesyfreezy.hexrpg.rpg.quests.steps.factory;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.QuestStep;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.factory.mappers.IQuestStepMapper;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.factory.mappers.QuestDialogueStepMapper;
import org.json.simple.JSONObject;

import java.util.HashMap;

public class QuestStepFactory {
    @Inject private HexRPGPlugin plugin;

    private final HashMap<String, IQuestStepMapper> mappers;

    @Inject
    public QuestStepFactory(QuestDialogueStepMapper questDialogueStepMapper) {
        mappers = new HashMap<>();
        mappers.put("talk_with_npc", questDialogueStepMapper);
    }

    public QuestStep map(String key, int stepId, JSONObject jsonData) {
        QuestStep step = mappers.get(key).map(stepId, jsonData);

        plugin.getDependencyInjector().injectMembers(step);

        return step;
    }
}