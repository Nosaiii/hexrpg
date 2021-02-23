package me.cheesyfreezy.hexrpg.exceptions.quests;

import java.io.File;

public class InvalidQuestJsonData extends Exception {
    public InvalidQuestJsonData(File questFile) {
        super("Unable to parse quest. A value in the JSON file '" + questFile.getName() + "' was incorrect");
    }
}