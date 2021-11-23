package com.iridium.iridiumfactions.listeners;

import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionBlocks;
import com.iridium.iridiumfactions.database.FactionSpawners;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplodeListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)

    public void onMonitorEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            Faction faction = IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(block.getLocation());
            if (block.getState() instanceof CreatureSpawner) {
                CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
                FactionSpawners factionSpawners = IridiumFactions.getInstance().getFactionManager().getFactionSpawners(faction, creatureSpawner.getSpawnedType());
                factionSpawners.setAmount(factionSpawners.getAmount() - 1);
            }
            FactionBlocks factionBlocks = IridiumFactions.getInstance().getFactionManager().getFactionBlock(faction, XMaterial.matchXMaterial(block.getType()));
            factionBlocks.setAmount(factionBlocks.getAmount() - 1);
        }
    }
}
