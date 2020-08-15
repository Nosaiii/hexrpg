package me.cheesyfreezy.hexrpg.rpg.mechanics.backpack;

import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import me.cheesyfreezy.hexrpg.rpg.items.other.Backpack;
import me.cheesyfreezy.hexrpg.rpg.mechanics.CustomInventory;
import me.cheesyfreezy.hexrpg.tools.InventoryTools;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BackPackInventory extends CustomInventory {
    private String backpackItem;
    private HashMap<Integer, String> content;

    public BackPackInventory(int size) {
        content = new HashMap<>();
        for (int i = 0; i < size * 9; i++) {
            content.put(i, NBTItem.convertItemtoNBT(new ItemStack(Material.AIR)).toString());
        }
    }

    public void open(Player player) {
        backpackItem = NBTItem.convertItemtoNBT(player.getInventory().getItemInMainHand()).toString();

        Inventory inv = Bukkit.getServer().createInventory(player, content.size(), LanguageManager.getMessage("literal-translations.backpack", player.getUniqueId()));

        for (int i = 0; i < inv.getSize(); i++) {
            if(!content.containsKey(i)) {
                continue;
            }
            inv.setItem(i, NBTItem.convertNBTtoItem(new NBTContainer(content.get(i))));
        }

        player.openInventory(inv);
        addToCache(inv, this);
    }

    public void save(Inventory inv, Player player) {
        if(backpackItem == null || backpackItem.isEmpty()) {
            throw new NullPointerException("An error occured trying to save the contents of the backpack. Please report this to the admin or to the developer.");
        }

        ItemStack backpackItemStack = NBTItem.convertNBTtoItem(new NBTContainer(backpackItem));
        NBTItem backpackNbtItem = new NBTItem(backpackItemStack);
        Backpack backpack = backpackNbtItem.getObject("rpgdata_backpack", Backpack.class);

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = new ItemStack(Material.AIR);
            if(inv.getItem(i) != null && !inv.getItem(i).getType().equals(Material.AIR)) {
                item = inv.getItem(i);
            }
            content.put(i, NBTItem.convertItemtoNBT(item).toString());
        }

        int backpackSlot = InventoryTools.getInventorySlotByItem(player.getInventory(), backpackItemStack);

        if(backpackSlot == -1) {
            throw new IndexOutOfBoundsException("An error occured trying to save the contents of the backpack. Please report this to the admin or to the developer.");
        }

        backpackItem = null;
        backpack.setBackPackInventory(this);

        player.getInventory().setItem(backpackSlot, backpack.update(backpackItemStack));
    }

    public void setBackpackItem(ItemStack backpackItem) {
        this.backpackItem = NBTItem.convertItemtoNBT(backpackItem).toString();
    }

    public void setItem(int slot, ItemStack item) {
        content.put(slot, NBTItem.convertItemtoNBT(item).toString());
    }

    public ItemStack getItem(int slot) {
        return NBTItem.convertNBTtoItem(new NBTContainer(content.get(slot)));
    }

    public int getSize() {
        return (int) Math.ceil(content.size() / 9);
    }
}