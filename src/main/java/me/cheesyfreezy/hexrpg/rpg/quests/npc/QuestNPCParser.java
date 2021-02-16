package me.cheesyfreezy.hexrpg.rpg.quests.npc;

import me.cheesyfreezy.hexrpg.rpg.quests.Quest;
import me.cheesyfreezy.hexrpg.tools.EnumUtils;
import org.bukkit.entity.Villager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class QuestNPCParser {
    /**
     * Parses a .json file into a {@link QuestNPC} array
     * @param questNpcFile The .json file to parse
     * @return A generated {@link Quest} array
     */
    public QuestNPC[] parse(File questNpcFile) {
        JSONParser parser = new JSONParser();

        try(FileReader reader = new FileReader(questNpcFile)) {
            // General initialization
            JSONObject questJson = (JSONObject) parser.parse(reader);

            // Individual NPC initialization
            JSONArray npcArray = (JSONArray) questJson.get("npcs");
            QuestNPC[] npcs = new QuestNPC[npcArray.size()];

            for(int i = 0; i < npcs.length; i++) {
                JSONObject npcJson = (JSONObject) npcArray.get(i);

                int id = ((Long) npcJson.get("id")).intValue();
                String name = (String) npcJson.get("name");
                Villager.Profession profession = EnumUtils.fromName(Villager.Profession.class, (String) npcJson.get("profession"));

                npcs[i] = new QuestNPC(id, name, profession);
            }

            return npcs;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return new QuestNPC[0];
    }
}