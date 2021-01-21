package me.cheesyfreezy.hexrpg.listeners.item;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.cheesyfreezy.hexrpg.rpg.items.other.Backpack;
import me.cheesyfreezy.hexrpg.rpg.tools.Feature;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class OnBackpackOpen implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!Feature.getFeature("backpacks").isEnabled()) {
            return;
        }

        Player player = event.getPlayer();

        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        if (mainHandItem == null || mainHandItem.getType().equals(Material.AIR)) {
            return;
        }

        NBTItem nbtItem = new NBTItem(mainHandItem);
        if (!nbtItem.hasKey("rpgdata_backpack")) {
            return;
        }
        Backpack backpack = nbtItem.getObject("rpgdata_backpack", Backpack.class);

        backpack.getBackPackInventory().open(player);
    }
}