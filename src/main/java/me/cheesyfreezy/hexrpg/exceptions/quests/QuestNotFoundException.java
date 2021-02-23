package me.cheesyfreezy.hexrpg.exceptions.quests;

public class QuestNotFoundException extends Exception {
    public QuestNotFoundException(int id) {
        super("The quest with id '" + id + "' was not bound to the quest service");
    }
}