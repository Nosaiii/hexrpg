package me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.listeners.quests.queststep.dialogue.OnQuestDialogueFreeze;
import me.cheesyfreezy.hexrpg.listeners.quests.queststep.dialogue.OnQuestDialogueInteractToTalk;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import me.cheesyfreezy.hexrpg.rpg.quests.npc.QuestNPC;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.QuestStep;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public abstract class QuestAbstractDialogueStep extends QuestStep {
    @Inject private HexRPGPlugin plugin;

    private final QuestNPC npc;

    private int currentDialogueIndex;

    public QuestAbstractDialogueStep(int id, QuestNPC npc) {
        super(id);

        this.npc = npc;

        currentDialogueIndex = 0;
    }

    public abstract void startDialogue(Player player);

    protected void startDialogueRunnable(Player player, QuestStep step, QuestDialogue[] dialogue, Consumer<Integer> onIteration, Runnable onFinish) {
        step.unregisterListener(player.getUniqueId(), OnQuestDialogueInteractToTalk.class);
        step.registerListener(player.getUniqueId(), new OnQuestDialogueFreeze(player));

        AtomicInteger dialogueTimer = new AtomicInteger();

        new BukkitRunnable() {
            @Override
            public void run() {
                if(currentDialogueIndex > 0) {
                    double readingTime = dialogue[currentDialogueIndex - 1].getReadingTime(player.getUniqueId());

                    if (dialogueTimer.getAndIncrement() / 20d >= readingTime) {
                        dialogueTimer.set(0);

                        player.sendMessage(dialogue[currentDialogueIndex].getDialogue(player.getUniqueId()));
                        onIteration.accept(currentDialogueIndex);
                        currentDialogueIndex++;

                        if(currentDialogueIndex == dialogue.length) {
                            cancel();
                            onFinish.run();
                            currentDialogueIndex = 0;
                        }
                    }
                } else {
                    player.sendMessage(dialogue[0].getDialogue(player.getUniqueId()));
                    onIteration.accept(currentDialogueIndex);
                    currentDialogueIndex++;
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    public QuestNPC getNpc() {
        return npc;
    }
}