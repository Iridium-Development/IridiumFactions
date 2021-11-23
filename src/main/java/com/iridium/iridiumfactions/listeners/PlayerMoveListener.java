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
        Faction fromFaction = IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(event.getFrom());
        Faction toFaction = IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(event.getTo());
        if (fromFaction.getId() != toFaction.getId()) {
            RelationshipType relationshipType = IridiumFactions.getInstance().getFactionManager().getFactionRelationship(user, toFaction);
            IridiumFactions.getInstance().getNms().sendTitle(player, StringUtils.color(relationshipType.getColor()) + toFaction.getName(), StringUtils.color(ChatColor.GRAY + toFaction.getDescription()), 20, 40, 20);
        }
    }

}
