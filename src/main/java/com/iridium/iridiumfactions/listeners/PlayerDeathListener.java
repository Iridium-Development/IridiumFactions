package com.iridium.iridiumfactions.listeners;

import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.configs.Configuration;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Configuration configuration = IridiumFactions.getInstance().getConfiguration();
        User user = IridiumFactions.getInstance().getUserManager().getUser(event.getEntity());
        user.setPower(Math.max(user.getPower() - configuration.powerLossPerDeath, configuration.minPower));
    }
}
