package me.cheesyfreezy.hexrpg.rpg.quests;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.exceptions.quests.QuestNPCNotFoundException;
import me.cheesyfreezy.hexrpg.rpg.quests.constants.QuestDifficulty;
import me.cheesyfreezy.hexrpg.rpg.quests.constants.QuestLength;
import me.cheesyfreezy.hexrpg.rpg.quests.npc.QuestNPC;
import me.cheesyfreezy.hexrpg.rpg.quests.reward.IQuestReward;
import me.cheesyfreezy.hexrpg.rpg.quests.reward.factory.QuestRewardFactory;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.QuestStep;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.factory.QuestStepFactory;
import me.cheesyfreezy.hexrpg.tools.EnumUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class QuestParser {
    @Inject private QuestRewardFactory questRewardFactory;
    @Inject private QuestStepFactory questStepFactory;
    @Inject private QuestService questService;

    /**
     * Parses a .json file into a {@link Quest} object
     * @param questFile The .json file to parse
     * @return A generated {@link Quest} object
     */
    public Quest parse(File questFile) {
        JSONParser parser = new JSONParser();

        try(FileReader reader = new FileReader(questFile)) {
            // General initialization
            JSONObject questJson = (JSONObject) parser.parse(reader);

            // Parsing meta deta
            int id = ((Long) questJson.get("id")).intValue();
            String name = (String) questJson.get("name");
            QuestDifficulty difficulty = EnumUtils.fromName(QuestDifficulty.class, (String) questJson.get("difficulty"));
            QuestLength length = EnumUtils.fromName(QuestLength.class, (String) questJson.get("length"));

            JSONArray questRequirementsArray = (JSONArray) questJson.get("quest-requirements");
            int[] questRequirementIds = new int[questRequirementsArray.size()];
            for(int i = 0; i < questRequirementIds.length; i++) {
                questRequirementIds[i] = (int) questRequirementsArray.get(i);
            }

            int startNPCId = ((Long) questJson.get("start-npc-id")).intValue();
            QuestNPC startNPC = null;
            try {
                startNPC = questService.getNPC(startNPCId);
            } catch (QuestNPCNotFoundException questNPCNotFoundException) {
                questNPCNotFoundException.printStackTrace();
            }

            // Parsing rewards
            JSONArray questRewardsArray = (JSONArray) questJson.get("rewards");
            IQuestReward[] questRewards = new IQuestReward[questRewardsArray.size()];

            for(int i = 0; i < questRewards.length; i++) {
                JSONObject rewardJson = (JSONObject) questRewardsArray.get(i);
                String rewardType = (String) rewardJson.get("type");
                JSONObject rewardData = (JSONObject) rewardJson.get("data");

                IQuestReward reward = questRewardFactory.map(rewardType, rewardData);
                questRewards[i] = reward;
            }

            // Parsing steps
            JSONArray questStepsArray = (JSONArray) questJson.get("steps");
            QuestStep[] questSteps = new QuestStep[questStepsArray.size()];

            for(int i = 0; i < questSteps.length; i++) {
                JSONObject stepJson = (JSONObject) questStepsArray.get(i);
                int stepId = ((Long) stepJson.get("id")).intValue();
                String stepType = (String) stepJson.get("type");
                JSONObject stepData = (JSONObject) stepJson.get("data");

                QuestStep step = questStepFactory.map(stepType, stepId, stepData);
                questSteps[i] = step;
            }

            // Building Quest
            return new Quest(id, name, difficulty, length, questRequirementIds, startNPC, questRewards, questSteps, questFile, questJson);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}