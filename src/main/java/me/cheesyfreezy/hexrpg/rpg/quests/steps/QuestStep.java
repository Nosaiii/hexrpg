package me.cheesyfreezy.hexrpg.rpg.quests.steps;

public abstract class QuestStep {
    private int id;

    public QuestStep(int id) {
        this.id = id;
    }

    public abstract void start();
    public abstract void finish();
}