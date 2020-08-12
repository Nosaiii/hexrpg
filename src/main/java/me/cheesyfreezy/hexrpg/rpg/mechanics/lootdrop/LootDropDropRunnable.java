package me.cheesyfreezy.hexrpg.rpg.mechanics.lootdrop;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;

import me.cheesyfreezy.hexrpg.main.Plugin;

public class LootDropDropRunnable implements Runnable {
	private LootDrop lootDrop;
	private int yGoal, currentY;

	public LootDropDropRunnable(LootDrop lootDrop, int yGoal) {
		this.lootDrop = lootDrop;
		this.yGoal = yGoal;
		this.currentY = lootDrop.getLocation().getWorld().getMaxHeight() + 1;
	}

	public LootDrop getLootDrop() {
		return lootDrop;
	}

	@Override
	public void run() {
		currentY--;
		
		Location location = lootDrop.getLocation().clone();
		location.setY(currentY);

		Firework fw = location.getWorld().spawn(location, Firework.class);
		FireworkMeta fwMeta = fw.getFireworkMeta();
		fwMeta.addEffect(FireworkEffect.builder().flicker(true).trail(true).with(Type.BURST).withColor(lootDrop.getTier().getFireworkColor()).build());
		fw.setFireworkMeta(fwMeta);
		
		fw.setMetadata("nodamage", new FixedMetadataValue(Plugin.getMain(), true));

		Bukkit.getServer().getScheduler().runTaskLater(Plugin.getMain(), () -> {
			fw.detonate();
		}, 1);
		
		if (currentY == yGoal) {
			lootDrop.place();
			Bukkit.getServer().getScheduler().cancelTask(lootDrop.getDropTaskId());
		}
	}
}