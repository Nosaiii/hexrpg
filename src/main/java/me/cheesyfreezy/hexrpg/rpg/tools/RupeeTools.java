package me.cheesyfreezy.hexrpg.rpg.tools;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.hexrpg.nbtapi.NBTItem;

public class RupeeTools {
	public static int getRupeesOfPlayer(Player player) {
		int rupees = 0;
		
		Inventory inv = player.getInventory();
		for (int i = 0; i < inv.getSize(); i++) {
			if(inv.getItem(i) == null || inv.getItem(i).getType().equals(Material.AIR)) {
				continue;
			}
			ItemStack item = inv.getItem(i);
			
			NBTItem nbtItem = new NBTItem(item);
			if(!nbtItem.hasKey("rpgdata_rupee")) {
				continue;
			}
			
			rupees += item.getAmount();
		}
		
		return rupees;
	}
	
	public static void removeRupeesFromPlayer(Player player, int amount) {
		Inventory inv = player.getInventory();
		for (int i = 0; i < inv.getSize(); i++) {
			if(inv.getItem(i) == null || inv.getItem(i).getType().equals(Material.AIR)) {
				continue;
			}
			ItemStack item = inv.getItem(i);
			
			NBTItem nbtItem = new NBTItem(item);
			if(!nbtItem.hasKey("rpgdata_rupee")) {
				continue;
			}
			
			if(item.getAmount() > amount) {
				item.setAmount(item.getAmount() - amount);
				amount = 0;
				
				break;
			} else {
				amount -= item.getAmount();
				inv.setItem(i, new ItemStack(Material.AIR));
			}
		}
	}

	public static boolean isRupee(ItemStack item) {
		NBTItem nbtItem = new NBTItem(item);
		return nbtItem.hasKey("rpgdata_rupee");
	}
}