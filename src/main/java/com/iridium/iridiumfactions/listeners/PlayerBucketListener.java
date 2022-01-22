package com.iridium.iridiumfactions.listeners;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.PermissionType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class PlayerBucketListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        onPlayerBucket(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        onPlayerBucket(event);
    }

    public void onPlayerBucket(PlayerBucketEvent event) {
        Player player = event.getPlayer();
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);
        Faction faction = IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(event.getBlock().getLocation());
        if (!IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, user, PermissionType.BUCKET, event.getBlock().getLocation())) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotUseBuckets
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
            ));
            event.setCancelled(true);
        }
    }
}
