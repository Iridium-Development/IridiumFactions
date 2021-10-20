package com.iridium.iridiumfactions.listeners;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.RelationshipType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);
        if (event.getTo() == null) return;
        if (event.getFrom().getChunk().equals(event.getTo().getChunk())) return;
        Faction fromFaction = IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(event.getFrom()).orElse(null);
        Faction toFaction = IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(event.getTo()).orElse(null);
        if (fromFaction != toFaction) {
            if (toFaction != null) {
                RelationshipType relationshipType = IridiumFactions.getInstance().getFactionManager().getFactionRelationship(user, toFaction);
                IridiumFactions.getInstance().getNms().sendTitle(player, StringUtils.color(relationshipType.getColor()) + toFaction.getName(), StringUtils.color(ChatColor.GRAY + toFaction.getDescription()), 20, 40, 20);
            } else {
                IridiumFactions.getInstance().getNms().sendTitle(player, StringUtils.color("&2Wilderness"), StringUtils.color(""), 20, 40, 20);
            }
        }
    }

}
