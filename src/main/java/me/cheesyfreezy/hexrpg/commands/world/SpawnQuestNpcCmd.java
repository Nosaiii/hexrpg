package me.cheesyfreezy.hexrpg.commands.world;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.exceptions.quests.QuestNPCNotFoundException;
import me.cheesyfreezy.hexrpg.rpg.quests.QuestService;
import me.cheesyfreezy.hexrpg.rpg.quests.npc.QuestNPC;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import me.cheesyfreezy.hexrpg.tools.PrimitiveTypeTools;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpawnQuestNpcCmd implements CommandExecutor {
    @Inject private QuestService questService;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String command = cmd.getName();

        if (command.equalsIgnoreCase("SpawnQuestNPC")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (!player.hasPermission("hexrpg.spawnquestnpc")) {
                    player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.not-allowed", player.getUniqueId(), true));
                    return true;
                }

                if(args.length == 0) {
                    player.sendMessage(ChatColor.RED + "/GiveRPGItem [NPC_Id] <x> <y> <z>");

                    List<String> formattedNpcList = new ArrayList<>();
                    for(QuestNPC npc : questService.getNPCs()) {
                        formattedNpcList.add(ChatColor.BLUE + npc.getName() + ": " + ChatColor.RED + npc.getId());
                    }
                    player.sendMessage(String.join(", ", formattedNpcList));
                } else if(args.length <= 4) {
                    // Quest NPC validation
                    if(!PrimitiveTypeTools.isInt(args[0])) {
                        player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.invalid-number", player.getUniqueId(), true));
                        return true;
                    }
                    int npcId = Integer.parseInt(args[0]);

                    QuestNPC questNPC = null;
                    try {
                        questNPC = questService.getNPC(npcId);
                    } catch(QuestNPCNotFoundException questNPCNotFoundException) {
                        player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.quests.invalid-npc", player.getUniqueId(), true));
                        return true;
                    }

                    // Location validation
                    Location location = player.getLocation();
                    if(args.length >= 2) {
                        if(!PrimitiveTypeTools.isInt(args[1])) {
                            player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.invalid-number", player.getUniqueId(), true));
                            return true;
                        }
                        location.setX(Integer.parseInt(args[1]));
                    }
                    if(args.length >= 3) {
                        if(!PrimitiveTypeTools.isInt(args[2])) {
                            player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.invalid-number", player.getUniqueId(), true));
                            return true;
                        }
                        location.setY(Integer.parseInt(args[2]));
                    }
                    if(args.length >= 4) {
                        if(!PrimitiveTypeTools.isInt(args[3])) {
                            player.sendMessage(LanguageManager.getMessage("command-and-chat-execution.invalid-number", player.getUniqueId(), true));
                            return true;
                        }
                        location.setZ(Integer.parseInt(args[3]));
                    }

                    questNPC.spawnInstance(location);
                }
            } else if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(LanguageManager.getMessage("command-and-chat-execution.player-only-command", null, true));
            }
        }

        return false;
    }
}
