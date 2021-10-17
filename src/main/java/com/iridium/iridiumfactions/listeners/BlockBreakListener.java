package com.iridium.iridiumfactions.listeners;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.PermissionType;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);
        IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(event.getBlock().getLocation()).ifPresent(faction -> {
            if(!IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, user, PermissionType.BLOCK_BREAK)){
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotBreakBlocks
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                ));
                event.setCancelled(true);
            }
        });
    }
}
