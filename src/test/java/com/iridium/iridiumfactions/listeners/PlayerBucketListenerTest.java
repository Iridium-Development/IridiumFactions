package com.iridium.iridiumfactions.listeners;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.*;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionClaim;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerBucketListenerTest {

    private ServerMock serverMock;

    @BeforeEach
    public void setup() {
        this.serverMock = MockBukkit.mock();
        MockBukkit.load(IridiumFactions.class);
    }

    @AfterEach
    public void tearDown() {
        Bukkit.getScheduler().cancelTasks(IridiumFactions.getInstance());
        MockBukkit.unmock();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void onBucketFillNoPermission() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, playerMock.getLocation().getChunk()));

        PlayerBucketFillEvent playerBucketFillEvent = new PlayerBucketFillEvent(playerMock, playerMock.getLocation().getBlock(), playerMock.getLocation().getBlock(), BlockFace.UP, Material.BUCKET, playerMock.getItemInHand());
        serverMock.getPluginManager().callEvent(playerBucketFillEvent);

        assertTrue(playerBucketFillEvent.isCancelled());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotUseBuckets
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void onBucketFillWithPermission() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.CO_OWNER).build();
        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.CO_OWNER, PermissionType.BUCKET.getPermissionKey(), true);
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, playerMock.getLocation().getChunk()));

        PlayerBucketFillEvent playerBucketFillEvent = new PlayerBucketFillEvent(playerMock, playerMock.getLocation().getBlock(), playerMock.getLocation().getBlock(), BlockFace.UP, Material.BUCKET, playerMock.getItemInHand());
        serverMock.getPluginManager().callEvent(playerBucketFillEvent);

        assertFalse(playerBucketFillEvent.isCancelled());
        playerMock.assertNoMoreSaid();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void onBucketEmptyNoPermission() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, playerMock.getLocation().getChunk()));

        PlayerBucketEmptyEvent playerBucketEmptyEvent = new PlayerBucketEmptyEvent(playerMock, playerMock.getLocation().getBlock(), playerMock.getLocation().getBlock(), BlockFace.UP, Material.BUCKET, playerMock.getItemInHand());
        serverMock.getPluginManager().callEvent(playerBucketEmptyEvent);

        assertTrue(playerBucketEmptyEvent.isCancelled());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotUseBuckets
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void onBucketEmptyWithPermission() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.CO_OWNER).build();
        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.CO_OWNER, PermissionType.BUCKET.getPermissionKey(), true);
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, playerMock.getLocation().getChunk()));

        PlayerBucketEmptyEvent playerBucketEmptyEvent = new PlayerBucketEmptyEvent(playerMock, playerMock.getLocation().getBlock(), playerMock.getLocation().getBlock(), BlockFace.UP, Material.BUCKET, playerMock.getItemInHand());
        serverMock.getPluginManager().callEvent(playerBucketEmptyEvent);

        assertFalse(playerBucketEmptyEvent.isCancelled());
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void onBucketEmptyPlayerFactionNoAccess() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();

        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, playerMock.getLocation().getChunk()));
        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.BUCKET.getPermissionKey(), true);
        IridiumFactions.getInstance().getFactionManager().setFactionAccess(faction, FactionRank.MEMBER, IridiumFactions.getInstance().getFactionManager().getFactionClaimViaChunk(playerMock.getLocation().getChunk()).get(), false);

        PlayerBucketEmptyEvent playerBucketEmptyEvent = new PlayerBucketEmptyEvent(playerMock, playerMock.getLocation().getBlock(), playerMock.getLocation().getBlock(), BlockFace.UP, Material.BUCKET, playerMock.getItemInHand());
        serverMock.getPluginManager().callEvent(playerBucketEmptyEvent);

        assertTrue(playerBucketEmptyEvent.isCancelled());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotUseBuckets
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
    }

}