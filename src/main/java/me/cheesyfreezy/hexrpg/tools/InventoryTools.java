package me.cheesyfreezy.hexrpg.tools;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryTools {
    public static int getInventorySlotByItem(Inventory inv, ItemStack item) {
        for (int i = 0; i < inv.getSize(); i++) {
            if(inv.getItem(i) == null || inv.getItem(i).getType().equals(Material.AIR) || !inv.getItem(i).equals(item)) {
                continue;
            }

            return i;
        }

        return -1;
    }
}