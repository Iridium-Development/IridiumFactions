package com.iridium.iridiumfactions.listeners;

import com.iridium.iridiumfactions.CooldownProvider;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.UpgradeType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import java.time.Duration;

public class SpawnerSpawnListener implements Listener {

    private final CooldownProvider<CreatureSpawner> cooldownProvider = CooldownProvider.newInstance(Duration.ofMillis(50));

    @EventHandler(ignoreCancelled = true)
    public void onCreatureSpawn(SpawnerSpawnEvent event) {
        Faction faction = IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(event.getLocation());
        FactionUpgrade factionUpgrade = IridiumFactions.getInstance().getFactionManager().getFactionUpgrade(faction, UpgradeType.WARPS_UPGRADE);
        CreatureSpawner spawner = event.getSpawner();
        if (!cooldownProvider.isOnCooldown(spawner)) {
            cooldownProvider.applyCooldown(spawner);
            Bukkit.getScheduler().runTask(IridiumFactions.getInstance(), () -> {
                spawner.setDelay((int) (spawner.getDelay() / IridiumFactions.getInstance().getUpgrades().spawnerUpgrade.upgrades.get(factionUpgrade.getLevel()).spawnerModifier));
                spawner.update();
            });
        }
    }

}
