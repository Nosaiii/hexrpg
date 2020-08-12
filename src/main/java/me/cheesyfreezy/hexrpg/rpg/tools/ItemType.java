package me.cheesyfreezy.hexrpg.rpg.tools;

import org.bukkit.Material;

public class ItemType {
	public static boolean isWeapon(Material mat) {
		return 
				mat.equals(Material.WOODEN_AXE) ||
				mat.equals(Material.STONE_AXE) ||
				mat.equals(Material.GOLDEN_AXE) ||
				mat.equals(Material.IRON_AXE) ||
				mat.equals(Material.DIAMOND_AXE) ||
				mat.equals(Material.WOODEN_SWORD) ||
				mat.equals(Material.STONE_SWORD) ||
				mat.equals(Material.GOLDEN_SWORD) ||
				mat.equals(Material.IRON_SWORD) ||
				mat.equals(Material.DIAMOND_SWORD) ||
				mat.equals(Material.TRIDENT) ||
				mat.equals(Material.BOW) ||
				mat.equals(Material.CROSSBOW);
	}
	
	public static boolean isArmor(Material mat) {
		return 
				mat.equals(Material.LEATHER_HELMET) ||
				mat.equals(Material.LEATHER_CHESTPLATE) ||
				mat.equals(Material.LEATHER_LEGGINGS) ||
				mat.equals(Material.LEATHER_BOOTS) ||
				mat.equals(Material.CHAINMAIL_HELMET) ||
				mat.equals(Material.CHAINMAIL_CHESTPLATE) ||
				mat.equals(Material.CHAINMAIL_LEGGINGS) ||
				mat.equals(Material.CHAINMAIL_BOOTS) ||
				mat.equals(Material.IRON_HELMET) ||
				mat.equals(Material.IRON_CHESTPLATE) ||
				mat.equals(Material.IRON_LEGGINGS) ||
				mat.equals(Material.IRON_BOOTS) ||
				mat.equals(Material.DIAMOND_HELMET) ||
				mat.equals(Material.DIAMOND_CHESTPLATE) ||
				mat.equals(Material.DIAMOND_LEGGINGS) ||
				mat.equals(Material.DIAMOND_BOOTS) ||
				mat.equals(Material.GOLDEN_HELMET) ||
				mat.equals(Material.GOLDEN_CHESTPLATE) ||
				mat.equals(Material.GOLDEN_LEGGINGS) ||
				mat.equals(Material.GOLDEN_BOOTS) ||
				mat.equals(Material.TURTLE_HELMET) ||
				mat.equals(Material.SHIELD);
	}

	public static boolean isArrow(Material mat) {
		return
				mat.equals(Material.ARROW) ||
				mat.equals(Material.SPECTRAL_ARROW) ||
				mat.equals(Material.TIPPED_ARROW);
	}
}