package me.cheesyfreezy.hexrpg.rpg.mechanics.droptable;

import java.util.*;
import java.util.stream.Collectors;

import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import me.cheesyfreezy.hexrpg.tools.PrimitiveTypeTools;
import me.cheesyfreezy.hexrpg.tools.RandomTools;

public class DropTable {
    private final EntityType entityType;
    private final boolean overrideDefault;
    private final int rolls;

    private final DropTableItem[] items;

    public DropTable(EntityType entityType) {
        this.entityType = entityType;

        String configKey = "default";
        if (entityType != null) {
            configKey = entityType.toString().toLowerCase();
        }

        String cPath = configKey + ".";
        ConfigFile config = ConfigFile.getConfig("drop_table.yml");

        overrideDefault = config.getBoolean(cPath + "override-default");
        rolls = config.getInteger(cPath + "rolls");

        ArrayList<DropTableItem> itemsTmp = new ArrayList<>();

        List<String> itemList = config.getStringList(cPath + "drops");
        for (String item : itemList) {
            String[] itemData = item.split(":");

            if (!PrimitiveTypeTools.isDouble(itemData[0])) {
                throw new ClassCastException("An error occured trying to parse a drop table. The amount for the item '" + itemData[1] + "' is invalid!");
            }
            double dropRate = Double.parseDouble(itemData[0]);

            String[] amountData = itemData[2].split("-", 2);
            int[] amounts;
            if (amountData.length > 1) {
                if (PrimitiveTypeTools.isInt(amountData[0]) && PrimitiveTypeTools.isInt(amountData[1])) {
                    amounts = new int[] {
                            Integer.parseInt(amountData[0]),
                            Integer.parseInt(amountData[1])
                    };
                } else {
                    throw new ClassCastException("An error occured trying to parse a drop table. The amount for the item '" + itemData[2] + "' is invalid!");
                }
            } else {
                if (PrimitiveTypeTools.isInt(itemData[2])) {
                    amounts = new int[]{Integer.parseInt(itemData[2])};
                } else {
                    throw new ClassCastException("An error occured trying to parse a drop table. The amount for the item '" + itemData[2] + "' is invalid!");
                }
            }

            if (amounts.length == 1) {
                itemsTmp.add(new DropTableItem(dropRate, itemData[1], amounts[0]));
            } else {
                itemsTmp.add(new DropTableItem(dropRate, itemData[1], amounts[0], amounts[1]));
            }
        }

        items = itemsTmp.toArray(new DropTableItem[itemsTmp.size()]);
    }

    public ItemStack[] roll() {
        ArrayList<ItemStack> rolledItems = new ArrayList<>();

        if (!overrideDefault && entityType != null) {
            Collections.addAll(rolledItems, new DropTable(null).roll());
        }

        for (int i = 0; i < rolls; i++) {
            double r = RandomTools.getRandomPercentage();

            List<DropTableItem> eligibleItems = Arrays.stream(items).filter(dti -> r <= dti.getDropRate()).collect(Collectors.toList());
            if (eligibleItems.isEmpty()) {
                continue;
            }

            DropTableItem rolledDropTableItem = eligibleItems.get(new Random().nextInt(eligibleItems.size()));
            rolledItems.add(rolledDropTableItem.buildItem());
        }

        return rolledItems.toArray(new ItemStack[rolledItems.size()]);
    }

    public boolean doesOverrideDefault() {
        return overrideDefault;
    }

    public int getRolls() {
        return rolls;
    }

    public DropTableItem[] getItems() {
        return items;
    }
}