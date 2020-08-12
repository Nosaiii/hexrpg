package me.cheesyfreezy.hexrpg.rpg.mechanics.droptable;

import java.util.Arrays;

import me.cheesyfreezy.hexrpg.rpg.items.applicable.ApplicableType;
import me.cheesyfreezy.hexrpg.rpg.items.other.Backpack;
import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.cheesyfreezy.hexrpg.main.Plugin;
import me.cheesyfreezy.hexrpg.rpg.items.combatitem.RPGCombatItem;
import me.cheesyfreezy.hexrpg.rpg.items.other.Rupee;
import me.cheesyfreezy.hexrpg.tools.RandomTools;

public class DropTableItem {
	private double dropRate;
	private String itemKey;
	private int[] amount;
	
	public DropTableItem(double dropRate, String itemKey, int amount) {
		this(dropRate, itemKey, amount, amount);
	}
	
	public DropTableItem(double dropRate, String itemKey, int minAmount, int maxAmount) {
		this.dropRate = dropRate;
		this.itemKey = itemKey;
		this.amount = new int[] { minAmount, maxAmount };
	}

	public double getDropRate() {
		return dropRate;
	}

	public String getItemKey() {
		return itemKey;
	}

	public int[] getAmount() {
		return amount;
	}
	
	public ItemStack buildItem() {
		ItemStack item = null;

		ConfigFile scrollsConfig = ConfigFile.getConfig("scrolls.yml");
		ConfigFile effectSocketConfig = ConfigFile.getConfig("effect_sockets.yml");
		
		if(itemKey.equalsIgnoreCase("RUPEE")) {
			item = new Rupee().getTemporaryItem();
		} else if(itemKey.equalsIgnoreCase("BACKPACK")) {
			item = new Backpack(3).getTemporaryItem();
		} else if(scrollsConfig.getRootKeys().contains(itemKey) || effectSocketConfig.getRootKeys().contains(itemKey)) {
			ApplicableType type = scrollsConfig.getRootKeys().contains(itemKey) ? ApplicableType.SCROLL : ApplicableType.EFFECT_SOCKET;
			item = Plugin.getMain().getApplicableService().getReference(type, itemKey).getTemporaryItem();
		} else if(Arrays.asList(RPGCombatItem.getCollection()).contains(itemKey)) {
			item = RPGCombatItem.build(itemKey);
		} else {
			try {
				item = new ItemStack(Material.matchMaterial(itemKey));
			} catch(IllegalArgumentException e) {
				throw new IllegalArgumentException("An error occured trying to parse a drop table. The item '" + itemKey + "' does not exist!");
			}
		}
		
		item.setAmount((int) RandomTools.getRandomRange(Math.min(amount[0], amount[1]), Math.max(amount[0], amount[1])));
		
		return item;
	}
}