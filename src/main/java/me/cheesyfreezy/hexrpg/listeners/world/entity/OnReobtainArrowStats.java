package me.cheesyfreezy.hexrpg.listeners.world.entity;

import me.cheesyfreezy.hexrpg.rpg.items.combatitem.RPGCombatItem;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupArrowEvent;

public class OnReobtainArrowStats implements Listener {
    @EventHandler
    public void onEntityPickupItem(PlayerPickupArrowEvent event) {
        Arrow arrow = (Arrow) event.getArrow();

        if(!arrow.hasMetadata("rpgdata_combatarrow")) {
            return;
        }
        RPGCombatItem rpgItem = (RPGCombatItem) arrow.getMetadata("rpgdata_combatarrow").get(0).value();

        event.getItem().setItemStack(rpgItem.update(arrow.getItemStack()));
    }
}