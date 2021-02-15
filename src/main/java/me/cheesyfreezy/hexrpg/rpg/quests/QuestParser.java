package me.cheesyfreezy.hexrpg.rpg.quests;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.rpg.quests.constants.QuestDifficulty;
import me.cheesyfreezy.hexrpg.rpg.quests.constants.QuestLength;
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
            int id = (int) questJson.get("id");
            String name = (String) questJson.get("name");
            QuestDifficulty difficulty = EnumUtils.fromName(QuestDifficulty.class, (String) questJson.get("difficulty"));
            QuestLength length = EnumUtils.fromName(QuestLength.class, (String) questJson.get("length"));

            JSONArray questRequirementsArray = (JSONArray) questJson.get("quest-requirements");
            int[] questRequirementIds = new int[questRequirementsArray.size()];
            for(int i = 0; i < questRequirementIds.length; i++) {
                questRequirementIds[i] = (int) questRequirementsArray.get(i);
            }

            // Parsing rewards
            JSONArray questRewardsArray = (JSONArray) questJson.get("rewards");
            IQuestReward[] questRewards = new IQuestReward[questRewardsArray.size()];

            for(int i = 0; i < questRewards.length; i++) {
                JSONObject rewardJson = (JSONObject) questRequirementsArray.get(i);
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
                int stepId = (int) stepJson.get("id");
                String stepType = (String) stepJson.get("type");
                JSONObject stepData = (JSONObject) stepJson.get("data");

                QuestStep step = questStepFactory.map(stepType, stepId, stepData);
                questSteps[i] = step;
            }

            // Building Quest
            return new Quest(id, name, difficulty, length, questRequirementIds, questRewards, questSteps, questFile, questJson);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}