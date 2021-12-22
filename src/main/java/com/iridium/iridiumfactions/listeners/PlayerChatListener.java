package com.iridium.iridiumfactions.listeners;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionChatType;
import com.iridium.iridiumfactions.FactionType;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.RelationshipType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        User user = IridiumFactions.getInstance().getUserManager().getUser(event.getPlayer());
        Faction yourFaction = user.getFaction();
        if (user.getFactionChatType() == FactionChatType.NONE || yourFaction.getFactionType() != FactionType.PLAYER_FACTION) {
            return;
        }
        for (Player player : event.getRecipients()) {
            Faction faction = IridiumFactions.getInstance().getUserManager().getUser(player).getFaction();
            if (faction.getFactionType() != FactionType.PLAYER_FACTION) continue;
            RelationshipType relationshipType = IridiumFactions.getInstance().getFactionManager().getFactionRelationship(faction, yourFaction);
            if (user.getFactionChatType().getRelationshipType().contains(relationshipType)) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionChatFormat
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%relationship_format%", relationshipType.getColor())
                        .replace("%player%", event.getPlayer().getName())
                        .replace("%message%", event.getMessage()))
                );
            }
        }
        event.getRecipients().clear();
    }

}
