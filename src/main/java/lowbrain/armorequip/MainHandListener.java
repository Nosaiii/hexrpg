package lowbrain.armorequip;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MainHandListener implements Listener {

    private final List<String> blockedMaterials;

    public MainHandListener(List<String> blockedMaterials){
        this.blockedMaterials = blockedMaterials;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        boolean shift = false, numberkey = false, dropping = false;

        if(e.isCancelled() || !(e.getWhoClicked() instanceof Player) || e.getSlotType() == InventoryType.SlotType.OUTSIDE)
            return;

        if ((!e.getInventory().getType().equals(InventoryType.CRAFTING)
                && !e.getInventory().getType().equals(InventoryType.WORKBENCH)))
            return;

        if(e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT))
            shift = true;

        if(e.getClick().equals(ClickType.NUMBER_KEY))
            numberkey = true;

        if (e.getClick().equals(ClickType.DROP) || e.getClick().equals(ClickType.CONTROL_DROP))
            dropping = true;

        ItemStack currentItem = e.getCurrentItem();
        ItemStack cursorItem = e.getCursor();

        // consider AIR as null
        if (currentItem != null && currentItem.getType().equals(Material.AIR))
            currentItem = null;

        if (cursorItem != null && cursorItem.getType().equals(Material.AIR))
            cursorItem = null;

        if (currentItem == null && cursorItem == null)
            return;

        int holdslot = e.getWhoClicked().getInventory().getHeldItemSlot();
        int clicked = e.getRawSlot();

        // dropping using Q shortcut in inventory
        if (dropping) {

            if (e.getSlotType().equals(InventoryType.SlotType.QUICKBAR)
                    && e.getSlot() == holdslot
                    && currentItem.getAmount() == 1) {

                MainHandEvent event = new MainHandEvent(
                        (Player)e.getWhoClicked(),
                        MainHandEvent.HandMethod.DROP,
                        e.getCurrentItem(), null);

                Bukkit.getServer().getPluginManager().callEvent(event);

                if(event.isCancelled())
                    e.setCancelled(true);

            }

        } else if (shift) {
            // unequipped current held item
            if (clicked - 36 == holdslot) {
                // holdslots are from 0 to 8 instead of 36 to 44
                MainHandEvent event = new MainHandEvent((Player)e.getWhoClicked(), MainHandEvent.HandMethod.HELD_SWAP, e.getCurrentItem(), null);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if(event.isCancelled())
                    e.setCancelled(true);

            } else {
                int empty = -1;

                // no need when shift clicking in the quickbar
                if (e.getSlotType() == InventoryType.SlotType.QUICKBAR)
                    return;

                // quick bar inventory slots from 0 to 8
                // when shift clicking the inventory, the item will go to the first empty slot
                if (e.getSlotType() == InventoryType.SlotType.RESULT) {

                    // DO NOTHING, HANDLED IN ITEMCRAFTEDEVENT

                } else {
                    for (int i = 0; i <= 8; i++) {
                        ItemStack item = e.getWhoClicked().getInventory().getItem(i);

                        if (item == null || item.getType().equals(Material.AIR)) {
                            empty = i;
                            break;
                        }
                    }
                }

                if (empty == holdslot) {
                    MainHandEvent event = new MainHandEvent((Player)e.getWhoClicked(), MainHandEvent.HandMethod.HELD_SWAP, currentItem, null);
                    Bukkit.getServer().getPluginManager().callEvent(event);

                    if(event.isCancelled())
                        e.setCancelled(true);
                }
            }

        } else {

            ItemStack newItem = null;
            ItemStack oldItem = null;

            if (numberkey) {

                // e.getHotbarButton() = the number clicked from 0 to 8
                // clicked = the actual cursor position
                // clicking the number of the cursor position doesnt do anything so return.
                if (e.getHotbarButton() == clicked - 36)
                    return;

                if (e.getHotbarButton() == holdslot) {
                    newItem = e.getCursor();
                    oldItem = e.getWhoClicked().getInventory().getItem(e.getHotbarButton());
                } else if (clicked - 36 == holdslot){
                    oldItem = e.getCursor();
                    newItem = e.getWhoClicked().getInventory().getItem(e.getHotbarButton());
                }

                if (oldItem == null || newItem == null)
                    return;

                MainHandEvent event = new MainHandEvent(
                        (Player)e.getWhoClicked(),
                        MainHandEvent.HandMethod.HOT_BAR,
                        oldItem, newItem);

                Bukkit.getServer().getPluginManager().callEvent(event);

                if(event.isCancelled())
                    e.setCancelled(true);

            } else if (e.getSlot() == holdslot) {

                newItem = cursorItem;
                oldItem = currentItem;

                MainHandEvent event = new MainHandEvent(
                        (Player)e.getWhoClicked(),
                        MainHandEvent.HandMethod.HOT_BAR,
                        oldItem, newItem);

                Bukkit.getServer().getPluginManager().callEvent(event);

                if(event.isCancelled())
                    e.setCancelled(true);

            }
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent e) {
        if (e.isCancelled())
            return;

        if (!(e.getWhoClicked() instanceof Player))
            return;

        if(!e.getClick().equals(ClickType.SHIFT_LEFT) && !e.getClick().equals(ClickType.SHIFT_RIGHT))
            return; // only handle shift click

        Player player = ((Player) e.getWhoClicked());

        ItemStack currentItem = e.getCurrentItem();

        if (currentItem == null || currentItem.getType().equals(Material.AIR))
            return;

        boolean fits = fitsInInventory(player, currentItem, e);

        int empty = -1;

        for (int i = 8; i >= 0 && !fits; i--) {
            ItemStack item = e.getWhoClicked().getInventory().getItem(i);

            if (item == null || item.getType().equals(Material.AIR)) {
                empty = i;
                break;
            }
        }

        if (empty == player.getInventory().getHeldItemSlot()) {
            MainHandEvent event = new MainHandEvent(player,
                    MainHandEvent.HandMethod.CRAFTED,
                    null, currentItem);

            Bukkit.getServer().getPluginManager().callEvent(event);

            if(event.isCancelled())
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwitchOffMain(PlayerSwapHandItemsEvent e) {
        if (e.isCancelled())
            return;

        ItemStack newItem = e.getMainHandItem();
        ItemStack oldItem = e.getOffHandItem();

        if (newItem == null || newItem.getType().equals(Material.AIR))
            newItem = null;

        if (oldItem == null || oldItem.getType().equals(Material.AIR))
            oldItem = null;

        if (oldItem == null && newItem == null)
            return;

        MainHandEvent event = new MainHandEvent(
                e.getPlayer(),
                MainHandEvent.HandMethod.HELD_SWITCH,
                oldItem, newItem);

        Bukkit.getServer().getPluginManager().callEvent(event);

        if(event.isCancelled())
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        // TODO
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent e) {
        // TODO
    }

    @EventHandler
    public void onInventoryPickup(PlayerPickupItemEvent e) {
        if (e.isCancelled()
                || e.getItem() == null
                || e.getItem().getItemStack() == null
                || e.getItem().getItemStack().getType().equals(Material.AIR))
            return;

        ItemStack item = e.getItem().getItemStack();

        boolean fits = fitsInInventory(e.getPlayer(), item, e);

        int empty = -1;

        for (int i = 0; i <= 8 && !fits; i++) {
            ItemStack t = e.getPlayer().getInventory().getItem(i);

            if (t == null || t.getType().equals(Material.AIR)) {
                empty = i;
                break;
            }
        }

        if (empty == e.getPlayer().getInventory().getHeldItemSlot()) {
            MainHandEvent event = new MainHandEvent(e.getPlayer(), MainHandEvent.HandMethod.PICK_UP, null, item);
            Bukkit.getServer().getPluginManager().callEvent(event);

            if(event.isCancelled())
                e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemHeld(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();

        int prev = e.getPreviousSlot();
        int next = e.getNewSlot();

        ItemStack newItem = player.getInventory().getItem(next);
        ItemStack oldItem = player.getInventory().getItem(prev);

        if (newItem == null && oldItem == null)
            return;

        MainHandEvent event = new MainHandEvent(player, MainHandEvent.HandMethod.HELD_SWAP, oldItem, newItem);

        Bukkit.getServer().getPluginManager().callEvent(event);

        if(event.isCancelled())
            e.setCancelled(true);
    }

    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent e) {
        Player player = e.getPlayer();

        ArmorType type = ArmorType.matchType(e.getBrokenItem());

        if (!type.equals(ArmorType.OFF_HAND))
            return;

        MainHandEvent event = new MainHandEvent(player, MainHandEvent.HandMethod.BROKE, e.getBrokenItem(), null);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent e){
        Player p = e.getEntity();

        Bukkit.getServer().getPluginManager().callEvent(
                new MainHandEvent(
                        p,
                        MainHandEvent.HandMethod.DEATH,
                        p.getInventory().getItemInMainHand(),
                        null));
    }

    private boolean fitsInInventory(HumanEntity who, ItemStack item, Event e) {
        boolean fits = false;

        if (item == null || item.getType().equals(Material.AIR) || item.getMaxStackSize() == 1)
            return false;

        int amount = item.getAmount();

        if (e instanceof CraftItemEvent)
            amount = getCraftAmount((CraftItemEvent)e);

        for (int i = 9; i < who.getInventory().getSize() && !fits; i++) {
            ItemStack t = who.getInventory().getItem(i);

            if (t == null || t.getType().equals(Material.AIR) || t.getAmount() == t.getMaxStackSize())
                continue;

            if (t.isSimilar(item))
                amount = amount - (t.getMaxStackSize() - t.getAmount());

            Main.instance.getLogger().info("left amount : " + amount);

            if (amount <= 0)
                fits = true;
        }

        return fits;
    }

    /**
     * Get the amount of items the player had just crafted.
     * This method will take into consideration shift clicking &
     * the amount of inventory space the player has left.
     * @param e
     * @return int: actual crafted item amount
     */
    private int getCraftAmount(CraftItemEvent e) {

        if(e.isCancelled()) { return 0; }

        Player p = (Player) e.getWhoClicked();

        if (e.isShiftClick()) {
            int itemsChecked = 0;
            int possibleCreations = 1;

            int amountCanBeMade = 0;

            for (ItemStack item : e.getInventory().getMatrix()) {
                if (item != null && item.getType() != Material.AIR) {
                    if (itemsChecked == 0) {
                        possibleCreations = item.getAmount();
                        itemsChecked++;
                    } else {
                        possibleCreations = Math.min(possibleCreations, item.getAmount());
                    }
                }
            }

            int amountOfItems = e.getRecipe().getResult().getAmount() * possibleCreations;

            ItemStack i = e.getRecipe().getResult();

            for(int s = 0; s <= 35; s++) {
                ItemStack test = p.getInventory().getItem(s);
                if(test == null || test.getType() == Material.AIR) {
                    amountCanBeMade+= i.getMaxStackSize();
                    continue;
                }
                if(test.isSimilar(i)) {
                    amountCanBeMade += i.getMaxStackSize() - test.getAmount();
                }
            }

            return amountOfItems > amountCanBeMade ? amountCanBeMade : amountOfItems;
        } else {
            return e.getRecipe().getResult().getAmount();
        }
    }
}
