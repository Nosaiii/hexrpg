package me.cheesyfreezy.hexrpg.rpg.quests.steps;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import me.cheesyfreezy.hexrpg.rpg.quests.Quest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class QuestStep {
    @Inject private HexRPGPlugin plugin;

    private final int id;

    private final HashMap<UUID, List<Listener>> registeredListeners;
    private final List<Quest> questObservers;

    public QuestStep(int id) {
        this.id = id;

        registeredListeners = new HashMap<>();
        questObservers = new ArrayList<>();
    }

    public abstract void start(Player player);
    public abstract void finish(Player player);

    /**
     * Registrates a new listener to a UUID for this quest step
     * @param uuid The UUID of the player to registrate the listener for
     * @param listener The listener to registrate for the player
     */
    public void registerListener(UUID uuid, Listener listener) {
        List<Listener> listeners = new ArrayList<>();
        if(registeredListeners.containsKey(uuid)) {
            listeners = registeredListeners.get(uuid);
        }

        listeners.add(listener);
        registeredListeners.put(uuid, listeners);

        Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    /**
     * Unregistrates all existing listeners with the given class of a UUID
     * @param uuid The UUID of the player to unregistrate the listener for
     * @param clazz The class of the listener to unregistrate
     * @param <T> The type of the listener to unregistrate
     */
    public <T extends Listener> void unregisterListener(UUID uuid, Class<T> clazz) {
        List<Listener> listenersToRemove = registeredListeners.get(uuid).stream()
                .filter(l -> l.getClass() == clazz)
                .collect(Collectors.toList());

        for(Listener listenerToRemove : listenersToRemove) {
            HandlerList.unregisterAll(listenerToRemove);
        }
    }

    public void subscribeObserver(Quest quest) {
        if(questObservers.contains(quest)) {
            return;
        }
        questObservers.add(quest);
    }

    public void onNext(Player player) {
        for(Quest quest : questObservers) {
            quest.nextStep(player);
        }
    }

    /**
     * The identifier of the quest step
     * @return The identifier of the quest step
     */
    public int getId() {
        return id;
    }
}