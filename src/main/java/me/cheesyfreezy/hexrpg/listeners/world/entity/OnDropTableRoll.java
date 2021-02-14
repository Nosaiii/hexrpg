package me.cheesyfreezy.hexrpg.listeners.world.entity;

import com.google.inject.Inject;
import me.cheesyfreezy.hexrpg.rpg.tools.RupeeTools;
import me.cheesyfreezy.hexrpg.tools.ConfigFile;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import me.cheesyfreezy.hexrpg.rpg.mechanics.droptable.DropTable;

public class OnDropTableRoll implements Listener {
	@Inject private Economy economy;

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (
				(event.getEntity() instanceof Player && ConfigFile.getConfig("config.yml").getBoolean("death-settings.clear-player-drops")) ||
				!(event.getEntity() instanceof Player && ConfigFile.getConfig("config.yml").getBoolean("death-settings.clear-other-drops"))
		) {
			event.getDrops().clear();
		}

		EntityType entityType = event.getEntityType();
		if (!ConfigFile.getConfig("drop_table.yml").isConfigurationSection(entityType.toString().toLowerCase())) {
			entityType = null;
		}

		DropTable dropTable = new DropTable(entityType);
		for (ItemStack drop : dropTable.roll()) {
			if(RupeeTools.isRupee(drop) && event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player) {
				Player killer = event.getEntity().getKiller();
				economy.depositPlayer(killer, drop.getAmount());

				continue;
			}

			event.getDrops().add(drop);
		}
	}
}