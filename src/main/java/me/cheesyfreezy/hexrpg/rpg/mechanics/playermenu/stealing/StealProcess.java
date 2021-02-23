package me.cheesyfreezy.hexrpg.rpg.mechanics.playermenu.stealing;

import java.util.function.BiConsumer;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.main.HexRPGPlugin;
import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import me.cheesyfreezy.hexrpg.tools.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import me.cheesyfreezy.hexrpg.tools.RandomTools;

public class StealProcess {
	@Inject private HexRPGPlugin plugin;

	private final Player stealer;
	private final Player victim;
	
	private BiConsumer<Player, Player> onSuccess;
	private BiConsumer<Player, Player> onFailed;
	private BiConsumer<Player, Player> onStop;
	
	private int taskId;
	
	private final int progressGoal;
	private int progress;
	private BossBar progressBar;
	
	public StealProcess(Player stealer, Player victim, int ticks) {
		this.stealer = stealer;
		this.victim = victim;
		
		taskId = -1;
		
		progressGoal = ticks;
		progress = 0;
	}
	
	public void start() {
		progressBar = Bukkit.getServer().createBossBar(LanguageManager.getMessage("player-menu.stealing.in-progress", stealer.getUniqueId(), true), BarColor.RED, BarStyle.SOLID);
		progressBar.addPlayer(stealer);
		progressBar.setVisible(true);
		
		taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			progressBar.setProgress(1.0d / progressGoal * progress);
			
			if(progress % 5 == 0) {
				stealer.getWorld().playSound(stealer.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 0.3f, 2);
			}
			
			if(progress >= progressGoal) {
				attemptSteal();
				stop(false);
				return;
			}
			
			progress++;
		}, 0, 1);
	}
	
	public void stop(boolean forced) {
		if(taskId != -1) {
			Bukkit.getServer().getScheduler().cancelTask(taskId);
		}
		
		progressBar.removeAll();
		progressBar.setVisible(false);
		
		if(forced && stealer != null && stealer.isOnline()) {
			stealer.sendMessage(LanguageManager.getMessage("player-menu.stealing.stealing-interrupted", stealer.getUniqueId(), true));
		}
		
		if(onStop != null) {
			onStop.accept(stealer, victim);
		}
	}
	
	private StealResult attemptSteal() {
		if(stealer != null && stealer.isOnline() && victim != null && victim.isOnline()) {
			double r = RandomTools.getRandomPercentage();
			
			if(r <= ConfigFile.getConfig("config.yml").getDouble("stealing.success-rate")) {
				if(onSuccess != null) {
					onSuccess.accept(stealer, victim);
				}
				
				return StealResult.SUCCEEDED;
			} else {
				if(onFailed != null) {
					onFailed.accept(stealer, victim);
				}
				
				return StealResult.FAILED;
			}
		} else {
			stop(true);
		}
		
		if(onFailed != null) {
			onFailed.accept(stealer, victim);
		}
		
		return StealResult.FAILED;
	}
	
	public Player getStealer() {
		return stealer;
	}

	public Player getVictim() {
		return victim;
	}

	public void onSucceeded(BiConsumer<Player, Player> consumer) {
		onSuccess = consumer;
	}
	
	public void onFailed(BiConsumer<Player, Player> consumer) {
		onFailed = consumer;
	}
	
	public void onStop(BiConsumer<Player, Player> consumer) {
		onStop = consumer;
	}
}