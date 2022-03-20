package com.iridium.iridiumfactions.listeners;

import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void monitorPlayerDeath(PlayerDeathEvent event) {
        User user = IridiumFactions.getInstance().getUserManager().getUser(event.getEntity());
        user.setPower(user.getPower() - IridiumFactions.getInstance().getConfiguration().powerLossPerDeath);
        user.setPower(Math.max(user.getPower(), IridiumFactions.getInstance().getConfiguration().minPower));
    }

}
