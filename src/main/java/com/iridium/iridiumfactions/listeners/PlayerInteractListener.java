package com.iridium.iridiumfactions.listeners;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import com.iridium.iridiumteams.PermissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;

public class PlayerInteractListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);

        Optional<Faction> faction = IridiumFactions.getInstance().getTeamManager().getTeamViaLocation(event.getClickedBlock().getLocation());
        if (!faction.isPresent()) return;
        if (!IridiumFactions.getInstance().getTeamManager().getTeamPermission(faction.get(), user, PermissionType.BLOCK_BREAK)) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotBreakBlocks
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
            ));
            return;
        }
    }

}
