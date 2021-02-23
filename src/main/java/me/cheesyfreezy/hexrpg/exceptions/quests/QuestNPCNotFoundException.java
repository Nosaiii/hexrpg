package me.cheesyfreezy.hexrpg.exceptions.quests;

public class QuestNPCNotFoundException extends Exception {
    public QuestNPCNotFoundException(int id) {
        super("The npc with id '" + id + "' was not bound to the quest service");
    }
}