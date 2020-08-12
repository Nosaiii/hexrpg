package me.cheesyfreezy.hexrpg.rpg.items.applicable.scroll;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.cheesyfreezy.hexrpg.rpg.items.RPGAttributeType;
import me.cheesyfreezy.hexrpg.rpg.items.applicable.Applicable;
import me.cheesyfreezy.hexrpg.rpg.items.applicable.ApplicableResult;
import me.cheesyfreezy.hexrpg.rpg.items.applicable.ApplicableType;
import me.cheesyfreezy.hexrpg.rpg.items.combatitem.RPGCombatItem;
import me.cheesyfreezy.hexrpg.tools.RandomTools;

public class RPGScroll extends Applicable<String, RPGCombatItem> {
	private double successRate, destroyRate;
	private Map<RPGAttributeType, Object> modifiers;
	
	public RPGScroll(String scrollKey, double successRate, double destroyRate, String itemName, ArrayList<String> baseLore, Map<RPGAttributeType, Object> modifiers) {
		super(ApplicableType.SCROLL, scrollKey, itemName, Material.PAPER, null, baseLore);
		
		this.successRate = successRate;
		this.destroyRate = destroyRate;
		
		this.modifiers = modifiers;
		
		tmpItem = update(new ItemStack(itemMaterial));
	}
	
	@Override
	protected ApplicableResult childApply(RPGCombatItem targetItem, ItemStack item) {
		double r = RandomTools.getRandomPercentage();
		
		if(r < successRate) {
			targetItem.setAppliedUpgrades(targetItem.getAppliedUpgrades() + 1, item);
			
			for(Entry<RPGAttributeType, Object> modification : modifiers.entrySet()) {
				targetItem.modifyAttribute(modification.getKey(), modification.getValue(), true, item);
			}
			
			return ApplicableResult.SUCCEEDED;
		} else {
			r = RandomTools.getRandomPercentage();
			if(r < destroyRate) {
				item.setType(Material.AIR);
			}
			
			return ApplicableResult.FAILED;
		}
	}
	
	@Override
	public ArrayList<String> buildLore() {
		ArrayList<String> lore = new ArrayList<>();
		for(String line : getBaseLore()) lore.add(line);
		
		lore.add("");
		lore.add(ChatColor.GREEN + "✓ " + ChatColor.WHITE + successRate + "%");
		lore.add(ChatColor.RED + "✗ " + ChatColor.WHITE + destroyRate + "%");
		
		return lore;
	}
	
	/**
	 * @return The success rate
	 */
	public double getSuccessRate() {
		return successRate;
	}

	/**
	 * @param successRate The success rate to set
	 */
	public void setSuccessRate(double successRate) {
		this.successRate = successRate;
	}

	/**
	 * @return The destroy rate
	 */
	public double getDestroyRate() {
		return destroyRate;
	}

	/**
	 * @param destroyRate The destroy rate to set
	 */
	public void setDestroyRate(double destroyRate) {
		this.destroyRate = destroyRate;
	}
}