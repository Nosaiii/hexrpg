package me.cheesyfreezy.hexrpg.rpg.mechanics.languageselector;

import de.tr7zw.hexrpg.nbtapi.NBTItem;
import me.cheesyfreezy.hexrpg.main.Plugin;
import me.cheesyfreezy.hexrpg.rpg.mechanics.CustomInventory;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class LanguageSelector extends CustomInventory {
    public final static int ROWS_PER_PAGE = 1, PREVIOUS_PAGE_SLOT = 0, NEXT_PAGE_SLOT = 8;

    private int currentPage;

    public void open(Player player, int page) {
        this.currentPage = page;

        File languagesFolder = new File(Plugin.getMain().getDataFolder() + File.separator + "lang");
        File[] languageFiles = languagesFolder.listFiles();

        int pageCount = getPageCount();
        Inventory inv = Bukkit.getServer().createInventory(player, ROWS_PER_PAGE * 9 + 9, LanguageManager.getMessage("language-menu.title", player.getUniqueId()) + " (" + page + "/" + pageCount + ")");

        int startIndex = ROWS_PER_PAGE * 9 * (page - 1);
        int endIndex = startIndex + (ROWS_PER_PAGE * 9);
        for(int i = startIndex; i < endIndex; i++) {
            if(i >= languageFiles.length) {
                continue;
            }
            File languageFile = languageFiles[i];

            ItemStack languageOption = new ItemStack(Material.PAPER, 1);

            ItemMeta languageOptionMeta = languageOption.getItemMeta();
            languageOptionMeta.setDisplayName(ChatColor.RED + WordUtils.capitalizeFully(languageFile.getName().replaceFirst("[.][^.]+$", "")));
            languageOptionMeta.setLore(new ArrayList<>(Collections.singleton(ChatColor.GRAY + languageFile.getName())));
            languageOptionMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            languageOption.setItemMeta(languageOptionMeta);

            if(getSelectedLanguage(player.getUniqueId()).equals(languageFile.getName())) {
                languageOption.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            }

            NBTItem languageOptionNbtItem = new NBTItem(languageOption);
            languageOptionNbtItem.setString("hexrpg_language_file", languageFile.getName());

            inv.setItem(i - startIndex, languageOptionNbtItem.getItem());
        }

        ItemStack previousPage = new ItemStack(Material.ARROW), nextPage = new ItemStack(Material.ARROW);
        ItemMeta previousPageMeta = previousPage.getItemMeta(), nextPageMeta = nextPage.getItemMeta();
        previousPageMeta.setDisplayName(ChatColor.GREEN + LanguageManager.getMessage("literal-translations.previous-page", player.getUniqueId()));
        nextPageMeta.setDisplayName(ChatColor.GREEN + LanguageManager.getMessage("literal-translations.next-page", player.getUniqueId()));
        previousPage.setItemMeta(previousPageMeta);
        nextPage.setItemMeta(nextPageMeta);

        inv.setItem(ROWS_PER_PAGE * 9 + PREVIOUS_PAGE_SLOT, previousPage);
        inv.setItem(ROWS_PER_PAGE * 9 + NEXT_PAGE_SLOT, nextPage);

        player.openInventory(inv);
        addToCache(inv, this);
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public int getPageCount() {
        File languagesFolder = new File(Plugin.getMain().getDataFolder() + File.separator + "lang");
        return (int) Math.ceil((double) languagesFolder.listFiles().length / (ROWS_PER_PAGE * 9));
    }

    public void setSelectedLanguage(UUID uuid, String fileName) {
        File languageDataFile = new File(Plugin.getMain().getDataFolder() + File.separator + "data", "language_data.yml");
        YamlConfiguration languageDataConfig = YamlConfiguration.loadConfiguration(languageDataFile);

        languageDataConfig.set(uuid.toString(), fileName);
        try {
            languageDataConfig.save(languageDataFile);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public String getSelectedLanguage(UUID uuid) {
        File languageDataFile = new File(Plugin.getMain().getDataFolder() + File.separator + "data", "language_data.yml");
        YamlConfiguration languageDataConfig = YamlConfiguration.loadConfiguration(languageDataFile);

        if(languageDataConfig.isSet(uuid.toString())) {
            return languageDataConfig.getString(uuid.toString());
        }

        return Plugin.getMain().getConfig().getString("language-settings.default");
    }
}