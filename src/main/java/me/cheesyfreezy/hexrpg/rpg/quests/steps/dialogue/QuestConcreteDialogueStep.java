package me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue;

import me.cheesyfreezy.hexrpg.listeners.quests.queststep.dialogue.OnQuestDialogueFreeze;
import me.cheesyfreezy.hexrpg.listeners.quests.queststep.dialogue.OnQuestDialogueInteractToTalk;
import me.cheesyfreezy.hexrpg.rpg.quests.npc.QuestNPC;
import org.bukkit.entity.Player;

public class QuestConcreteDialogueStep extends QuestAbstractNPCDialogueStep {

    private final QuestDialogue[] dialogue;

    public QuestConcreteDialogueStep(int id, QuestNPC npc, QuestDialogue[] dialogue) {
        super(id, npc);

        this.dialogue = dialogue;
    }

    @Override
    public void start(Player player) {
        registerListener(player.getUniqueId(), new OnQuestDialogueInteractToTalk(player, this));

        if(id == 0) {
            startDialogue(player);
        }
    }

    @Override
    public void finish(Player player) {
        unregisterListener(player.getUniqueId(), OnQuestDialogueFreeze.class);
    }

    public void startDialogue(Player player) {
        startDialogueRunnable(player, this, dialogue, i -> {}, () -> onNext(player));
    }
}
