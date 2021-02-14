package me.cheesyfreezy.hexrpg.rpg.quests;

import java.util.ArrayList;
import java.util.List;

public class QuestService {
    private final List<Quest> quests = new ArrayList<>();

    public void registerQuest(Quest quest) {
        quests.add(quest);
    }
}