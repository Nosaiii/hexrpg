package me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.menus;

import java.util.Arrays;
import java.util.OptionalInt;

import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.cheesyfreezy.hexrpg.main.Plugin;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.PlayerMenuInventory;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.PlayerMenuOption;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.trading.TradingPlayer;

public class PlayerMenuTrade extends PlayerMenuInventory {
	public static final int[][] TRADING_SLOTS = new int[][] {
		{1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30, 36, 37, 38, 39, 45, 46, 47, 48},
		{5, 6, 7, 14, 15, 16, 17, 23, 24, 25, 26, 32, 33, 34, 35, 41, 42, 43, 44, 50, 51, 52, 53}
	};
	
	private TradingPlayer[] players;
	
	public PlayerMenuTrade(Player inviter, Player invitee) {
		super("⇄ (" + inviter.getName() + " - " + invitee.getName() + ")", 6);
		players = new TradingPlayer[] {
				new TradingPlayer(inviter),
				new TradingPlayer(invitee)
		};
	}

	@Override
	protected void buildContent() {
		int[] confirmTradeSlots = new int[] { 0, 8 };
		
		for(int i = 0; i < players.length; i++) {
			ItemStack confirmTrade = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
			ItemMeta confirmTradeMeta = confirmTrade.getItemMeta();
			confirmTradeMeta.setDisplayName(ChatColor.GREEN + "Accept trade");
			confirmTrade.setItemMeta(confirmTradeMeta);
			
			int iFinal = i;
			int slot = confirmTradeSlots[i];
			
			setOption(slot, new PlayerMenuOption(confirmTrade, (player) -> {
				players[iFinal].setAccepted(true);
				
				if(didBothAccept()) {
					Plugin.getMain().getPlayerTradingService().confirmTrade(players[0].getPlayer(), this);
				}
			}, (player) -> {
				return players[iFinal].getPlayer() == player;
			}));
		}
		
		fillVerticalLine(4, false);
	}
	
	public void addItem(Player player, ItemStack item, int slot) {
		Inventory inv = getTradingInventory(player);
		
		int playerIndex = players[0].getPlayer() == player ? 0 : 1;
		OptionalInt optionalSlot = Arrays.stream(TRADING_SLOTS[playerIndex]).filter(i -> inv.getItem(i) == null || inv.getItem(i).getType().equals(Material.AIR)).sorted().findFirst();
		
		if(!optionalSlot.isPresent()) {
			return;
		}
		
		inv.setItem(optionalSlot.getAsInt(), item);
		player.getInventory().setItem(slot, new ItemStack(Material.AIR));
	}
	
	public void removeItem(Player player, int slot) {
		Inventory inv = getTradingInventory(player);
		
		for(ItemStack excludedItem : player.getInventory().addItem(inv.getItem(slot)).values()) {
			player.getWorld().dropItem(player.getLocation(), excludedItem);
		}
		
		int playerIndex = players[0].getPlayer() == player ? 0 : 1;
		int[] rearrangeSlots = Arrays.stream(TRADING_SLOTS[playerIndex]).filter(i -> i >= slot && inv.getItem(i) != null && !inv.getItem(i).getType().equals(Material.AIR)).sorted().toArray();
		
		if(rearrangeSlots.length > 1) {
			for(int i=1;i<rearrangeSlots.length;i++) {
				int rearrangeSlot = rearrangeSlots[i];
				
				inv.setItem(rearrangeSlots[i - 1], inv.getItem(rearrangeSlot));
				inv.setItem(rearrangeSlot, new ItemStack(Material.AIR));
			}
		} else {
			player.getOpenInventory().getTopInventory().setItem(slot, new ItemStack(Material.AIR));
		}
	}
	
	private Inventory getTradingInventory(Player player) {
		return player.getOpenInventory().getTopInventory();
	}
	
	public boolean didBothAccept() {
		return Arrays.stream(players).allMatch(tp -> tp.hasAccepted());
	}
	
	public TradingPlayer[] getTradingPlayers() {
		return players;
	}
}