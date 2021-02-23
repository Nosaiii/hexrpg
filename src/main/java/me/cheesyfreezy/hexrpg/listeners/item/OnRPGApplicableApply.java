package me.cheesyfreezy.hexrpg.listeners.item;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.cheesyfreezy.hexrpg.rpg.items.applicable.Applicable;
import me.cheesyfreezy.hexrpg.rpg.items.applicable.ApplicableResult;
import me.cheesyfreezy.hexrpg.rpg.items.applicable.scroll.RPGScroll;
import me.cheesyfreezy.hexrpg.rpg.items.applicable.socketeffect.RPGEffectSocket;
import me.cheesyfreezy.hexrpg.rpg.items.combatitem.RPGCombatItem;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

public class OnRPGApplicableApply implements Listener {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@EventHandler
	public void onPrepareItemCraftEvent(PrepareItemCraftEvent event) {
		if(Arrays.stream(event.getInventory().getMatrix()).filter(Objects::nonNull).count() != 2) {
			return;
		}

		boolean invalid = false;
		RPGCombatItem presentRpgCombatItem = null;
		ItemStack presentRpgCombatItemStack = null;
		
		Applicable presentApplicable = null;
		
		for(ItemStack item : event.getInventory().getMatrix()) {
			if(item == null) {
				continue;
			}
			
			NBTItem nbtItem = new NBTItem(item);
			
			if(nbtItem.hasKey("rpgdata_combatitem")) {
				RPGCombatItem rpgCombatItemIngredient = nbtItem.getObject("rpgdata_combatitem", RPGCombatItem.class);
				if(Arrays.asList(RPGCombatItem.getCollection()).contains(rpgCombatItemIngredient.getItemKey())) {
					presentRpgCombatItem = rpgCombatItemIngredient;
					presentRpgCombatItemStack = item;
				}
			} else if(nbtItem.hasKey("rpgdata_rpgscroll")) {
				presentApplicable = nbtItem.getObject("rpgdata_rpgscroll", RPGScroll.class);
			} else if(nbtItem.hasKey("rpgdata_rpgeffectsocket")) {
				presentApplicable = nbtItem.getObject("rpgdata_rpgeffectsocket", RPGEffectSocket.class);
			} else {
				invalid = true;
				break;
			}
		}
		
		if(presentRpgCombatItem == null || presentApplicable == null) {
			return;
		}
		
		if(!presentApplicable.canApply(presentRpgCombatItem)) {
			return;
		}
		
		if(!invalid) {
			ItemStack presentRpgCombatItemStackClone = presentRpgCombatItemStack.clone();
			NBTItem rpgCombatItemResultNbt = new NBTItem(presentRpgCombatItemStackClone);
			RPGCombatItem rpgCombatItemResult = rpgCombatItemResultNbt.getObject("rpgdata_combatitem", RPGCombatItem.class);
			rpgCombatItemResult.setInCraftingState(true, presentRpgCombatItemStackClone);

			rpgCombatItemResultNbt = new NBTItem(rpgCombatItemResult.update(presentRpgCombatItemStackClone));
			rpgCombatItemResultNbt.setString("rpg_crafting_applicable_type", presentApplicable.getClass().getName());
			rpgCombatItemResultNbt.setObject("rpg_craftingapplicable", presentApplicable);

			event.getInventory().setResult(rpgCombatItemResultNbt.getItem());
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		
		if(!(event.getClickedInventory() instanceof CraftingInventory)) {
			return;
		}
		CraftingInventory ci = (CraftingInventory) event.getClickedInventory();
		
		if(event.getCursor().getType() != Material.AIR) {
			return;
		}
		
		if(ci.getResult() == null) {
			return;
		}
		ItemStack resultItemStack = ci.getResult();
		NBTItem nbtItemResult = new NBTItem(resultItemStack);
		
		if(nbtItemResult.hasKey("rpgdata_combatitem") && nbtItemResult.hasKey("rpg_craftingapplicable")) {
			if(nbtItemResult.getObject("rpgdata_combatitem", RPGCombatItem.class) == null) {
				return;
			}
			
			if(event.getRawSlot() != 0) {
				return;
			}
			
			RPGCombatItem combatItem = nbtItemResult.getObject("rpgdata_combatitem", RPGCombatItem.class);
			combatItem.setInCraftingState(false, resultItemStack);
			
			try {
				Applicable applicable = (Applicable) nbtItemResult.getObject("rpg_craftingapplicable", Class.forName(nbtItemResult.getString("rpg_crafting_applicable_type")));
				
				ApplicableResult result = applicable.apply(combatItem, resultItemStack);
				ci.setResult(combatItem.update(resultItemStack));
				
				switch(result) {
					case SUCCEEDED:
						player.sendMessage(LanguageManager.getMessage("applicables.successfully-applied", player.getUniqueId(), true, applicable.getRealName()));
						player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 1);
						
						break;
					case FAILED:
						player.sendMessage(LanguageManager.getMessage("applicables.failed-to-apply", player.getUniqueId(), true, applicable.getRealName()));
						player.playSound(player.getEyeLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 0.5f);
						
						break;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}