package me.cheesyfreezy.hexrpg.rpg.items.other;

import de.tr7zw.hexrpg.nbtapi.NBTItem;
import me.cheesyfreezy.hexrpg.rpg.items.RPGItem;
import me.cheesyfreezy.hexrpg.rpg.mechanics.backpack.BackPackInventory;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Backpack extends RPGItem {
    public final static int MAX_SIZE = 6;

    private BackPackInventory inv;

    public Backpack(int size) {
        inv = new BackPackInventory(size);
        tmpItem = update(new ItemStack(Material.CHEST));
    }

    @Override
    public ItemStack update(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + LanguageManager.getGlobalMessage("backpack.item-name"));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLore(new ArrayList<>(Arrays.asList(
                ChatColor.GRAY + LanguageManager.getGlobalMessage("backpack.size") + ": " + getBackPackInventory().getSize()
        )));

        item.setItemMeta(meta);

        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setObject("rpgdata_backpack", this);
        return nbtItem.getItem();
    }

    public void setBackPackInventory(BackPackInventory backPackInventory) {
        this.inv = backPackInventory;
    }

    public BackPackInventory getBackPackInventory() {
        return inv;
    }
}