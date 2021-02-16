package me.cheesyfreezy.hexrpg.listeners.quests.queststep.dialogue;

import de.tr7zw.changeme.nbtapi.NBTEntity;
import me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue.QuestDialogueStep;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class OnQuestDialogueInteractToTalk implements Listener {
    private final Player player;
    private final QuestDialogueStep dialogueStep;

    public OnQuestDialogueInteractToTalk(Player player, QuestDialogueStep dialogueStep) {
        this.player = player;
        this.dialogueStep = dialogueStep;
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();

        if(this.player != player) {
            return;
        }

        Entity entity = event.getRightClicked();
        NBTEntity nbtEntity = new NBTEntity(entity);

        if(!nbtEntity.getPersistentDataContainer().hasKey("quest_npc_id")) {
            return;
        }
        int questNpcId = nbtEntity.getPersistentDataContainer().getInteger("quest_npc_id");

        if(dialogueStep.getNpc().getId() != questNpcId) {
            return;
        }

        dialogueStep.startDialogue(player);
    }
}