package me.cheesyfreezy.hexrpg.rpg.items.other;

import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.hexrpg.nbtapi.NBTItem;
import me.cheesyfreezy.hexrpg.rpg.items.RPGItem;

public class Rupee extends RPGItem {
	public Rupee() {
		tmpItem = update(new ItemStack(Material.EMERALD));
	}
	
	@Override
	public ItemStack update(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(ChatColor.GREEN + LanguageManager.getGlobalMessage("etc.rupee"));
		item.setItemMeta(meta);
		
		NBTItem nbtItem = new NBTItem(item);
		nbtItem.setObject("rpgdata_rupee", this);
		return nbtItem.getItem();
	}
}