package me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.stealing;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.IntStream;

import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerStealingService {
	private final HashMap<Player, StealProcess> stealing = new HashMap<>();
	
	public void steal(Player stealer, Player victim) {
		StealProcess sp = new StealProcess(stealer, victim, ConfigFile.getConfig("config.yml").getInteger("stealing.stealing-time"));
		
		sp.onSucceeded((lStealer, lVictim) -> {
			Inventory victimInv = lVictim.getInventory();
			
			int[] slots = IntStream.range(0, lVictim.getInventory().getSize()).filter(i -> victimInv.getItem(i) != null && !victimInv.getItem(i).getType().equals(Material.AIR)).toArray();
			
			if(slots.length == 0) {
				stealer.sendMessage(LanguageManager.getMessage("player-menu.stealing.no-items-found", stealer.getUniqueId(), true));
				return;
			}
			
			int slot = slots[new Random().nextInt(slots.length)];
			
			for(ItemStack excludedItem : stealer.getInventory().addItem(victimInv.getItem(slot)).values()) {
				stealer.getWorld().dropItem(stealer.getLocation(), excludedItem);
			}
			victimInv.setItem(slot, new ItemStack(Material.AIR));
			
			stealer.sendMessage(LanguageManager.getMessage("player-menu.stealing.item-stolen", stealer.getUniqueId(), true, victim.getName()));
			stealer.playSound(stealer.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5, 2);
			
			victim.sendMessage(LanguageManager.getMessage("player-menu.stealing.stolen-from", victim.getUniqueId(), true, stealer.getName()));
			victim.playSound(victim.getEyeLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 5, 1);
		});
		
		sp.onFailed((lStealer, lVictim) -> {
			lStealer.sendMessage(LanguageManager.getMessage("player-menu.stealing.failed-to-steal", lStealer.getUniqueId(), true));
		});
		
		sp.onStop((lStealer, lVictim) -> {
			stealing.remove(stealer);
		});
		
		stealing.put(stealer, sp);
		
		sp.start();
	}
	
	public boolean isStealing(Player player) {
		return stealing.containsKey(player);
	}
	
	public boolean isBeingStolenFrom(Player player) {
		return getStealerByVictim(player) != null;
	}
	
	public Player getStealerByVictim(Player victim) {
		for(Entry<Player, StealProcess> stealingPair : stealing.entrySet()) {
			if(stealingPair.getValue().getVictim() != victim) {
				continue;
			}
			
			return stealingPair.getKey();
		}
		
		return null;
	}
	
	public StealProcess getStealProcess(Player stealer) {
		return stealing.get(stealer);
	}
}