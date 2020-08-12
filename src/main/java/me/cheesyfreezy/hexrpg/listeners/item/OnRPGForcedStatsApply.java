package me.cheesyfreezy.hexrpg.listeners.item;

import de.tr7zw.hexrpg.nbtapi.NBTItem;
import me.cheesyfreezy.hexrpg.main.Plugin;
import me.cheesyfreezy.hexrpg.rpg.items.combatitem.RPGCombatItem;
import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import me.cheesyfreezy.hexrpg.tools.RandomTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OnRPGForcedStatsApply implements Listener {
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        for(int i = 0; i < event.getInventory().getSize(); i++) {
            if(event.getInventory().getItem(i) == null || event.getInventory().getItem(i).getType().equals(Material.AIR)) {
                continue;
            }

            ItemStack item = event.getInventory().getItem(i);
            ItemStack fixedItem = checkForcedStatsApply(item);

            if(fixedItem != item) {
                event.getInventory().setItem(i, fixedItem);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        ItemStack item = event.getItemDrop().getItemStack();
        ItemStack fixedItem = checkForcedStatsApply(item);

        if(fixedItem != item) {
            Item fixedDrop = event.getItemDrop().getWorld().dropItem(player.getEyeLocation(), fixedItem);
            fixedDrop.setVelocity(player.getLocation().getDirection().normalize());

            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();

        ItemStack item = event.getItem().getItemStack();
        ItemStack fixedItem = checkForcedStatsApply(item);

        if(fixedItem != item) {
            player.getInventory().addItem(fixedItem);

            event.getItem().remove();
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        if(player.getInventory().getItem(event.getNewSlot()) == null || player.getInventory().getItem(event.getNewSlot()).getType().equals(Material.AIR)) {
            return;
        }

        ItemStack item = player.getInventory().getItem(event.getNewSlot());
        ItemStack fixedItem = checkForcedStatsApply(item);

        if(fixedItem != item) {
            player.getInventory().setItem(event.getNewSlot(), fixedItem);
        }
    }

    private ItemStack checkForcedStatsApply(ItemStack item) {
        if(!ConfigFile.getConfig("config.yml").getBoolean("stats-settings.forced-stats.enabled")) {
            return item;
        }

        List<String> materialNames = new ArrayList<>();
        String matchedRpgItemKey = "";

        ConfigFile file = ConfigFile.getConfig(RPGCombatItem.FILE_NAME);
        for(String rpgItemKey : file.getRootKeys()) {
            String materialName = file.getString(rpgItemKey + ".material").toUpperCase();
            if(materialNames.contains(materialName)) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Forced stats is enabled in the configuration file, but there are duplicated item material names given in the " + RPGCombatItem.FILE_NAME + " file!");
                return item;
            }

            materialNames.add(materialName);

            if(Material.matchMaterial(materialName).equals(item.getType())) {
                matchedRpgItemKey = rpgItemKey;
            }
        }

        if(matchedRpgItemKey.isEmpty()) {
            return item;
        }

        double identifyRate = ConfigFile.getConfig("config.yml").getDouble("stats-settings.forced-stats.identify-rate");

        ItemStack rpgCombatItemStack = RPGCombatItem.build(matchedRpgItemKey);
        NBTItem nbtItem = new NBTItem(rpgCombatItemStack);
        RPGCombatItem rpgCombatItem = nbtItem.getObject("rpgdata_combatitem", RPGCombatItem.class);

        rpgCombatItem.setIdentified(RandomTools.getRandomPercentage() <= identifyRate, true, null, rpgCombatItemStack);

        return rpgCombatItem.update(rpgCombatItemStack);
    }
}