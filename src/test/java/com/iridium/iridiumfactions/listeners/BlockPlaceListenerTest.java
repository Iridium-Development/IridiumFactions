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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockPlaceListenerTest {

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

    @Test
    public void onBlockPlaceWilderness() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();

        assertFalse(playerMock.simulateBlockPlace(Material.DIRT, playerMock.getLocation()).isCancelled());
    }

    @Test
    public void onBlockPlaceWarzone() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();

        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(new Faction(FactionType.WARZONE), playerMock.getLocation().getChunk()));

        assertTrue(playerMock.simulateBlockPlace(Material.DIRT, playerMock.getLocation()).isCancelled());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotPlaceBlocks
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void onBlockPlaceSafezone() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();

        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(new Faction(FactionType.SAFEZONE), playerMock.getLocation().getChunk()));

        assertTrue(playerMock.simulateBlockPlace(Material.DIRT, playerMock.getLocation()).isCancelled());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotPlaceBlocks
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void onBlockPlaceAlly() {
        Faction faction = new FactionBuilder().build();
        Faction myFaction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(myFaction).withFactionRank(FactionRank.MEMBER).build();

        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, playerMock.getLocation().getChunk()));
        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.ALLY, PermissionType.BLOCK_PLACE.getPermissionKey(), true);
        IridiumFactions.getInstance().getFactionManager().setFactionRelationship(faction, myFaction, RelationshipType.ALLY);

        assertFalse(playerMock.simulateBlockPlace(Material.DIRT, playerMock.getLocation()).isCancelled());
    }

    @Test
    public void onBlockPlacePlayerFactionNoPermission() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();

        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, playerMock.getLocation().getChunk()));
        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.BLOCK_PLACE.getPermissionKey(), false);

        assertTrue(playerMock.simulateBlockPlace(Material.DIRT, playerMock.getLocation()).isCancelled());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotPlaceBlocks
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void onBlockPlacePlayerFactionWithPermission() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();

        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, playerMock.getLocation().getChunk()));
        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.BLOCK_PLACE.getPermissionKey(), true);

        assertFalse(playerMock.simulateBlockPlace(Material.DIRT, playerMock.getLocation()).isCancelled());
    }

    @Test
    public void onBlockPlacePlayerFactionNoAccess() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();

        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, playerMock.getLocation().getChunk()));
        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.BLOCK_PLACE.getPermissionKey(), true);
        IridiumFactions.getInstance().getFactionManager().setFactionAccess(faction, FactionRank.MEMBER, IridiumFactions.getInstance().getFactionManager().getFactionClaimViaChunk(playerMock.getLocation().getChunk()).get(), false);

        assertTrue(playerMock.simulateBlockPlace(Material.DIRT, playerMock.getLocation()).isCancelled());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotPlaceBlocks
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
    }
}