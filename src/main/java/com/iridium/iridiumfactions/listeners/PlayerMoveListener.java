package com.iridium.iridiumfactions.listeners;

import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getTo() == null) return;
        if (event.getFrom().getChunk().equals(event.getTo().getChunk())) return;
        Faction fromFaction = IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(event.getFrom()).orElse(null);
        Faction toFaction = IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(event.getTo()).orElse(null);
        if (fromFaction != toFaction) {
            if (toFaction != null) {
                IridiumFactions.getInstance().getNms().sendTitle(player, toFaction.getName(), "", 20, 40, 20);
            } else {
                IridiumFactions.getInstance().getNms().sendTitle(player, "Wilderness", "", 20, 40, 20);
            }
        }
    }

}
