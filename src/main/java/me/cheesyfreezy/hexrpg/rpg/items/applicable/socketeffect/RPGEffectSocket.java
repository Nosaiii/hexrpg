package me.cheesyfreezy.hexrpg.rpg.items.applicable.socketeffect;

import me.cheesyfreezy.hexrpg.rpg.items.applicable.Applicable;
import me.cheesyfreezy.hexrpg.rpg.items.applicable.ApplicableResult;
import me.cheesyfreezy.hexrpg.rpg.items.applicable.ApplicableType;
import me.cheesyfreezy.hexrpg.rpg.items.combatitem.RPGCombatItem;
import me.cheesyfreezy.hexrpg.rpg.mechanics.EffectSocketService;
import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class RPGEffectSocket extends Applicable {
	public RPGEffectSocket(String effectSocketKey, ArrayList<String> baseLore) throws NoSuchFieldError {
		super(
				ApplicableType.EFFECT_SOCKET,
				effectSocketKey,
				ChatColor.translateAlternateColorCodes('&', ConfigFile.getConfig(EffectSocketService.FILE_NAME).getString(effectSocketKey + ".appearance.display-name")),
				Material.matchMaterial(ConfigFile.getConfig(EffectSocketService.FILE_NAME).getString(effectSocketKey + ".appearance.display-item.name")),
				ConfigFile.getConfig(EffectSocketService.FILE_NAME).getByte(effectSocketKey + ".appearance.display-item.data"),
				baseLore);
		tmpItem = update(new ItemStack(itemMaterial));

		if(itemMaterial == Material.AIR) {
			throw new NoSuchFieldError();
		}
	}
	
	@Override
	protected ApplicableResult childApply(RPGCombatItem targetItem, ItemStack item) {
		targetItem.setEffectSocketKey(subType, item);
		return ApplicableResult.SUCCEEDED;
	}
	
	@Override
	public ArrayList<String> buildLore() {
		ArrayList<String> lore = new ArrayList<>();
		for(String line : getBaseLore()) lore.add(ChatColor.translateAlternateColorCodes('&', line));
		
		return lore;
	}
}