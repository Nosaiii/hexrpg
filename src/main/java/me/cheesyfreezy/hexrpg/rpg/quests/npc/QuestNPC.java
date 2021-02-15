package me.cheesyfreezy.hexrpg.rpg.quests.npc;

import de.tr7zw.changeme.nbtapi.NBTEntity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public class QuestNPC {
    private final int id;
    private final String name;
    private final Villager.Profession profession;

    public QuestNPC(int id, String name, Villager.Profession profession) {
        this.id = id;
        this.name = name;
        this.profession = profession;
    }

    /**
     * Instantiates an NPC using this template at a given location
     * @param location The location to spawn the instance NPC at
     */
    public void spawnInstance(Location location) {
        Villager npc = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);

        // Naming
        npc.setCustomName(ChatColor.GOLD + name);
        npc.setCustomNameVisible(true);

        // Appearance
        npc.setAdult();
        npc.setProfession(profession);

        // Behaviour
        npc.setAI(false);
        npc.setLootTable(null);

        NBTEntity nbtEntity = new NBTEntity(npc);
        nbtEntity.getPersistentDataContainer().setInteger("quest_npc_id", id);
    }

    /**
     * The identifier of the NPC
     * @return The identifier of the NPC
     */
    public int getId() {
        return id;
    }

    /**
     * The name of the NPC
     * @return The name of the NPC
     */
    public String getName() {
        return name;
    }
}