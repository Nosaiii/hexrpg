package me.cheesyfreezy.hexrpg.rpg.quests.steps.factory.mappers;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.exceptions.quests.QuestNPCNotFoundException;
import me.cheesyfreezy.hexrpg.rpg.quests.QuestService;
import me.cheesyfreezy.hexrpg.rpg.quests.npc.QuestNPC;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.QuestStep;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue.QuestAbstractDialogueStep;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue.QuestDialogue;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue.QuestOptionalDialogueStep;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue.RequiredItem;
import me.cheesyfreezy.hexrpg.tools.EnumUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class QuestOptionalDialogueStepMapper implements IQuestStepMapper {
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

        int takeItemsAt = ((Long) jsonData.get("take-items-at")).intValue();

        JSONArray requiredItemsArray = (JSONArray) jsonData.get("items");
        RequiredItem[] requiredItems = new RequiredItem[requiredItemsArray.size()];

        for(int i = 0; i < requiredItems.length; i++) {
            JSONObject requiredItemJson = (JSONObject) requiredItemsArray.get(i);

            Material material = EnumUtils.fromName(Material.class, (String) requiredItemJson.get("name"));
            int amount = ((Long) requiredItemJson.get("amount")).intValue();
            requiredItems[i] = new RequiredItem(material, amount);
        }

        List<QuestDialogue[]> dialogues = new ArrayList<>();
        for(String jsonDialogueKey : new String[] {"deny-dialogue", "accept-dialogue"}) {
            JSONArray dialogueArray = (JSONArray) jsonData.get(jsonDialogueKey);
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

            dialogues.add(dialogue);
        }

        return new QuestOptionalDialogueStep(stepId, interactNpc, takeItemsAt, dialogues.get(0), dialogues.get(1), requiredItems);
    }
}
