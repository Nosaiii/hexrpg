package me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.listeners.quests.queststep.dialogue.OnQuestDialogueAtLocation;
import me.cheesyfreezy.hexrpg.listeners.quests.queststep.dialogue.OnQuestDialogueInteract;
import me.cheesyfreezy.hexrpg.listeners.quests.queststep.dialogue.OnQuestDialogueInteractToTalk;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.QuestStep;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public abstract class QuestAbstractDialogueStep extends QuestStep {
    public static final ChatColor DEFAULT_PREFIX_COLOR = ChatColor.BLUE;

    @Inject private HexRPGPlugin plugin;

    private final HashMap<UUID, BukkitRunnable> dialogueRunnables;

    public QuestAbstractDialogueStep(int id) {
        super(id);

        dialogueRunnables = new HashMap<>();
    }

    @Override
    public void forceQuit(Player player) {
        BukkitRunnable dialogueRunnable = getDialogueRunnable(player);
        if(dialogueRunnable != null) {
            dialogueRunnable.cancel();
        }

        unregisterListener(player.getUniqueId(), OnQuestDialogueAtLocation.class);
    }

    public abstract void startDialogue(Player player);

    protected void startDialogueRunnable(Player player, QuestDialogue[] dialogue, Consumer<Integer> onIteration, Runnable onFinish) {
        unregisterListener(player.getUniqueId(), OnQuestDialogueInteractToTalk.class);
        unregisterListener(player.getUniqueId(), OnQuestDialogueAtLocation.class);
        unregisterListener(player.getUniqueId(), OnQuestDialogueInteract.class);

        AtomicInteger dialogueTimer = new AtomicInteger();

        BukkitRunnable dialogueRunnable = new BukkitRunnable() {
            private int currentDialogueIndex = 0;

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
        };

        dialogueRunnable.runTaskTimer(plugin, 0, 1);

        dialogueRunnables.put(player.getUniqueId(), dialogueRunnable);
    }

    public BukkitRunnable getDialogueRunnable(Player player) {
        UUID uuid = player.getUniqueId();

        if(!dialogueRunnables.containsKey(uuid)) {
            return null;
        }

        return dialogueRunnables.get(uuid);
    }
}
