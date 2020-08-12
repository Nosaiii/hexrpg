package me.cheesyfreezy.hexrpg.listeners.inventory;

import de.tr7zw.hexrpg.nbtapi.NBTItem;
import me.cheesyfreezy.hexrpg.rpg.mechanics.CustomInventory;
import me.cheesyfreezy.hexrpg.rpg.mechanics.backpack.BackPackInventory;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class OnInBackpackClick implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if(!CustomInventory.hasCache(event.getInventory()) || !(CustomInventory.getInventoryObject(event.getInventory()) instanceof BackPackInventory)) {
            return;
        }

        if(event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) {
            return;
        }

        NBTItem nbtItem = new NBTItem(event.getCurrentItem());
        if (!nbtItem.hasKey("rpgdata_backpack")) {
            return;
        }

        event.setCancelled(true);

        player.closeInventory();
        player.sendMessage(LanguageManager.getMessage("backpacks.backpacks-stacking-not-allowed", player.getUniqueId(), true));
    }
}