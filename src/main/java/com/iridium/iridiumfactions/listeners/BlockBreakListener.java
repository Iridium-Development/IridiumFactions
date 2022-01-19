package com.iridium.iridiumfactions.listeners;

import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.PermissionType;
import com.iridium.iridiumfactions.UpgradeType;
import com.iridium.iridiumfactions.database.*;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);
        Faction faction = IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(event.getBlock().getLocation());
        if (!IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, user, PermissionType.BLOCK_BREAK, event.getBlock().getLocation())) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotBreakBlocks
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
            ));
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMonitorBlockBreak(BlockBreakEvent event) {
        User user = IridiumFactions.getInstance().getUserManager().getUser(event.getPlayer());
        Faction faction = IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(event.getBlock().getLocation());
        if (event.getBlock().getState() instanceof CreatureSpawner) {
            CreatureSpawner creatureSpawner = (CreatureSpawner) event.getBlock().getState();
            FactionSpawners factionSpawners = IridiumFactions.getInstance().getFactionManager().getFactionSpawners(faction, creatureSpawner.getSpawnedType());
            factionSpawners.setAmount(factionSpawners.getAmount() - 1);
        }
        FactionBlocks factionBlocks = IridiumFactions.getInstance().getFactionManager().getFactionBlock(faction, XMaterial.matchXMaterial(event.getBlock().getType()));
        factionBlocks.setAmount(factionBlocks.getAmount() - 1);

        FactionUpgrade factionUpgrade = IridiumFactions.getInstance().getFactionManager().getFactionUpgrade(user.getFaction(), UpgradeType.EXPERIENCE_UPGRADE);
        event.setExpToDrop((int) (event.getExpToDrop() * IridiumFactions.getInstance().getUpgrades().experienceUpgrade.upgrades.get(factionUpgrade.getLevel()).experienceModifier));
    }
}
