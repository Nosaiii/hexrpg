package me.cheesyfreezy.hexrpg.rpg.quests;

import me.cheesyfreezy.hexrpg.exceptions.quests.InvalidQuestPlayerData;
import me.cheesyfreezy.hexrpg.exceptions.quests.QuestNotFoundException;
import me.cheesyfreezy.hexrpg.rpg.quests.constants.QuestDifficulty;
import me.cheesyfreezy.hexrpg.rpg.quests.constants.QuestLength;
import me.cheesyfreezy.hexrpg.rpg.quests.npc.QuestNPC;
import me.cheesyfreezy.hexrpg.rpg.quests.reward.IQuestReward;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.QuestStep;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class Quest {
    private final int id;
    private final String name;
    private final QuestDifficulty difficulty;
    private final QuestLength length;

    private final int[] questRequirementIds;
    private Quest[] questRequirements;

    private final QuestNPC startNPC;

    private final IQuestReward[] rewards;
    private final QuestStep[] steps;

    private final File file;
    private final JSONObject json;

    public Quest(int id, String name, QuestDifficulty difficulty, QuestLength length, int[] questRequirementIds, QuestNPC startNPC, IQuestReward[] rewards, QuestStep[] steps, File file, JSONObject json) {
        this.id = id;
        this.name = name;
        this.difficulty = difficulty;
        this.length = length;
        this.questRequirementIds = questRequirementIds;
        this.startNPC = startNPC;
        this.rewards = rewards;
        this.steps = steps;

        this.file = file;
        this.json = json;

        for (QuestStep step : steps) {
            step.subscribeObserver(this);
        }
    }

    /**
     * Populates the list of quest requirements with {@link Quest} objects
     * @param questService The quest service to retrieve the {@link Quest} objects from
     * @throws QuestNotFoundException Thrown when a specific quest was not found by id
     */
    public void validateQuestRequirements(QuestService questService) throws QuestNotFoundException {
        questRequirements = new Quest[questRequirementIds.length];

        for(int i = 0; i < questRequirements.length; i++) {
            questRequirements[i] = questService.getQuest(questRequirementIds[i]);
        }
    }

    /**
     * Starts the quest for the player
     * @param player The player to start the quest for
     */
    public void start(Player player) {
        JSONObject playerSpecificData = new JSONObject();

        //noinspection unchecked
        playerSpecificData.put("uuid", player.getUniqueId().toString());
        //noinspection unchecked
        playerSpecificData.put("step", 1);
        //noinspection unchecked
        playerSpecificData.put("finished", false);
        //noinspection unchecked
        playerSpecificData.put("rewards-received", false);

        JSONArray playerData = (JSONArray) json.get("player-data");
        //noinspection unchecked
        playerData.add(playerSpecificData);
        saveJson();

        callCurrentStep(player);
    }

    /**
     * Moves the player to the next step of the quest
     * @param player The player to move to the next step to
     */
    public void nextStep(Player player) {
        try {
            JSONObject playerSpecificData = getPlayerData(player.getUniqueId());

            int currentStep = (int) playerSpecificData.get("step");
            if(currentStep == steps.length) {
                finish(player.getUniqueId());
            } else {
                //noinspection unchecked
                playerSpecificData.put("step", currentStep + 1);
                saveJson();

                // Finish previous step
                QuestStep newStep = steps[currentStep - 2];
                newStep.finish(player);

                // Start new step
                callCurrentStep(player);
            }
        } catch (InvalidQuestPlayerData invalidQuestPlayerData) {
            invalidQuestPlayerData.printStackTrace();
        }
    }

    /**
     * Calls the start method on the current step of the player
     * @param player The player to start the step for
     */
    private void callCurrentStep(Player player) {
        try {
            JSONObject playerSpecificData = getPlayerData(player.getUniqueId());
            int currentStep = (int) playerSpecificData.get("step");

            QuestStep newStep = steps[currentStep - 1];
            newStep.start(player);
        } catch (InvalidQuestPlayerData invalidQuestPlayerData) {
            invalidQuestPlayerData.printStackTrace();
        }
    }

    /**
     * Finished the quest for the given player by UUID
     * @param uuid The UUID of the player to finish the quest for
     */
    public void finish(UUID uuid) {
        try {
            JSONObject playerSpecificData = getPlayerData(uuid);

            //noinspection unchecked
            playerSpecificData.put("finished", true);
            saveJson();

            OfflinePlayer offlinePlayer = Bukkit.getPlayer(uuid);
            if(offlinePlayer.isOnline()) {
                reward(offlinePlayer.getPlayer());
            }
        } catch (InvalidQuestPlayerData invalidQuestPlayerData) {
            invalidQuestPlayerData.printStackTrace();
        }
    }

    /**
     * Rewards the player with the rewards of this quest
     * @param player The player to give the rewards to
     */
    public void reward(Player player) {
        for(IQuestReward reward : rewards) {
            reward.reward(player);
        }

        try {
            JSONObject playerSpecificData = getPlayerData(player.getUniqueId());

            //noinspection unchecked
            playerSpecificData.put("rewards-received", true);
            saveJson();
        } catch (InvalidQuestPlayerData invalidQuestPlayerData) {
            invalidQuestPlayerData.printStackTrace();
        }
    }

    public boolean hasStarted(UUID uuid) {
        try {
            //noinspection unused
            JSONObject playerSpecificData = getPlayerData(uuid);
            return true;
        } catch (InvalidQuestPlayerData invalidQuestPlayerData) {
            return false;
        }
    }

    /**
     * Retrieves the data of a given player by UUID
     * @param uuid The UUID of the player to retrieve the data from
     * @return A {@link JSONObject} object containing the data of the player of this quest
     * @throws InvalidQuestPlayerData Thrown when the player data of the given player was invalid
     */
    private JSONObject getPlayerData(UUID uuid) throws InvalidQuestPlayerData {
        JSONArray playerData = (JSONArray) json.get("player-data");

        @SuppressWarnings("unchecked") // Supressing this statement is ok in this case
        Optional<JSONObject> playerSpecificDataOpt = playerData.stream()
                .filter(pd -> ((String) ((JSONObject) pd).get("uuid")).equalsIgnoreCase(uuid.toString()))
                .findFirst();

        if(!playerSpecificDataOpt.isPresent()) {
            throw new InvalidQuestPlayerData(this, uuid);
        }
        return playerSpecificDataOpt.get();
    }

    /**
     * Saves the objects json data to the .json file of the quest
     */
    private void saveJson() {
        try(FileWriter writer = new FileWriter(file)) {
            writer.write(json.toJSONString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The identifier of the quest
     * @return The identifier of the quest
     */
    public int getId() {
        return id;
    }

    /**
     * The name of the quest
     * @return The name of the quest
     */
    public String getName() {
        return name;
    }

    /**
     * The difficulty of the quest
     * @return The {@link QuestDifficulty} of the quest
     */
    public QuestDifficulty getDifficulty() {
        return difficulty;
    }

    /**
     * The length of the quest
     * @return The {@link QuestLength} of the quest
     */
    public QuestLength getLength() {
        return length;
    }

    /**
     * An array of quests that are required before starting this quest
     * @return An array of quests that are required before starting this quest
     */
    public Quest[] getQuestRequirements() {
        return questRequirements;
    }

    /**
     * The NPC that has to be interacted with to start this quest
     * @return The NPC that has to be interacted with to start this quest
     */
    public QuestNPC getStartNPC() {
        return startNPC;
    }

    /**
     * An array of rewards the player received upon completion
     * @return An array of {@link IQuestReward} objects
     */
    public IQuestReward[] getRewards() {
        return rewards;
    }

    /**
     * The JSON container containing all of the quest data
     * @return A {@link JSONObject} object containing all of the quest data
     */
    public JSONObject getJson() {
        return json;
    }
}