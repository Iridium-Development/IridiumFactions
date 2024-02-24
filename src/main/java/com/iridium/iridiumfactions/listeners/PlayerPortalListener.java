package com.iridium.iridiumfactions.listeners;

import com.iridium.iridiumfactions.IridiumFactions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class PlayerPortalListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (IridiumFactions.getInstance().getConfiguration().disablePortals) {
            event.setCancelled(true);
        }
    }
}
