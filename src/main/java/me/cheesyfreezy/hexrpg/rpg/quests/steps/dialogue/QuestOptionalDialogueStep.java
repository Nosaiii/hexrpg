package me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue;

import me.cheesyfreezy.hexrpg.listeners.quests.queststep.dialogue.OnQuestDialogueFreeze;
import me.cheesyfreezy.hexrpg.listeners.quests.queststep.dialogue.OnQuestDialogueInteractToTalk;
import me.cheesyfreezy.hexrpg.rpg.quests.npc.QuestNPC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class QuestOptionalDialogueStep extends QuestAbstractNPCDialogueStep {
    private final int takeItemAt;
    private final QuestDialogue[] denyDialogue;
    private final QuestDialogue[] acceptDialogue;
    private final RequiredItem[] requiredItems;

    public QuestOptionalDialogueStep(int id, QuestNPC npc, int takeItemAt, QuestDialogue[] denyDialogue, QuestDialogue[] acceptDialogue, RequiredItem[] requiredItems) {
        super(id, npc);

        this.takeItemAt = takeItemAt;
        this.denyDialogue = denyDialogue;
        this.acceptDialogue = acceptDialogue;
        this.requiredItems = requiredItems;
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

    @Override
    public void startDialogue(Player player) {
        registerListener(player.getUniqueId(), new OnQuestDialogueFreeze(player));

        if(hasRequiredItems(player)) {
            Consumer<Integer> takeItemCheckConsumer = i -> {
                if(i + 1 != takeItemAt) {
                    return;
                }

                for(RequiredItem requiredItem : requiredItems) {
                    ItemStack itemToRemove = new ItemStack(requiredItem.getMaterial(), requiredItem.getAmount());
                    player.getInventory().removeItem(itemToRemove);
                }
            };

            startDialogueRunnable(player, acceptDialogue, takeItemCheckConsumer, () -> onNext(player));
        } else {
            startDialogueRunnable(player, denyDialogue, i -> {}, () -> {
                registerListener(player.getUniqueId(), new OnQuestDialogueInteractToTalk(player, this));
                unregisterListener(player.getUniqueId(), OnQuestDialogueFreeze.class);
            });
        }
    }

    public boolean hasRequiredItems(Player player) {
        for(RequiredItem requiredItem : requiredItems) {
            List<ItemStack> items = Arrays.stream(player.getInventory().getContents())
                    .filter(i -> i != null && !i.getType().equals(Material.AIR))
                    .collect(Collectors.toList());

            if(items.stream().noneMatch(requiredItem::matches)) {
                return false;
            }
        }

        return true;
    }
}
