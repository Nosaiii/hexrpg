package me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.processors;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.ChatProcessorService;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.cheesyfreezy.hexrpg.rpg.mechanics.playershop.PlayerShop;
import me.cheesyfreezy.hexrpg.rpg.tools.chatprocessor.IChatProcessor;
import me.cheesyfreezy.hexrpg.tools.PrimitiveTypeTools;

public class PlayerShopAddItemChatProcessor implements IChatProcessor {
	@Inject private HexRPGPlugin plugin;
	@Inject private ChatProcessorService chatProcessorService;

	private PlayerShop shop;
	private int itemSlot;
	
	public PlayerShopAddItemChatProcessor(PlayerShop shop, int itemSlot) {
		this.shop = shop;
		this.itemSlot = itemSlot;
	}
	
	@Override
	public void init(Player player) {
		player.sendMessage(LanguageManager.getMessage("chat-processors.player-shop-add-item.initialize", player.getUniqueId(), true));
		player.sendMessage(LanguageManager.getMessage("chat-processors.cancel-instructions", player.getUniqueId(), true));
	}

	@Override
	public void process(Player player, String message) {
		if(!PrimitiveTypeTools.isInt(message)) {
			player.sendMessage(LanguageManager.getMessage("chat-processors.player-shop-add-item.invalid-input", player.getUniqueId(), true));
			return;
		}
		int price = Integer.parseInt(message);
		
		ItemStack item = player.getInventory().getItem(itemSlot);
		if(item == null || item.getType().equals(Material.AIR)) {
			player.sendMessage(LanguageManager.getMessage("chat-processors.player-shop-add-item.no-item-found", player.getUniqueId(), true));
			return;
		}
		
		if(!shop.addSoldItem(item, price)) {
			player.sendMessage(LanguageManager.getMessage("chat-processors.player-shop-add-item.no-free-slots", player.getUniqueId(), true));
			return;
		}
		player.getInventory().setItem(itemSlot, new ItemStack(Material.AIR));
		
		Bukkit.getServer().getScheduler().runTask(plugin, () -> {
			shop.open(player, shop.isEditing());
		});

		chatProcessorService.removeStatus(player);
	}
}