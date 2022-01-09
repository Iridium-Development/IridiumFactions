package com.iridium.iridiumfactions.listeners;

import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.UpgradeType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionUpgrade;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void monitorEntityDeath(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null) return;
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);
        Faction faction = user.getFaction();

        FactionUpgrade factionUpgrade = IridiumFactions.getInstance().getFactionManager().getFactionUpgrade(faction, UpgradeType.EXPERIENCE_UPGRADE);
        event.setDroppedExp((int) (event.getDroppedExp() * IridiumFactions.getInstance().getUpgrades().experienceUpgrade.upgrades.get(factionUpgrade.getLevel()).experienceModifier));
    }

}
