package me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.cheesyfreezy.hexrpg.rpg.mechanics.CustomInventory;

public abstract class PlayerMenuInventory extends CustomInventory {
	private String name;
	private int rows;

	private PlayerMenuOption[] playerMenuOptions;

	public PlayerMenuInventory(String name, int rows) {
		this.name = name;
		this.rows = rows;

		playerMenuOptions = new PlayerMenuOption[rows * 9];
	}

	protected abstract void buildContent();

	public String getName() {
		return name;
	}

	public int getRows() {
		return rows;
	}

	public void setOption(int slot, PlayerMenuOption pmo) {
		playerMenuOptions[slot] = pmo;
	}

	public PlayerMenuOption getOption(int slot) {
		return playerMenuOptions[slot];
	}

	public boolean hasOption(int slot) {
		try {
			return playerMenuOptions[slot] != null;
		} catch(IndexOutOfBoundsException e) {
			return false;
		}
	}

	public void fillEmpty() {
		ItemStack empty = getEmptyItem();

		for (int i = 0; i < rows * 9; i++) {
			if (playerMenuOptions[i] != null) {
				continue;
			}

			playerMenuOptions[i] = new PlayerMenuOption(empty);
		}
	}

	public void fillHorizontalLine(int yAxis, boolean override) {
		ItemStack empty = getEmptyItem();

		for (int i = yAxis * 9; yAxis <= yAxis * 9 + 9; yAxis++) {
			if (playerMenuOptions[i] != null && !override) {
				continue;
			}
			playerMenuOptions[i] = new PlayerMenuOption(empty);
		}
	}

	public void fillVerticalLine(int xAxis, boolean override) {
		ItemStack empty = getEmptyItem();
		
		for (int i = xAxis; i <= (rows - 1) * 9 + xAxis; i += 9) {
			if (playerMenuOptions[i] != null && !override) {
				continue;
			}
			playerMenuOptions[i] = new PlayerMenuOption(empty);
		}
	}

	private ItemStack getEmptyItem() {
		ItemStack empty = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta emptyMeta = empty.getItemMeta();
		emptyMeta.setDisplayName(ChatColor.BLACK + "[-]");
		empty.setItemMeta(emptyMeta);

		return empty;
	}

	public void open(Player... players) {
		buildContent();

		Inventory inv = Bukkit.getServer().createInventory(null, rows * 9, name);

		for (int i = 0; i < playerMenuOptions.length; i++) {
			PlayerMenuOption pmo = playerMenuOptions[i];

			if (pmo == null) {
				continue;
			}

			inv.setItem(i, pmo.getItem());
		}

		for (Player player : players) {
			player.openInventory(inv);
		}

		addToCache(inv, this);
	}
}