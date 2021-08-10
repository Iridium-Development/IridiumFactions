package com.iridium.iridiumfactions.listeners;

import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);

        // Update the internal username in case of name change
        user.setName(event.getPlayer().getName());
    }

}
