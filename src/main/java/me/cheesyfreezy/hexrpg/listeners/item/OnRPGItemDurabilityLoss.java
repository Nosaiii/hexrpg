package me.cheesyfreezy.hexrpg.listeners.item;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.cheesyfreezy.hexrpg.rpg.items.combatitem.RPGCombatItem;
import me.cheesyfreezy.hexrpg.rpg.tools.Feature;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import me.cheesyfreezy.hexrpg.tools.RandomTools;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class OnRPGItemDurabilityLoss implements Listener {
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(!Feature.getFeature("item-stats").isEnabled()) {
			return;
		}

		if(!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();
		
		ItemStack[] armorPieces = player.getInventory().getArmorContents();
		for(int i = 0; i < armorPieces.length; i++) {
			if(armorPieces[i] == null || armorPieces[i].getType().equals(Material.AIR)) {
				continue;
			}
			
			armorPieces[i] = applyDamageLoss(player, armorPieces[i]);
		}
		
		player.getInventory().setArmorContents(armorPieces);
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(!Feature.getFeature("item-stats").isEnabled()) {
			return;
		}

		if(!(event.getDamager() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getDamager();
		PlayerInventory inv = player.getInventory();

		inv.setItemInMainHand(applyDamageLoss(player, inv.getItemInMainHand()));
		inv.setItemInOffHand(applyDamageLoss(player, inv.getItemInOffHand()));
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(!Feature.getFeature("item-stats").isEnabled()) {
			return;
		}

		Player player = event.getPlayer();
		
		PlayerInventory inv = player.getInventory();

		ItemStack mainHandItem = inv.getItemInMainHand();
		if(!mainHandItem.getType().equals(Material.AIR)) {
			ItemStack damagedItem = applyDamageLoss(player, mainHandItem);
			inv.setItemInMainHand(damagedItem);
		}
	}
	
	private ItemStack applyDamageLoss(Player player, ItemStack item) {
		if(item == null || item.getType().equals(Material.AIR)) {
			return item;
		}

		NBTItem nbtItem = new NBTItem(item);
		if(!nbtItem.hasKey("rpgdata_combatitem")) {
			return item;
		}
		RPGCombatItem rpgItem = nbtItem.getObject("rpgdata_combatitem", RPGCombatItem.class);

		if(!rpgItem.isIdentified()) {
			return item;
		}

		double overhaulChance = rpgItem.getTotalOverhaul();
		double rOverhaulRoll = RandomTools.getRandomPercentage();
		if(rOverhaulRoll <= overhaulChance) {
			return item;
		}
		
		int[] durabilityLossRange = rpgItem.getDurabilityLoss();
		int durabilityLoss = 0;
		
		if(durabilityLossRange.length > 1) {
			durabilityLoss = (int) RandomTools.getRandomRange(durabilityLossRange[0], durabilityLossRange[1]);
		} else {
			durabilityLoss = durabilityLossRange[0];
		}
		
		rpgItem.setDurability(rpgItem.getDurability() - durabilityLoss, item);
		if(rpgItem.getDurability() == 0) {
			player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, 7, (float) 1.5);
			player.sendMessage(LanguageManager.getMessage("rpg-items.item-broke", player.getUniqueId(), true));
			
			item.setType(Material.AIR);
		} else {
			item = rpgItem.update(item);
		}
		
		return item;
	}
}