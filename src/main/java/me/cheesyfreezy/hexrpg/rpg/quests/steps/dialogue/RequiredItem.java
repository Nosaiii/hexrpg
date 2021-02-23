package me.cheesyfreezy.hexrpg.rpg.quests.steps.dialogue;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RequiredItem {
    private final Material material;
    private final int amount;

    public RequiredItem(Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }

    public boolean matches(ItemStack item) {
        return item.getType().equals(material) && item.getAmount() >= amount;
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }
}