package me.cheesyfreezy.hexrpg.rpg.quests.steps.factory.mappers;

import me.cheesyfreezy.hexrpg.rpg.quests.steps.QuestStep;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.kill.QuestKillEntityStep;
import me.cheesyfreezy.hexrpg.tools.EnumUtils;
import org.bukkit.entity.EntityType;
import org.json.simple.JSONObject;

public class QuestKillEntityStepMapper implements IQuestStepMapper {
    @Override
    public QuestStep map(int stepId, JSONObject jsonData) {
        EntityType entityType = EnumUtils.fromName(EntityType.class, (String) jsonData.get("type"));
        int requiredKillCount = ((Long) jsonData.get("required-kills")).intValue();

        return new QuestKillEntityStep(stepId, entityType, requiredKillCount);
    }
}
