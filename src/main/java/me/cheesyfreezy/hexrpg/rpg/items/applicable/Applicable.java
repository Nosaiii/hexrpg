package me.cheesyfreezy.hexrpg.rpg.items.applicable;

import com.google.inject.Inject;
import de.tr7zw.changeme.nbtapi.NBTItem;
import me.cheesyfreezy.hexrpg.rpg.items.RPGItem;
import me.cheesyfreezy.hexrpg.rpg.items.combatitem.RPGCombatItem;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public abstract class Applicable extends RPGItem {
	@Inject private ApplicableService applicableService;

	protected final ApplicableType type;
	protected final String subType;
	
	private final String itemName;
	protected final Material itemMaterial;
	protected final Byte data;
	private final ArrayList<String> baseLore;
	
	public Applicable(ApplicableType type, String subType, String itemName, Material itemMaterial, Byte data, ArrayList<String> baseLore) {
		this.type = type;
		this.subType = subType;
		
		this.itemName = itemName;
		this.itemMaterial = itemMaterial == null ? Material.AIR : itemMaterial;
		this.data = data == null ? null : data <= 0 ? null : data;
		this.baseLore = baseLore;
	}
	
	@Override
	public ItemStack update(ItemStack item) {
		if(item.getType() != itemMaterial) {
			item.setType(itemMaterial);
		}

		if(itemMaterial == Material.AIR) {
			return item;
		}

		if(data != null) {
			try {
				Method durabilitySetterMethod = ItemStack.class.getMethod("setDurability", short.class);
				durabilitySetterMethod.invoke(item, data);
			} catch(NoSuchMethodException e) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "An error occured while trying to set the durability of an applicable. If you are running newer versions of Spigot, make sure you have set 'data' to '0' in the effect_sockets.yml file");
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(itemName);
		meta.setLore(buildLore());
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		item.setItemMeta(meta);
		
		item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		
		NBTItem nbtItem = new NBTItem(item);
		nbtItem.setObject("rpgdata_" + getClass().getSimpleName().toLowerCase(), this);
		return nbtItem.getItem();
	}
	
	@SuppressWarnings("unchecked")
	public boolean canApply(RPGCombatItem targetItem) {
		return applicableService.getCondition(this).test(targetItem);
	}
	
	public ApplicableResult apply(RPGCombatItem targetItem, ItemStack item) {
		if(!canApply(targetItem)) {
			return ApplicableResult.FAILED;
		}
		
		return childApply(targetItem, item);
	}
	protected abstract ApplicableResult childApply(RPGCombatItem targetItem, ItemStack item);
	public abstract ArrayList<String> buildLore();
	
	public ApplicableType getType() {
		return type;
	}
	
	public String getSubType() {
		return subType;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public ArrayList<String> getBaseLore() {
		return baseLore;
	}
	
	public String getRealName() {
		return WordUtils.capitalizeFully(type.toString().replace("_", " "));
	}
}