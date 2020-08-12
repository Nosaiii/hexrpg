package me.cheesyfreezy.hexrpg.listeners.world.entity;

import de.tr7zw.hexrpg.nbtapi.NBTItem;
import me.cheesyfreezy.hexrpg.main.Plugin;
import me.cheesyfreezy.hexrpg.rpg.items.combatitem.RPGCombatItem;
import me.cheesyfreezy.hexrpg.tools.RandomTools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class OnArrowStatsApply implements Listener {
    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }
        Player shooter = (Player) event.getEntity();

        Arrow arrow = (Arrow) event.getProjectile();

        ItemStack mainHandItem = shooter.getInventory().getItemInMainHand();
        if(mainHandItem == null || (!mainHandItem.getType().equals(Material.BOW) && !mainHandItem.getType().equals(Material.CROSSBOW))) {
            return;
        }

        NBTItem nbtItem = new NBTItem(mainHandItem);
        NBTItem nbtArrow = new NBTItem(event.getArrowItem());
        if(!nbtItem.hasKey("rpgdata_combatitem") || !nbtArrow.hasKey("rpgdata_combatitem")) {
            return;
        }
        RPGCombatItem rpgItem = nbtItem.getObject("rpgdata_combatitem", RPGCombatItem.class);
        RPGCombatItem rpgArrow = nbtArrow.getObject("rpgdata_combatitem", RPGCombatItem.class);

        arrow.setMetadata("rpgdata_combatitem", new FixedMetadataValue(Plugin.getMain(), rpgItem));
        arrow.setMetadata("rpgdata_combatarrow", new FixedMetadataValue(Plugin.getMain(), rpgArrow));

        arrow.setVelocity(arrow.getVelocity().multiply(1d + rpgItem.getTotalTravelSpeed() / 100d));

        double r = RandomTools.getRandomPercentage();
        if(r < rpgArrow.getTotalFlamableChance()) {
            arrow.setFireTicks(Integer.MAX_VALUE);
        }
    }
}