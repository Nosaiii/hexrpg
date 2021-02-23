package me.cheesyfreezy.hexrpg.rpg.quests.reward;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class QuestRewardItem implements IQuestReward {
    private String materialName;
    private int amount;

    public QuestRewardItem(String materialName, int amount) {
        this.materialName = materialName;
        this.amount = amount;
    }

    public ItemStack buildItem() {
        return new ItemStack(Material.matchMaterial(materialName), amount);
    }

    @Override
    public void reward(Player player) {
        for(ItemStack dropItem : player.getInventory().addItem(buildItem()).values()) {
            player.getWorld().dropItem(player.getEyeLocation(), dropItem);
        }
    }

    @Override
    public String getLabel() {
        return WordUtils.capitalizeFully(buildItem().getType().toString().replace("_", " ")) + " (" + amount + "x)";
    }
}
