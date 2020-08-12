package me.cheesyfreezy.hexrpg.rpg.tools;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.hexrpg.nbtapi.NBTItem;
import me.cheesyfreezy.hexrpg.rpg.items.combatitem.RPGCombatItem;
import me.cheesyfreezy.hexrpg.tools.RandomTools;

public class RPGCalculations {
	public static RPGDamage calculateDamage(RPGCombatItem[] items, Player target) {
		double damage = 0;
		boolean criticalStrike = false;
		
		double totalTenacity = 0;
		double totalCriticalRate = 0;
		double totalCriticalDamageMultiplier = 0;
		double tenacityRemoval = 0;
		double armorPenetrationRemoval = 0;
		
		for(RPGCombatItem item : items) {
			if(target != null && target instanceof Player) {
				damage += item.getTotalPvpDamage();
			} else {
				damage += item.getTotalPveDamage();
			}
			
			totalTenacity += item.getTotalTenacity();
			if(totalTenacity > 99) {
				totalTenacity = 99;
			}
			
			totalCriticalRate += item.getTotalCriticalRate();
			totalCriticalDamageMultiplier += item.getTotalCriticalDamageMultiplier();
			
			armorPenetrationRemoval = item.getTotalArmorPenetration();
		}
		
		if(RandomTools.getRandomPercentage() < totalCriticalRate) {
			damage *= totalCriticalDamageMultiplier;
			criticalStrike = true;
		}
		
		double targetArmor = 20;
		double targetDodgeChance = 0;
		if(target != null) {
			for(ItemStack armor : target.getInventory().getArmorContents()) {
				if(armor == null) {
					continue;
				}
				NBTItem nbtArmorItem = new NBTItem(armor);
				
				if(!nbtArmorItem.hasKey("rpgdata_combatitem")) {
					continue;
				}
				RPGCombatItem rpgArmorItem = nbtArmorItem.getObject("rpgdata_combatitem", RPGCombatItem.class);
				
				targetArmor += rpgArmorItem.getTotalArmor();
				targetDodgeChance += rpgArmorItem.getTotalDodgeChance();
			}
			
			tenacityRemoval = targetArmor / 100 * (100 - totalTenacity);
			
			targetArmor = tenacityRemoval + armorPenetrationRemoval;
			if(targetArmor < 1) {
				targetArmor = 1;
			}
		}
		
		damage -= damage / 100 * Math.log10(targetArmor / 2);
		
		if(damage < 1) {
			damage = 1;
		}
		
		boolean dodged = false;
		if(RandomTools.getRandomPercentage() <= targetDodgeChance) {
			dodged = true;
			damage = 0;
		}
		
		return new RPGDamage(damage, criticalStrike, dodged);
	}
	
	public static class RPGDamage {
		private double damage;
		private boolean criticalStrike;
		private boolean dodged;
		
		public RPGDamage(double damage, boolean criticalStrike, boolean dodged) {
			this.damage = damage;
			this.criticalStrike = criticalStrike;
			this.dodged = dodged;
		}
		
		public double getDamage() {
			return damage;
		}
		
		public boolean isCriticalStrike() {
			return criticalStrike;
		}
		
		public boolean isDodged() {
			return dodged;
		}
	}
}