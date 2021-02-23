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

    protected final int id;

    private final HashMap<UUID, List<Listener>> registeredListeners;
    private final List<Quest> questObservers;

    public QuestStep(int id) {
        this.id = id;

        registeredListeners = new HashMap<>();
        questObservers = new ArrayList<>();
    }

    /**
     * Starts this quest step for the given player
     * @param player The player to start the quest step for
     */
    public abstract void start(Player player);

    /**
     * Finishes off this quest step for the given player
     * @param player The player to finish the quest step off for
     */
    public abstract void finish(Player player);

    /**
     * Forces the player to exit out of this quest step. Do not manually call this. This is automatically called when a player logs off
     * @param player The player to force exit this quest step for
     */
    public abstract void forceQuit(Player player);

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

    /**
     * Subscribes to the quest to the observer pattern of this instance of a quest step. This method should not be called manually.
     * @param quest The quest to add as an observer for this quest step
     */
    public void subscribeObserver(Quest quest) {
        if(questObservers.contains(quest)) {
            return;
        }
        questObservers.add(quest);
    }

    /**
     * Continues the sequence of quest steps of the quest(s) this quest step is subscribed to for the given player. This method calls the {@code finish} method of the current step and the {@code start} method of the next quest step
     * @param player The player to continue the quest for
     */
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