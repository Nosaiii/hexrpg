package me.cheesyfreezy.hexrpg.rpg.quests;

import me.cheesyfreezy.hexrpg.exceptions.quests.QuestNPCNotFoundException;
import me.cheesyfreezy.hexrpg.exceptions.quests.QuestNotFoundException;
import me.cheesyfreezy.hexrpg.rpg.quests.npc.QuestNPC;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuestService {
    private final List<Quest> quests = new ArrayList<>();
    private final List<QuestNPC> questNPCs = new ArrayList<>();

    /**
     * Registrates a {@link Quest} object to this service
     * @param quest The {@link Quest} object to registrate
     */
    public void registerQuest(Quest quest) {
        quests.add(quest);
    }

    /**
     * Registrates a {@link QuestNPC} object to this service
     * @param npc The {@link QuestNPC} object to registrate
     */
    public void registerNPC(QuestNPC npc) {
        questNPCs.add(npc);
    }

    /**
     * Retrieves the quest with the given identifier from this service
     * @param id The identifier of the quest to retrieve
     * @return A {@link Quest} object
     * @throws QuestNotFoundException Thrown when the quest was not found
     */
    public Quest getQuest(int id) throws QuestNotFoundException {
        Optional<Quest> quest = quests.stream().filter(q -> q.getId() == id).findFirst();
        if(!quest.isPresent()) {
            throw new QuestNotFoundException(id);
        }
        return quest.get();
    }

    /**
     * Retrieves the NPC with the given identifier from this service
     * @param id The identifier of the NPC to retrieve
     * @return A {@link QuestNPC} object
     * @throws QuestNPCNotFoundException Thrown when the NPC was not found
     */
    public QuestNPC getNPC(int id) throws QuestNPCNotFoundException {
        Optional<QuestNPC> npc = questNPCs.stream().filter(q -> q.getId() == id).findFirst();
        if(!npc.isPresent()) {
            throw new QuestNPCNotFoundException(id);
        }
        return npc.get();
    }

    /**
     * Returns an array containing all registered quests
     * @return An array with {@link Quest} objects
     */
    public Quest[] getQuests() {
        return (Quest[]) quests.toArray();
    }

    /**
     * Returns an array containing all registered NPCs
     * @return An array with {@link QuestNPC} objects
     */
    public QuestNPC[] getNPCs() {
        return (QuestNPC[]) questNPCs.toArray();
    }
}