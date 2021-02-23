package me.cheesyfreezy.hexrpg.rpg.items.combatitem;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.cheesyfreezy.hexrpg.rpg.items.RPGAttributeType;
import me.cheesyfreezy.hexrpg.rpg.items.RPGItem;
import me.cheesyfreezy.hexrpg.rpg.mechanics.EffectSocketService;
import me.cheesyfreezy.hexrpg.rpg.tools.ItemType;
import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import me.cheesyfreezy.hexrpg.tools.PrimitiveTypeTools;
import me.cheesyfreezy.hexrpg.tools.RandomTools;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class RPGCombatItem extends RPGItem implements Cloneable {
	public static final String FILE_NAME = "rpgitem.yml";

	public static String[] getCollection() {
		ConfigFile c = ConfigFile.getConfig(FILE_NAME);
		return c.getRootKeys().toArray(new String[c.getRootKeys().size()]);
	}

	private static final ChatColor COLOR_COMMON = ChatColor.WHITE;
	private static final ChatColor COLOR_DECENT = ChatColor.GREEN;
	private static final ChatColor COLOR_ORDINARY = ChatColor.AQUA;
	private static final ChatColor COLOR_RARE = ChatColor.LIGHT_PURPLE;
	private static final ChatColor COLOR_GRAND = ChatColor.GOLD;

	private static final ChatColor LORE_PRIMARY_COLOR = ChatColor.GRAY;
	private static final ChatColor LORE_SECONDARY_COLOR = ChatColor.YELLOW;

	private String itemKey;

	private String name;
	private RPGCombatItemTier tier;
	private Material material;

	private double pveDamage, pveDamageUpgrade;
	private double pvpDamage, pvpDamageUpgrade;
	private double criticalRate, criticalRateUpgrade;
	private double criticalDamageMultiplier, criticalDamageMultiplierUpgrade;

	private double additionalHealth, additionalHealthUpgrade;
	private double agility, agilityUpgrade;
	private double armor, armorUpgrade;
	private double dodgeChance, dodgeChanceUpgrade;

	private double tenacity, tenacityUpgrade;
	private double lifesteal, lifestealUpgrade;
	private double armorPenetration, armorPenetrationUpgrade;

	private double travelSpeed, travelSpeedUpgrade;
	private double flamableChance, flamableChanceUpgrade;

	private double overhaul, overhaulUpgrade;
	
	private int durability;
	private int maxDurability;
	private boolean hasDurability;
	
	private int appliedUpgrades;

	private boolean identified;
	private boolean inCraftingState;
	
	private String effectSocketKey;

	public static ItemStack build(String itemKey) {
		ConfigFile c = ConfigFile.getConfig(FILE_NAME);
		String cPath = itemKey + ".";
		
		return new RPGCombatItem(itemKey, c.getString(cPath + "name"),
				Material.matchMaterial(c.getString(cPath + "material")),
				c.getInteger(cPath + "durability"),
				false,
				false,
				0,
				"").getTemporaryItem();
	}

	private RPGCombatItem(String itemKey, String name, Material material, int durability, boolean identified, boolean inCraftingState, int appliedUpgrades, String effectSocketKey) {
		this.itemKey = itemKey;

		this.name = name;
		this.material = material;

		this.maxDurability = durability;
		this.durability = durability;
		hasDurability = durability > 0;
		
		this.appliedUpgrades = appliedUpgrades;
		
		this.identified = identified;
		this.inCraftingState = inCraftingState;
		
		this.effectSocketKey = effectSocketKey;
		
		tmpItem = update(new ItemStack(material));
	}
	
	private void initStats(RPGCombatItemTier tier) {
		ConfigFile c = ConfigFile.getConfig(FILE_NAME);
		String cPath = itemKey + ".";
		String cStatsPath = cPath + "stats.";
		
		if(tier == null) {
			this.tier = getRandomItemTier(itemKey);
		} else {
			this.tier = tier;
		}
		
		double tierStatsMultiplier = c.getDouble(cPath + "tiers.stats-multipliers." + this.tier.toString().toLowerCase());
		
		this.pveDamage = PrimitiveTypeTools.round(c.getDouble(cStatsPath + "pve-damage") * tierStatsMultiplier, 2);
		this.pvpDamage = PrimitiveTypeTools.round(c.getDouble(cStatsPath + "pvp-damage") * tierStatsMultiplier, 2);
		this.criticalRate = PrimitiveTypeTools.round(c.getDouble(cStatsPath + "critical-rate") * tierStatsMultiplier, 2);
		this.criticalDamageMultiplier = PrimitiveTypeTools.round(c.getDouble(cStatsPath + "critical-damage-multiplier") * tierStatsMultiplier, 2);

		this.additionalHealth = PrimitiveTypeTools.round(c.getDouble(cStatsPath + "additional-health") * tierStatsMultiplier, 2);
		this.agility = PrimitiveTypeTools.round(c.getDouble(cStatsPath + "agility") * tierStatsMultiplier, 2);
		this.armor = PrimitiveTypeTools.round(c.getDouble(cStatsPath + "armor") * tierStatsMultiplier, 2);
		this.dodgeChance = PrimitiveTypeTools.round(c.getDouble(cStatsPath + "dodge-chance") * tierStatsMultiplier, 2);

		this.tenacity = PrimitiveTypeTools.round(c.getDouble(cStatsPath + "tenacity") * tierStatsMultiplier, 2);
		this.lifesteal = PrimitiveTypeTools.round(c.getDouble(cStatsPath + "lifesteal") * tierStatsMultiplier, 2);
		this.armorPenetration = PrimitiveTypeTools.round(c.getDouble(cStatsPath + "armor-penetration") * tierStatsMultiplier, 2);

		this.travelSpeed = PrimitiveTypeTools.round(c.getDouble(cStatsPath + "travel-speed") * tierStatsMultiplier, 2);
		this.flamableChance = PrimitiveTypeTools.round(c.getDouble(cStatsPath + "flamable-chance") * tierStatsMultiplier, 2);

		this.overhaul = PrimitiveTypeTools.round(c.getDouble(cStatsPath + "overhaul") * tierStatsMultiplier, 2);

		this.maxDurability = (int) (c.getDouble(cPath + "durability") * c.getDouble(cPath + "tiers.durability-multipliers." + this.tier.toString().toLowerCase()));
		this.durability = this.maxDurability;
		
		this.appliedUpgrades = 0;
	}

	/**
	 * @return the corresponding tier color
	 */
	public ChatColor getTierColor() {
		if(tier == null) {
			return ChatColor.WHITE;
		}
		
		switch (tier) {
			case COMMON:
				return COLOR_COMMON;
			case DECENT:
				return COLOR_DECENT;
			case ORDINARY:
				return COLOR_ORDINARY;
			case RARE:
				return COLOR_RARE;
			case GRAND:
				return COLOR_GRAND;
		}

		return ChatColor.WHITE;
	}

	/**
	 * @return the unique configuration item key
	 */
	public String getItemKey() {
		return itemKey;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the tier
	 */
	public RPGCombatItemTier getTier() {
		return tier;
	}

	/**
	 * @param tier the tier to set
	 */
	public void setTier(RPGCombatItemTier tier) {
		this.tier = tier;
	}

	/**
	 * @return the material
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * @param material the material to set
	 */
	public void setMaterial(Material material) {
		this.material = material;
	}

	/**
	 * @return the PVE damage
	 */
	public double getPveDamage() {
		return pveDamage;
	}

	/**
	 * @param pveDamage the PVE damage to set
	 */
	public void setPveDamage(double pveDamage, ItemStack item) {
		if(!ItemType.isWeapon(item.getType())) this.pveDamage = 0;
		
		this.pveDamage = PrimitiveTypeTools.round(pveDamage, 2);
	}
	
	/**
	 * @return the PVP damage
	 */
	public double getPvpDamage() {
		return pvpDamage;
	}

	/**
	 * @param pvpDamage the PVP damage to set
	 */
	public void setPvpDamage(double pvpDamage, ItemStack item) {
		if(!ItemType.isWeapon(item.getType())) this.pvpDamage = 0;
		
		this.pvpDamage = PrimitiveTypeTools.round(pvpDamage, 2);
	}

	/**
	 * @return the critical chance
	 */
	public double getCriticalRate() {
		return criticalRate;
	}

	/**
	 * @param criticalRate the critical rate to set
	 */
	public void setCriticalRate(double criticalRate, ItemStack item) {
		if(!ItemType.isWeapon(item.getType())) this.criticalRate = 0;
		
		this.criticalRate = PrimitiveTypeTools.round(criticalRate, 2);
	}
	
	/**
	 * @return the critical damage multiplier
	 */
	public double getCriticalDamageMultiplier() {
		return criticalDamageMultiplier;
	}

	/**
	 * @param criticalDamageMultiplier the critical damage multiplier to set
	 */
	public void setCriticalDamageMultiplier(double criticalDamageMultiplier, ItemStack item) {
		if(!ItemType.isWeapon(item.getType())) this.criticalDamageMultiplierUpgrade = 0;
		
		this.criticalDamageMultiplier = PrimitiveTypeTools.round(criticalDamageMultiplier, 2);
	}

	/**
	 * @return the additional health
	 */
	public double getAdditionalHealth() {
		return additionalHealth;
	}

	/**
	 * @param additionalHealth the additional health to set
	 */
	public void setAdditionalHealth(double additionalHealth, ItemStack item) {
		if(!ItemType.isArmor(item.getType())) this.additionalHealth = 0;
		
		this.additionalHealth = PrimitiveTypeTools.round(additionalHealth, 2);
	}
	
	/**
	 * @return the agility
	 */
	public double getAgility() {
		return agility;
	}

	/**
	 * @param agility the agility to set
	 */
	public void setAgility(double agility, ItemStack item) {
		if(!ItemType.isArmor(item.getType())) this.agility = 0;
		
		this.agility = PrimitiveTypeTools.round(agility, 2);
	}

	/**
	 * @return the armor
	 */
	public double getArmor() {
		return armor;
	}

	/**
	 * @param armor the armor to set
	 */
	public void setArmor(double armor, ItemStack item) {
		if(!ItemType.isArmor(item.getType())) this.armor = 0;
		
		this.armor = PrimitiveTypeTools.round(armor, 2);
	}
	
	/**
	 * @return the dodge chance
	 */
	public double getDodgeChance() {
		return dodgeChance;
	}

	/**
	 * @param dodgeChance the dodge chance to set
	 */
	public void setDodgeChance(double dodgeChance, ItemStack item) {
		if(!ItemType.isArmor(item.getType())) this.dodgeChance = 0;
		
		this.dodgeChance = PrimitiveTypeTools.round(dodgeChance, 2);
	}

	/**
	 * @return the tenacity
	 */
	public double getTenacity() {
		return tenacity;
	}

	/**
	 * @param tenacity the tenacity to set
	 */
	public void setTenacity(double tenacity, ItemStack item) {
		if(!ItemType.isWeapon(item.getType())) this.tenacity = 0;
		
		this.tenacity = PrimitiveTypeTools.round(tenacity, 2);
	}

	/**
	 * @return the lifesteal
	 */
	public double getLifesteal() {
		return lifesteal;
	}

	/**
	 * @param lifesteal the lifesteal to set
	 */
	public void setLifesteal(double lifesteal, ItemStack item) {
		if(!ItemType.isWeapon(item.getType())) this.lifesteal = 0;
		
		this.lifesteal = PrimitiveTypeTools.round(lifesteal, 2);
	}

	/**
	 * @return the armor penetration
	 */
	public double getArmorPenetration() {
		return armorPenetration;
	}

	/**
	 * @param armorPenetration the armor penetration to set
	 */
	public void setArmorPenetration(double armorPenetration, ItemStack item) {
		if(!ItemType.isWeapon(item.getType())) this.armorPenetration = 0;
		
		this.armorPenetration = PrimitiveTypeTools.round(armorPenetration, 2);
	}

	/**
	 * @return the travel speed
	 */
	public double getTravelSpeed() {
		return travelSpeed;
	}

	/**
	 * @param travelSpeed the travel speed to set
	 */
	public void setTravelSpeed(double travelSpeed, ItemStack item) {
		if(!ItemType.isArrow(item.getType())) this.travelSpeed = 0;

		this.travelSpeed = PrimitiveTypeTools.round(travelSpeed, 2);
	}

	/**
	 * @return the flamable chance
	 */
	public double getFlamableChance() {
		return flamableChance;
	}

	/**
	 * @param flamableChance the flamable chance to set
	 */
	public void setFlamableChance(double flamableChance, ItemStack item) {
		if(!ItemType.isArrow(item.getType())) this.flamableChance = 0;

		this.flamableChance = PrimitiveTypeTools.round(flamableChance, 2);
	}

	/**
	 * @return the overhaul chance
	 */
	public double getOverhaul() {
		return overhaul;
	}

	/**
	 * @param overhaul the overhaul chance to set
	 */
	public void setOverhaul(double overhaul, ItemStack item) {
		if(!ItemType.isArrow(item.getType())) this.overhaul = 0;

		this.overhaul = PrimitiveTypeTools.round(overhaul, 2);
	}

	/**
	 * @return The upgrade value of the PVE damage stat
	 */
	public double getPveDamageUpgrade() {
		return pveDamageUpgrade;
	}

	/**
	 * @param pveDamageUpgrade the PVE damage upgrade value to set
	 */
	public void setPveDamageUpgrade(double pveDamageUpgrade, ItemStack item) {
		if(!ItemType.isWeapon(item.getType())) this.pveDamageUpgrade = 0;
		
		this.pveDamageUpgrade = PrimitiveTypeTools.round(pveDamageUpgrade, 2);
	}
	
	/**
	 * @return The upgrade value of the PVP damage stat
	 */
	public double getPvpDamageUpgrade() {
		return pvpDamageUpgrade;
	}

	/**
	 * @param pvpDamageUpgrade the PVP damage upgrade value to set
	 */
	public void setPvpDamageUpgrade(double pvpDamageUpgrade, ItemStack item) {
		if(!ItemType.isWeapon(item.getType())) this.pvpDamageUpgrade = 0;
		
		this.pvpDamageUpgrade = PrimitiveTypeTools.round(pvpDamageUpgrade, 2);
	}

	/**
	 * @return The upgrade value of the critical rate stat
	 */
	public double getCriticalRateUpgrade() {
		return criticalRateUpgrade;
	}

	/**
	 * @param criticalRateUpgrade the critical rate upgrade value to set
	 */
	public void setCriticalRateUpgrade(double criticalRateUpgrade, ItemStack item) {
		if(!ItemType.isWeapon(item.getType())) this.criticalRateUpgrade = 0;
		
		this.criticalRateUpgrade = PrimitiveTypeTools.round(criticalRateUpgrade, 2);
	}
	
	/**
	 * @return The upgrade value of the critical damage multiplier stat
	 */
	public double getCriticalDamageMultiplierUpgrade() {
		return criticalDamageMultiplierUpgrade;
	}

	/**
	 * @param criticalDamageMultiplierUpgrade the critical damage multiplier upgrade value to set
	 */
	public void setCriticalDamageMultiplierUpgrade(double criticalDamageMultiplierUpgrade, ItemStack item) {
		if(!ItemType.isWeapon(item.getType())) this.criticalDamageMultiplierUpgrade = 0;
		
		this.criticalDamageMultiplierUpgrade = PrimitiveTypeTools.round(criticalDamageMultiplierUpgrade, 2);
	}

	/**
	 * @return The upgrade value of the additional health stat
	 */
	public double getAdditionalHealthUpgrade() {
		return additionalHealthUpgrade;
	}

	/**
	 * @param additionalHealthUpgrade the additional health upgrade value to set
	 */
	public void setAdditionalHealthUpgrade(double additionalHealthUpgrade, ItemStack item) {
		if(!ItemType.isArmor(item.getType())) this.additionalHealthUpgrade = 0;
		
		this.additionalHealthUpgrade = PrimitiveTypeTools.round(additionalHealthUpgrade, 2);
	}
	
	/**
	 * @return The upgrade value of the agility stat
	 */
	public double getAgilityUpgrade() {
		return agilityUpgrade;
	}

	/**
	 * @param agilityUpgrade the agility upgrade value to set
	 */
	public void setAgilityUpgrade(double agilityUpgrade, ItemStack item) {
		if(!ItemType.isArmor(item.getType())) this.agilityUpgrade = 0;
		
		this.agilityUpgrade = PrimitiveTypeTools.round(agilityUpgrade, 2);
	}

	/**
	 * @return The upgrade value of the armor stat
	 */
	public double getArmorUpgrade() {
		return armorUpgrade;
	}

	/**
	 * @param armorUpgrade the armor upgrade value to set
	 */
	public void setArmorUpgrade(double armorUpgrade, ItemStack item) {
		if(!ItemType.isArmor(item.getType())) this.armorUpgrade = 0;
		
		this.armorUpgrade = PrimitiveTypeTools.round(armorUpgrade, 2);
	}
	
	/**
	 * @return The upgrade value of the dodge chance stat
	 */
	public double getDodgeChanceUpgrade() {
		return armorUpgrade;
	}

	/**
	 * @param dodgeChanceUpgrade the dodge chance upgrade value to set
	 */
	public void setDodgeChanceUpgrade(double dodgeChanceUpgrade, ItemStack item) {
		if(!ItemType.isArmor(item.getType())) this.dodgeChanceUpgrade = 0;
		
		this.dodgeChanceUpgrade = PrimitiveTypeTools.round(dodgeChanceUpgrade, 2);
	}

	/**
	 * @return The upgrade value of the tenacity stat
	 */
	public double getTenacityUpgrade() {
		return tenacityUpgrade;
	}

	/**
	 * @param tenacityUpgrade the tenacity upgrade value to set
	 */
	public void setTenacityUpgrade(double tenacityUpgrade, ItemStack item) {
		if(!ItemType.isWeapon(item.getType())) this.tenacityUpgrade = 0;
		
		this.tenacityUpgrade = PrimitiveTypeTools.round(tenacityUpgrade, 2);
	}

	/**
	 * @return The upgrade value of the lifesteal stat
	 */
	public double getLifestealUpgrade() {
		return lifestealUpgrade;
	}

	/**
	 * @param lifestealUpgrade the lifesteal upgrade value to set
	 */
	public void setLifestealUpgrade(double lifestealUpgrade, ItemStack item) {
		if(!ItemType.isWeapon(item.getType())) this.lifestealUpgrade = 0;
		
		this.lifestealUpgrade = PrimitiveTypeTools.round(lifestealUpgrade, 2);
	}

	/**
	 * @return The upgrade value of the armor penetration stat
	 */
	public double getArmorPenetrationUpgrade() {
		return armorPenetrationUpgrade;
	}

	/**
	 * @param armorPenetrationUpgrade the armor penetration upgrade value to set
	 */
	public void setArmorPenetrationUpgrade(double armorPenetrationUpgrade, ItemStack item) {
		if(!ItemType.isWeapon(item.getType())) this.armorPenetrationUpgrade = 0;
		
		this.armorPenetrationUpgrade = PrimitiveTypeTools.round(armorPenetrationUpgrade, 2);
	}

	/**
	 * @return The upgrade value of the travel speed stat
	 */
	public double getTravelSpeedUpgrade() {
		return travelSpeedUpgrade;
	}

	/**
	 * @param travelSpeedUpgrade the travel speed upgrade value to set
	 */
	public void setTravelSpeedUpgrade(double travelSpeedUpgrade, ItemStack item) {
		if(!ItemType.isArrow(item.getType())) this.travelSpeedUpgrade = 0;

		this.travelSpeedUpgrade = PrimitiveTypeTools.round(travelSpeedUpgrade, 2);
	}

	/**
	 * @return The upgrade value of the flamable chance stat
	 */
	public double getFlamableChanceUpgrade() {
		return flamableChanceUpgrade;
	}

	/**
	 * @param flamableChanceUpgrade the flamable chance upgrade value to set
	 */
	public void setFlamableChanceUpgrade(double flamableChanceUpgrade, ItemStack item) {
		if(!ItemType.isArrow(item.getType())) this.flamableChanceUpgrade = 0;

		this.flamableChanceUpgrade = PrimitiveTypeTools.round(flamableChanceUpgrade, 2);
	}

	/**
	 * @return The upgrade value of the overhaul chance stat
	 */
	public double getOverhaulUpgrade() {
		return overhaulUpgrade;
	}

	/**
	 * @param overhaulUpgrade the overhaul chance upgrade value to set
	 */
	public void setOverhaulUpgrade(double overhaulUpgrade, ItemStack item) {
		if(!ItemType.isWeapon(item.getType()) && !ItemType.isArmor(item.getType())) this.overhaulUpgrade = 0;

		this.overhaulUpgrade = PrimitiveTypeTools.round(overhaulUpgrade, 2);
	}

	/**
	 * @param itemKey the itemKey to set
	 */
	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}

	/**
	 * @return The sum of the base PVE damage and the upgraded PVE damage value
	 */
	public double getTotalPveDamage() {
		return pveDamage + pveDamageUpgrade;
	}
	
	/**
	 * @return The sum of the base PVP damage and the upgraded PVP damage value
	 */
	public double getTotalPvpDamage() {
		return pvpDamage + pvpDamageUpgrade;
	}
	
	/**
	 * @return The sum of the base critical rate and the upgraded critical rate value
	 */
	public double getTotalCriticalRate() {
		return criticalRate + criticalRateUpgrade;
	}
	
	/**
	 * @return The sum of the base critical damage multiplier and the upgraded critical damage multiplier value
	 */
	public double getTotalCriticalDamageMultiplier() {
		return criticalDamageMultiplier + criticalDamageMultiplierUpgrade;
	}
	
	/**
	 * @return The sum of the base additional health and the upgraded additional health value
	 */
	public double getTotalAdditionalHealth() {
		return additionalHealth + additionalHealthUpgrade;
	}
	
	/**
	 * @return The sum of the base agility and the upgraded agility value
	 */
	public double getTotalAgility() {
		return agility + agilityUpgrade;
	}
	
	/**
	 * @return The sum of the base armor and the upgraded armor value
	 */
	public double getTotalArmor() {
		return armor + armorUpgrade;
	}
	
	/**
	 * @return The sum of the base dodge chance and the upgraded dodge chance value
	 */
	public double getTotalDodgeChance() {
		return dodgeChance + dodgeChanceUpgrade;
	}
	
	/**
	 * @return The sum of the base tenacity and the upgraded tenacity value
	 */
	public double getTotalTenacity() {
		return tenacity + tenacityUpgrade;
	}
	
	/**
	 * @return The sum of the base lifesteal and the upgraded lifesteal value
	 */
	public double getTotalLifesteal() {
		return lifesteal + lifestealUpgrade;
	}
	
	/**
	 * @return The sum of the base armor penetration and the upgraded armor penetration value
	 */
	public double getTotalArmorPenetration() {
		return armorPenetration + armorPenetrationUpgrade;
	}

	/**
	 * @return The sum of the base travel speed and the upgraded travel speed value
	 */
	public double getTotalTravelSpeed() {
		return travelSpeed + travelSpeedUpgrade;
	}

	/**
	 * @return The sum of the base flamable chance and the upgraded flamable chance value
	 */
	public double getTotalFlamableChance() {
		return flamableChance + flamableChanceUpgrade;
	}

	/**
	 * @return The sum of the base overhaul chance and the upgraded overhaul chance value
	 */
	public double getTotalOverhaul() {
		return overhaul + overhaulUpgrade;
	}

	/**
	 * @return The durability
	 */
	public int getDurability() {
		return durability;
	}
	
	/**
	 * @return The maximum durability
	 */
	public int getMaxDurability() {
		return maxDurability;
	}

	/**
	 * @param durability the durability to set
	 */
	public void setDurability(int durability, ItemStack item) {
		durability = Math.max(durability, 0);
		this.durability = durability;
		
		ConfigFile c = ConfigFile.getConfig(FILE_NAME);
		if(durability > maxDurability && durability > c.getInteger(itemKey + ".durability")) {
			maxDurability = durability;
		}
	}
	
	/**
	 * @return The amount of upgrades applied
	 */
	public int getAppliedUpgrades() {
		return appliedUpgrades;
	}
	
	/**
	 * @param appliedUpgrades The amount of upgrades applied to set
	 */
	public void setAppliedUpgrades(int appliedUpgrades, ItemStack item) {
		this.appliedUpgrades = appliedUpgrades;
	}

	/**
	 * @return Whether the item has been identified
	 */
	public boolean isIdentified() {
		return identified;
	}

	/**
	 * @param identified whether to set if the item is identified
	 * @param initStats whether to initialize new stats on the item upon identifying it
	 * @param tier A specific tier the item will be identified with (can be null for a random tier)
	 */
	public void setIdentified(boolean identified, boolean initStats, @Nullable RPGCombatItemTier tier, ItemStack item) {
		if(!this.identified && identified && initStats) {
			initStats(tier);
		}
		
		this.identified = identified;
	}

	/**
	 * @return Whether the item has its stats hidden (including the tier) for the crafting phase
	 */
	public boolean isInCraftingState() {
		return inCraftingState;
	}

	/**
	 * @param inCraftingState whether the stats should be hidden (including the tier) during the crafting phase
	 */
	public void setInCraftingState(boolean inCraftingState, ItemStack item) {
		this.inCraftingState = inCraftingState;
	}
	
	/**
	 * @return The effect socket applied to the item
	 */
	public String getEffectSocketKey() {
		return effectSocketKey;
	}
	
	/**
	 * @param effectSocketKey The effect socket applied to the item to set
	 */
	public void setEffectSocketKey(String effectSocketKey, ItemStack item) {
		this.effectSocketKey = effectSocketKey;
	}
	
	public int[] getDurabilityLoss() throws IllegalArgumentException {
		ConfigFile c = ConfigFile.getConfig(FILE_NAME);
		
		String durabilityLossStr = c.getString(itemKey + ".durability-loss");
		if(durabilityLossStr.contains("-")) {
			return PrimitiveTypeTools.parseIntRange(durabilityLossStr);
		} else {
			if(!PrimitiveTypeTools.isInt(durabilityLossStr)) {
				throw new IllegalArgumentException("The durability loss value from the " + FILE_NAME + " file could not be parsed correctly.");
			}
			return new int[] { Integer.parseInt(durabilityLossStr) };
		}
	}
	
	@Override
	public ItemStack update(ItemStack item) {
		if(material != null) {
			item.setType(material);
		} else {
			return new ItemStack(Material.AIR);
		}
		
		if(durability == 0 && hasDurability) {
			NBTItem nbtItem = new NBTItem(item);
			nbtItem.removeKey("rpgdata_combatitem");
			item = nbtItem.getItem();

			item.setType(Material.AIR);
			
			return item;
		}
		
		ItemMeta meta = item.getItemMeta();

		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_DESTROYS);

		ArrayList<String> lore = new ArrayList<>();
		
		if (identified && !inCraftingState) {
			meta.setDisplayName("" + getTierColor() + ChatColor.BOLD + getTier().name() + " " + ChatColor.RESET + getTierColor() + name);
			
			ArrayList<String> statsLore = new ArrayList<>();

			if (getTotalPveDamage() > 0) {
				statsLore.add(LORE_PRIMARY_COLOR + LanguageManager.getGlobalMessage("stats.pve-damage") + ": " + LORE_SECONDARY_COLOR + getTotalPveDamage() + ChatColor.GRAY + " (" + pveDamage + " + " + pveDamageUpgrade + ")");
			}
			if (getTotalPvpDamage() > 0) {
				statsLore.add(LORE_PRIMARY_COLOR + LanguageManager.getGlobalMessage("stats.pvp-damage") + ": " + LORE_SECONDARY_COLOR + getTotalPvpDamage() + ChatColor.GRAY + " (" + pvpDamage + " + " + pvpDamageUpgrade + ")");
			}
			if (getTotalCriticalRate() > 0) {
				statsLore.add(LORE_PRIMARY_COLOR + LanguageManager.getGlobalMessage("stats.critical-rate") + ": " + LORE_SECONDARY_COLOR + getTotalCriticalRate() + "%" + ChatColor.GRAY + " (" + criticalRate + "% + " + criticalRateUpgrade + "%)");
			}
			if (getTotalCriticalDamageMultiplier() > 0) {
				statsLore.add(LORE_PRIMARY_COLOR + LanguageManager.getGlobalMessage("stats.critical-damage-multiplier") + ": " + LORE_SECONDARY_COLOR + getTotalCriticalDamageMultiplier() + "x" + ChatColor.GRAY + " (" + criticalDamageMultiplier + "x + " + criticalDamageMultiplierUpgrade + "x)");
			}
			if (getTotalAdditionalHealth() > 0) {
				statsLore.add(LORE_PRIMARY_COLOR + LanguageManager.getGlobalMessage("stats.health") + ": " + LORE_SECONDARY_COLOR + "+" + getTotalAdditionalHealth() + ChatColor.GRAY + " (" + additionalHealth + " + " + additionalHealthUpgrade + ")");
			}
			if (getTotalArmor() > 0) {
				statsLore.add(LORE_PRIMARY_COLOR + LanguageManager.getGlobalMessage("stats.armor") + ": " + LORE_SECONDARY_COLOR + getTotalArmor() + ChatColor.GRAY + " (" + armor + " + " + armorUpgrade + ")");
			}
			if (getTotalDodgeChance() > 0) {
				statsLore.add(LORE_PRIMARY_COLOR + LanguageManager.getGlobalMessage("stats.dodge-chance") + ": " + LORE_SECONDARY_COLOR + getTotalDodgeChance() + ChatColor.GRAY + " (" + dodgeChance + " + " + dodgeChanceUpgrade + ")");
			}
			if (getTotalTenacity() > 0) {
				statsLore.add(LORE_PRIMARY_COLOR + LanguageManager.getGlobalMessage("stats.tenacity") + ": +" + LORE_SECONDARY_COLOR + getTotalTenacity() + "%" + ChatColor.GRAY + " (" + tenacity + "% + " + tenacityUpgrade + "%)");
			}
			if (getTotalLifesteal() > 0) {
				statsLore.add(LORE_PRIMARY_COLOR + LanguageManager.getGlobalMessage("stats.lifesteal") + ": " + LORE_SECONDARY_COLOR + getTotalLifesteal() + "%" + ChatColor.GRAY + " (" + lifesteal + "% + " + lifestealUpgrade + "%)");
			}
			if (getTotalArmorPenetration() > 0) {
				statsLore.add(LORE_PRIMARY_COLOR + LanguageManager.getGlobalMessage("stats.armor-penetration") + ": +" + LORE_SECONDARY_COLOR + getTotalArmorPenetration() + ChatColor.GRAY + " (" + armorPenetration + " + " + armorPenetrationUpgrade + ")");
			}
			if (getTotalTravelSpeed() > 0) {
				statsLore.add(LORE_PRIMARY_COLOR + LanguageManager.getGlobalMessage("stats.travel-speed") + ": " + LORE_SECONDARY_COLOR + getTotalTravelSpeed() + "%" + ChatColor.GRAY + " (" + travelSpeed + "% + " + travelSpeedUpgrade + "%)");
			}
			if (getTotalFlamableChance() > 0) {
				statsLore.add(LORE_PRIMARY_COLOR + LanguageManager.getGlobalMessage("stats.flamable-chance") + ": " + LORE_SECONDARY_COLOR + getTotalFlamableChance() + "%" + ChatColor.GRAY + " (" + flamableChance + "% + " + flamableChanceUpgrade + "%)");
			}
			if (getOverhaul() > 0) {
				statsLore.add(LORE_PRIMARY_COLOR + LanguageManager.getGlobalMessage("stats.overhaul") + ": " + LORE_SECONDARY_COLOR + getTotalOverhaul() + "%" + ChatColor.GRAY + " (" + overhaul + "% + " + overhaulUpgrade + "%)");
			}
			
			lore.add("");
			lore.add(LORE_SECONDARY_COLOR + "" + ChatColor.STRIKETHROUGH + "                       " + LORE_SECONDARY_COLOR + "[ " + LanguageManager.getGlobalMessage("etc.stats") + " ]" + LORE_SECONDARY_COLOR + "" + ChatColor.STRIKETHROUGH + "                         ");
			if(statsLore.isEmpty()) {
				lore.add(ChatColor.GRAY + LanguageManager.getGlobalMessage("etc.none"));
			} else {
				lore.addAll(statsLore);
			}
			lore.add(LORE_PRIMARY_COLOR + LanguageManager.getGlobalMessage("stats.upgrades-applied") + ": " + LORE_SECONDARY_COLOR + appliedUpgrades);
			
			if(!ItemType.isArrow(item.getType())) {
				lore.add("");
				lore.add(LORE_SECONDARY_COLOR + "" + ChatColor.STRIKETHROUGH + "                   " + LORE_SECONDARY_COLOR + "[ " + LanguageManager.getGlobalMessage("etc.effect-socket") + " ]" + LORE_SECONDARY_COLOR + "" + ChatColor.STRIKETHROUGH + "                  ");

				ConfigFile effectSocketConfig = ConfigFile.getConfig(EffectSocketService.FILE_NAME);
				if(effectSocketConfig.getRootKeys().contains(effectSocketKey)) {
					lore.add(ChatColor.GRAY + "[" + ChatColor.GREEN + "*" + ChatColor.GRAY + "] | " + LORE_SECONDARY_COLOR + effectSocketConfig.getString(effectSocketKey + ".appearance.display-name"));
				} else {
					lore.add(ChatColor.GRAY + "[-] | " + LanguageManager.getGlobalMessage("etc.none"));
				}

				lore.add("");
				lore.add("");

				StringBuilder durabilityStringBuilder = new StringBuilder();
				double durabilityProgress = 100d / maxDurability * durability;
				int blocks = 20;

				for (int i = 0; i < blocks; i++) {
					if (durabilityProgress > i * (100.0d / blocks)) {
						durabilityStringBuilder.append(LORE_SECONDARY_COLOR);
					} else {
						durabilityStringBuilder.append(ChatColor.GRAY);
					}
					durabilityStringBuilder.append("▌");
				}
				lore.add(LORE_PRIMARY_COLOR + LanguageManager.getGlobalMessage("stats.durability") + " [" + durabilityStringBuilder.toString() + LORE_PRIMARY_COLOR + "] (" + durability + "/" + maxDurability + ")");
			}
			
			if(tier != null) {
				lore.add("");
				lore.add(ChatColor.DARK_GRAY + WordUtils.capitalizeFully(tier.toString()));
			}
		} else if(!identified && !inCraftingState) {
			meta.setDisplayName(ChatColor.GRAY + LanguageManager.getGlobalMessage("stats.unidentified-item", name));
			lore.add(ChatColor.WHITE + LanguageManager.getGlobalMessage("stats.identification-lore"));
		} else if(inCraftingState) {
			meta.setDisplayName(LanguageManager.getGlobalMessage("rpg-items.pre-applicable-apply", true));
		}
		
		meta.setLore(lore);

		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

		item.setItemMeta(meta);

		NBTItem nbtItem = new NBTItem(item);
		nbtItem.setObject("rpgdata_combatitem", this);
		
		return nbtItem.getItem();
	}
	
	public void modifyAttribute(RPGAttributeType attribute, Object value, ItemStack item) {
		modifyAttribute(attribute, value, false, item);
	}
	
	public void modifyAttribute(RPGAttributeType attribute, Object value, boolean upgrade, ItemStack item) {
		if(value instanceof String) {
			String valStr = (String) value;
			
			if(valStr.matches("^-?\\d+(-\\d+)?$")) {
				double[] amountData = PrimitiveTypeTools.parseDoubleRange(valStr);
				
				if(amountData.length > 1) {
					value = PrimitiveTypeTools.round(RandomTools.getRandomRange(amountData[0], amountData[1]), 1);
				} else {
					value = PrimitiveTypeTools.round(amountData[0], 1);
				}
			}
		}
		
		switch(attribute) {
			case PVE_DAMAGE:
				if(upgrade) setPveDamageUpgrade((double) value + getPveDamageUpgrade(), item);
				else setPveDamage((double) value, item);
				break;
			case PVP_DAMAGE:
				if(upgrade) setPvpDamageUpgrade((double) value + getPvpDamageUpgrade(), item);
				else setPvpDamage((double) value, item);
				break;
			case CRITICAL_RATE:
				if(upgrade) setCriticalRateUpgrade((double) value + getCriticalRateUpgrade(), item);
				else setCriticalRate((double) value, item);
				break;
			case CRITICAL_DAMAGE_MULTIPLIER:
				if(upgrade) setCriticalDamageMultiplierUpgrade((double) value + getCriticalDamageMultiplierUpgrade(), item);
				else setCriticalDamageMultiplier((double) value, item);
				break;
			case ADDITIONAL_HEALTH:
				if(upgrade) setAdditionalHealthUpgrade((double) value + getAdditionalHealthUpgrade(), item);
				else setAdditionalHealth((double) value, item);
				break;
			case AGILITY:
				if(upgrade) setAgilityUpgrade((double) value + getAgilityUpgrade(), item);
				else setAgility((double) value, item);
				break;
			case ARMOR:
				if(upgrade) setArmorUpgrade((double) value + getArmorUpgrade(), item);
				else setArmor((double) value, item);
				break;
			case DODGE_CHANCE:
				if(upgrade) setDodgeChanceUpgrade((double) value + getDodgeChanceUpgrade(), item);
				else setDodgeChance((double) value, item);
				break;
			case TENACITY:
				if(upgrade) setTenacityUpgrade((double) value + getTenacityUpgrade(), item);
				else setTenacity((double) value, item);
				break;
			case LIFESTEAL:
				if(upgrade) setLifestealUpgrade((double) value + getLifestealUpgrade(), item);
				else setLifesteal((double) value, item);
				break;
			case ARMOR_PENETRATION:
				if(upgrade) setArmorPenetrationUpgrade((double) value + getArmorPenetrationUpgrade(), item);
				else setArmorPenetration((double) value, item);
				break;
			case TRAVEL_SPEED:
				if(upgrade) setTravelSpeedUpgrade((double) value + getTravelSpeedUpgrade(), item);
				else setTravelSpeed((double) value, item);
				break;
			case FLAMABLE_CHANCE:
				if(upgrade) setFlamableChanceUpgrade((double) value + getFlamableChanceUpgrade(), item);
				else setFlamableChance((double) value, item);
				break;
			case OVERHAUL:
				if(upgrade) setOverhaulUpgrade((double) value + getOverhaulUpgrade(), item);
				else setOverhaul((double) value, item);
				break;
			case IDENTIFICATION:
				if(!(boolean) value) {
					break;
				}
				setIdentified((boolean) value, true, null, item);
				break;
		}
	}

	public static RPGCombatItemTier getRandomItemTier(String itemKey) {
		ConfigFile c = ConfigFile.getConfig(FILE_NAME);
		String cPath = itemKey + ".tiers.rates.";

		double[] tierRates = new double[] { c.getDouble(cPath + "common"), c.getDouble(cPath + "decent"),
				c.getDouble(cPath + "ordinary"), c.getDouble(cPath + "rare"), c.getDouble(cPath + "grand") };

		double r = RandomTools.getRandomPercentage();
		double rOffset = 0;

		for (int i = 0; i < tierRates.length; i++) {
			double rate = tierRates[i];

			if (r > rOffset && r <= rOffset + rate) {
				return RPGCombatItemTier.values()[i];
			}

			rOffset += rate;
		}

		return null;
	}
	
	@Override
	public RPGCombatItem clone() {
		try {
			return (RPGCombatItem) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}