package me.cheesyfreezy.hexrpg.exceptions.quests;

import me.cheesyfreezy.hexrpg.rpg.quests.Quest;

import java.util.UUID;

public class InvalidQuestPlayerData extends Exception {
    public InvalidQuestPlayerData(Quest quest, UUID uuid) {
        super("Invalid player data violation for player '" + uuid.toString() + "' for quest '" + quest.getName() + "'");
    }
}