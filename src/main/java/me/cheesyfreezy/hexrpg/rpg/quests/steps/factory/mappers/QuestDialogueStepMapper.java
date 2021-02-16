package me.cheesyfreezy.hexrpg.rpg.quests.steps.factory.mappers;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.exceptions.quests.QuestNPCNotFoundException;
import me.cheesyfreezy.hexrpg.rpg.quests.QuestService;
import me.cheesyfreezy.hexrpg.rpg.quests.npc.QuestNPC;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.QuestStep;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue.QuestDialogue;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue.QuestDialogueStep;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.OptionalInt;

public class QuestDialogueStepMapper implements IQuestStepMapper {
    @Inject private QuestService questService;

    @Override
    public QuestStep map(int stepId, JSONObject jsonData) {
        int npcId = ((Long) jsonData.get("npc-id")).intValue();
        QuestNPC interactNpc = null;
        try {
            interactNpc = questService.getNPC(npcId);
        } catch (QuestNPCNotFoundException e) {
            e.printStackTrace();
        }

        JSONArray dialogueArray = (JSONArray) jsonData.get("dialogue");
        QuestDialogue[] dialogue = new QuestDialogue[dialogueArray.size()];

        for(int i = 0; i < dialogue.length; i++) {
            JSONObject dialogueJson = (JSONObject) dialogueArray.get(i);

            int speakerNpcId = ((Long) dialogueJson.get("speaker-npc-id")).intValue();
            String speakerName = null;
            if(speakerNpcId != -1) {
                try {
                    QuestNPC npc = questService.getNPC(speakerNpcId);
                    speakerName = npc.getName();
                } catch (QuestNPCNotFoundException e) {
                    e.printStackTrace();
                }
            }

            JSONObject texts = (JSONObject) dialogueJson.get("text");
            HashMap<String, String> localizedMessages = new HashMap<>();
            for(Object localization : texts.keySet()) {
                localizedMessages.put((String) localization, (String) texts.get(localization));
            }

            dialogue[i] = new QuestDialogue(speakerName, localizedMessages);
        }

        OptionalInt longestNameLengthOptional = Arrays.stream(dialogue)
                .filter(d -> d.getSpeakerName() != null)
                .mapToInt(d -> d.getSpeakerName().length())
                .max();
        if(longestNameLengthOptional.isPresent()) {
            int longestNameLength = longestNameLengthOptional.getAsInt();

            for(QuestDialogue questDialogue : dialogue) {
                questDialogue.setSpeakerNamePadding(longestNameLength);
            }
        }

        return new QuestDialogueStep(stepId, interactNpc, dialogue);
    }
}
