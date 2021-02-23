package me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue;

import me.cheesyfreezy.hexrpg.rpg.quests.npc.QuestNPC;

public abstract class QuestAbstractNPCDialogueStep extends QuestAbstractDialogueStep {
    private final QuestNPC npc;

    public QuestAbstractNPCDialogueStep(int stepId, QuestNPC npc) {
        super(stepId);

        this.npc = npc;
    }

    public QuestNPC getNpc() {
        return npc;
    }
}