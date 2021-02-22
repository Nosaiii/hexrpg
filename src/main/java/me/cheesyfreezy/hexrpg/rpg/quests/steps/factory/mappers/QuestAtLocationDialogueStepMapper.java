package me.cheesyfreezy.hexrpg.rpg.quests.steps.factory.mappers;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.exceptions.quests.QuestNPCNotFoundException;
import me.cheesyfreezy.hexrpg.rpg.quests.QuestService;
import me.cheesyfreezy.hexrpg.rpg.quests.npc.QuestNPC;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.QuestStep;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue.QuestAbstractDialogueStep;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue.QuestAtLocationDialogueStep;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue.QuestConcreteDialogueStep;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue.QuestDialogue;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.OptionalInt;

public class QuestAtLocationDialogueStepMapper implements IQuestStepMapper {
    @Inject private QuestService questService;

    @Override
    public QuestStep map(int stepId, JSONObject jsonData) {
        JSONObject locationJson = (JSONObject) jsonData.get("location");
        String locationWorld = (String) locationJson.get("world");
        int locationX = ((Long) locationJson.get("x")).intValue();
        int locationY = ((Long) locationJson.get("y")).intValue();
        int locationZ = ((Long) locationJson.get("z")).intValue();
        Location location = new Location(Bukkit.getWorld(locationWorld), locationX, locationY, locationZ);

        JSONArray dialogueArray = (JSONArray) jsonData.get("dialogue");
        QuestDialogue[] dialogue = new QuestDialogue[dialogueArray.size()];

        for(int i = 0; i < dialogue.length; i++) {
            JSONObject dialogueJson = (JSONObject) dialogueArray.get(i);

            ChatColor prefixColor = QuestAbstractDialogueStep.DEFAULT_PREFIX_COLOR;

            int speakerNpcId = ((Long) dialogueJson.get("speaker-npc-id")).intValue();
            String speakerName = null;
            if(speakerNpcId != -1) {
                try {
                    QuestNPC npc = questService.getNPC(speakerNpcId);
                    speakerName = npc.getName();
                    prefixColor = npc.getColor();
                } catch (QuestNPCNotFoundException e) {
                    e.printStackTrace();
                }
            }

            JSONObject texts = (JSONObject) dialogueJson.get("text");
            HashMap<String, String> localizedMessages = new HashMap<>();
            for(Object localization : texts.keySet()) {
                localizedMessages.put((String) localization, (String) texts.get(localization));
            }

            dialogue[i] = new QuestDialogue(speakerName, prefixColor, localizedMessages);
        }

        return new QuestAtLocationDialogueStep(stepId, dialogue, location);
    }
}
