package me.cheesyfreezy.hexrpg.listeners.item;

import java.util.ArrayList;

import me.cheesyfreezy.hexrpg.main.Plugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.hexrpg.nbtapi.NBTItem;
import me.cheesyfreezy.hexrpg.rpg.items.combatitem.RPGCombatItem;
import me.cheesyfreezy.hexrpg.rpg.tools.RPGCalculations;
import me.cheesyfreezy.hexrpg.rpg.tools.RPGCalculations.RPGDamage;

public class OnRPGDamageApply implements Listener {
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(!(event.getDamager() instanceof Player) || !(event.getDamager() instanceof Arrow)) {
			return;
		}

		ArrayList<RPGCombatItem> validRpgItems = new ArrayList<>();
		LivingEntity actualDamager = null;

		if(event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			actualDamager = player;

			for(ItemStack handItem : new ItemStack[] {
					player.getInventory().getItemInMainHand(),
					player.getInventory().getItemInOffHand()
			}) {
				if(handItem == null || handItem.getType().equals(Material.AIR)) {
					continue;
				}

				NBTItem nbtItem = new NBTItem(handItem);
				if(!nbtItem.hasKey("rpgdata_combatitem")) {
					continue;
				}
				RPGCombatItem rpgItem = nbtItem.getObject("rpgdata_combatitem", RPGCombatItem.class);
				validRpgItems.add(rpgItem);
			}
		} else if(event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getDamager();

			if(arrow.getShooter() instanceof LivingEntity) {
				actualDamager = (LivingEntity) arrow.getShooter();
			}

			if(arrow.hasMetadata("rpgdata_combatitem")) {
				validRpgItems.add((RPGCombatItem) arrow.getMetadata("rpgdata_combatitem").get(0).value());
				arrow.removeMetadata("rpgdata_combatitem", Plugin.getMain());
			}
		}
		
		if(validRpgItems.isEmpty()) {
			return;
		}
		
		RPGCombatItem[] rpgItems = validRpgItems.toArray(new RPGCombatItem[validRpgItems.size()]);
		Player target = event.getEntity() instanceof Player ? (Player) event.getEntity() : null;
		
		RPGDamage rpgDamage = RPGCalculations.calculateDamage(rpgItems, target);
		
		double particleHeight = event.getEntity().getBoundingBox().getHeight() / 2;
		Location particleSpawnLocation = event.getEntity().getLocation().clone().add(0, particleHeight, 0);
		if(rpgDamage.isCriticalStrike()) {
			target.getWorld().spawnParticle(Particle.BLOCK_CRACK, particleSpawnLocation, 2, Material.REDSTONE_BLOCK.createBlockData());
		}
		if(rpgDamage.isDodged()) {
			target.getWorld().spawnParticle(Particle.PORTAL, particleSpawnLocation, 20);
		}
		
		double damage = rpgDamage.getDamage();
		event.setDamage(damage);
		
		double totalLifesteal = 0;
		for(RPGCombatItem item : rpgItems) {
			totalLifesteal += item.getTotalLifesteal();
		}
		double healing = damage / 100 * totalLifesteal;
		
		double newHealth = actualDamager.getHealth() + healing;
		double maxHealth = actualDamager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
		if(newHealth < 0) {
			newHealth = 0;
		} else if(newHealth > maxHealth) {
			newHealth = maxHealth;
		}

		actualDamager.setHealth(newHealth);
	}
}