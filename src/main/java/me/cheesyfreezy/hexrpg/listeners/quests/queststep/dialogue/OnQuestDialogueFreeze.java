package me.cheesyfreezy.hexrpg.listeners.quests.queststep.dialogue;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class OnQuestDialogueFreeze implements Listener {
    private Player player;

    public OnQuestDialogueFreeze(Player player) {
        this.player = player;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if(this.player != player) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();

        int fromX = from.getBlockX();
        int fromZ = from.getBlockZ();
        int toX = to.getBlockX();
        int toZ = to.getBlockZ();

        if(fromX != toX || fromZ != toZ) {
            event.setCancelled(true);
        }
    }
}