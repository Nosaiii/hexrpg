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

public class QuestDialogueStep extends QuestStep {
    @Inject private HexRPGPlugin plugin;

    private final QuestNPC npc;

    private final QuestDialogue[] dialogue;
    private int currentDialogueIndex;

    public QuestDialogueStep(int id, QuestNPC npc, QuestDialogue[] dialogue) {
        super(id);

        this.npc = npc;

        this.dialogue = dialogue;
        currentDialogueIndex = 0;
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
        unregisterListener(player.getUniqueId(), OnQuestDialogueInteractToTalk.class);
        registerListener(player.getUniqueId(), new OnQuestDialogueFreeze(player));

        AtomicInteger dialogueTimer = new AtomicInteger();

        new BukkitRunnable() {
            @Override
            public void run() {
                if(currentDialogueIndex > 0) {
                    double readingTime = dialogue[currentDialogueIndex - 1].getReadingTime(player.getUniqueId());

                    if (dialogueTimer.getAndIncrement() / 20d >= readingTime) {
                        dialogueTimer.set(0);

                        player.sendMessage(dialogue[currentDialogueIndex].getDialogue(player.getUniqueId()));
                        currentDialogueIndex++;

                        if(currentDialogueIndex == dialogue.length) {
                            cancel();
                            onNext(player);
                        }
                    }
                } else {
                    player.sendMessage(dialogue[0].getDialogue(player.getUniqueId()));
                    currentDialogueIndex++;
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    public QuestNPC getNpc() {
        return npc;
    }
}
