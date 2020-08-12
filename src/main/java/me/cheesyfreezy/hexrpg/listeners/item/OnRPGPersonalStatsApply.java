package me.cheesyfreezy.hexrpg.listeners.item;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.hexrpg.nbtapi.NBTItem;
import lowbrain.armorequip.ArmorEquipEvent;
import me.cheesyfreezy.hexrpg.rpg.items.combatitem.RPGCombatItem;

public class OnRPGPersonalStatsApply implements Listener {
	@EventHandler
	public void onArmorEquip(ArmorEquipEvent event) {
		Player player = event.getPlayer();
		
		double additionalHealth = 0;
		double agility = 0;
		
		if(event.getNewArmorPiece() != null && !event.getNewArmorPiece().getType().equals(Material.AIR)) {
			for(ItemStack armorPiece : player.getInventory().getArmorContents()) {
				if(armorPiece == null || !armorPiece.getType().equals(Material.AIR)) {
					continue;
				}
				
				NBTItem nbtArmorPiece = new NBTItem(armorPiece);
				
				if(!nbtArmorPiece.hasKey("rpgdata_combatitem")) {
					return;
				}
				RPGCombatItem combatItem = nbtArmorPiece.getObject("rpgdata_combatitem", RPGCombatItem.class);
				
				additionalHealth += combatItem.getTotalAdditionalHealth();
				agility += combatItem.getTotalAgility();
			}
		}
		
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue() + additionalHealth);
		
		double baseMovementSpeed = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getDefaultValue();
		player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(baseMovementSpeed + (baseMovementSpeed / 100 * agility));
	}
}