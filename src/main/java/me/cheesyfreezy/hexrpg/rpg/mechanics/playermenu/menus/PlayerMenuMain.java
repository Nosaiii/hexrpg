package me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.menus;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.stealing.PlayerStealingService;
import me.cheesyfreezy.hexrpg.rpg.tools.Feature;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.PlayerMenuInventory;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.PlayerMenuOption;
import me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.trading.PlayerTradingService;

public class PlayerMenuMain extends PlayerMenuInventory {
	@Inject private PlayerTradingService playerTradingService;
	@Inject private PlayerStealingService playerStealingService;

	private Player player;
	private Player interactedPlayer;
	
	public PlayerMenuMain(Player player, Player interactedPlayer) {
		super(LanguageManager.getMessage("literal-translations.trade", player.getUniqueId()) + " (" + interactedPlayer.getName() + ")", 1);
		this.player = player;
		this.interactedPlayer = interactedPlayer;
	}

	@Override
	protected void buildContent() {
		ItemStack trade = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
		ItemStack steal = new ItemStack(Material.RED_STAINED_GLASS_PANE);

		ItemMeta tradeMeta = trade.getItemMeta();
		ItemMeta stealMeta = steal.getItemMeta();

		tradeMeta.setDisplayName(ChatColor.GREEN + LanguageManager.getMessage("literal-translations.trade", player.getUniqueId()));
		stealMeta.setDisplayName(ChatColor.RED + LanguageManager.getMessage("literal-translations.steal", player.getUniqueId()));
		
		tradeMeta.setLore(new ArrayList<>(Arrays.asList(LanguageManager.getMessage("player-menu.click-to-trade", player.getUniqueId(), true, interactedPlayer.getName()))));
		stealMeta.setLore(new ArrayList<>(Arrays.asList(LanguageManager.getMessage("player-menu.click-to-steal", player.getUniqueId(), true, interactedPlayer.getName()))));

		trade.setItemMeta(tradeMeta);
		steal.setItemMeta(stealMeta);

		if(Feature.getFeature("player-menu.trading").isEnabled()) {
			setOption(0, new PlayerMenuOption(trade, (player) -> {
				openTradeMenu();
			}));
		}
		if(Feature.getFeature("player-menu.stealing").isEnabled()) {
			setOption(1, new PlayerMenuOption(steal, (player) -> {
				startStealing();
			}));
		}

		fillEmpty();
	}
	
	private void openTradeMenu() {
		if(playerTradingService.hasBeenInvited(player)) {
			Player inviter = playerTradingService.getInviterByInvitee(player);
			playerTradingService.startTrade(inviter);
		} else {
			playerTradingService.invite(player, interactedPlayer, true, true);
			player.closeInventory();
		}
	}
	
	private void startStealing() {
		playerStealingService.steal(player, interactedPlayer);
		player.closeInventory();
	}
}