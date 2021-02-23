package me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.trading;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.cheesyfreezy.hexrpg.rpg.mechanics.CustomInventory;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.menus.PlayerMenuTrade;

public class PlayerTradingService {
	private HashMap<Player, Player> trading = new HashMap<Player, Player>();
	
	public void invite(Player inviter, Player invitee, boolean notifyInviter, boolean notifyInvitee) {
		boolean inviteeHasBeenInvited = hasBeenInvited(invitee);
		Player inviteeInviter = getInviterByInvitee(invitee);
		
		removeInvitation(getInviterByInvitee(inviter), true, false);
		
		if(inviteeHasBeenInvited && inviteeInviter != inviter) {
			removeInvitation(getInviterByInvitee(invitee), true, false);
		}
		
		trading.put(inviter, invitee);
		
		if(notifyInviter && inviter != null) {
			inviter.sendMessage(LanguageManager.getMessage("player-menu.trading.request-sent", inviter.getUniqueId(), true, invitee.getName()));
		}
		
		if(notifyInvitee && invitee != null) {
			invitee.sendMessage(LanguageManager.getMessage("player-menu.trading.request-received", invitee.getUniqueId(), true, inviter.getName()));
		}
	}
	
	public void startTrade(Player inviter) {
		Player invitee = trading.get(inviter);
		
		PlayerMenuTrade pmt = new PlayerMenuTrade(inviter, invitee);
		pmt.open(invitee, inviter);
	}
	
	public void confirmTrade(Player inviter, PlayerMenuTrade pmt) {
		Player invitee = trading.get(inviter);

		dropExcludedItems(inviter, invitee, pmt);
		dropExcludedItems(invitee, inviter, pmt);

		closeTrade(inviter, pmt, false);
	}

	private void dropExcludedItems(Player from, Player to, PlayerMenuTrade pmt) {
		for(ItemStack excludedItem : from.getInventory().addItem(getItemsFromPlayer(to, pmt)).values()) {
			from.getWorld().dropItem(from.getLocation(), excludedItem);
		}
	}
	
	public void closeTrade(Player player, PlayerMenuTrade pmt, boolean forced) {
		Player inviter = null;
		Player invitee = null;
		if(hasBeenInvited(player)) {
			inviter = getInviterByInvitee(player);
			invitee = player;
		} else {
			inviter = player;
			invitee = trading.get(inviter);
		}
		
		removeInvitation(inviter, false, false);
		
		CustomInventory.removeFromCache(player.getOpenInventory().getTopInventory());
		
		if(forced) {
			for(ItemStack excludedItem : inviter.getInventory().addItem(getItemsFromPlayer(inviter, pmt)).values()) {
				inviter.getWorld().dropItem(inviter.getLocation(), excludedItem);
			}
			for(ItemStack excludedItem : invitee.getInventory().addItem(getItemsFromPlayer(invitee, pmt)).values()) {
				invitee.getWorld().dropItem(invitee.getLocation(), excludedItem);
			}
		}
		
		inviter.closeInventory();
		invitee.closeInventory();
	}
	
	private ItemStack[] getItemsFromPlayer(Player player, PlayerMenuTrade pmt) {
		int playerIndex = pmt.getTradingPlayers()[0].getPlayer() == player ? 0 : 1;
		
		Inventory inv = player.getOpenInventory().getTopInventory();
		List<ItemStack> itemsList = Arrays.stream(PlayerMenuTrade.TRADING_SLOTS[playerIndex]).filter(i -> inv.getItem(i) != null && !inv.getItem(i).getType().equals(Material.AIR)).boxed().map(i -> inv.getItem(i)).collect(Collectors.toList());
		return itemsList.toArray(new ItemStack[itemsList.size()]);
	}
	
	public boolean hasBeenInvited(Player player) {
		return trading.containsValue(player);
	}
	
	public boolean hasInvited(Player player) {
		return trading.containsKey(player);
	}
	
	public void removeInvitation(Player inviter, boolean notifyInviter, boolean notifyInvitee) {
		if(notifyInviter && inviter != null) {
			inviter.sendMessage(LanguageManager.getMessage("player-menu.trading.invitation-cancelled", inviter.getUniqueId(), true));
		}
		
		if(notifyInvitee && trading.get(inviter) != null) {
			trading.get(inviter).sendMessage(LanguageManager.getMessage("player-menu.trading.invitation-cancelled", trading.get(inviter).getUniqueId(), true));
		}
		
		trading.remove(inviter);
	}
	
	public Player getInviterByInvitee(Player invitee) {
		for(Entry<Player, Player> inviteEntry : trading.entrySet()) {
			if(!inviteEntry.getValue().equals(invitee)) {
				continue;
			}
			
			return inviteEntry.getKey();
		}
		
		return null;
	}
	
	public void closePendingTrades() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			Inventory inv = player.getOpenInventory().getTopInventory();
			if(inv == null) {
				continue;
			}

			if(CustomInventory.hasCache(inv)) {
				if(CustomInventory.getInventoryObject(inv) instanceof PlayerMenuTrade) {
					PlayerMenuTrade pmt = (PlayerMenuTrade) CustomInventory.getInventoryObject(inv);
					closeTrade(player, pmt, true);
				} else {
					player.closeInventory();
				}
			}
		}
	}
}