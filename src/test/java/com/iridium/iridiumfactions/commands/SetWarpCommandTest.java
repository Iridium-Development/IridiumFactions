package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.*;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionClaim;
import com.iridium.iridiumfactions.database.FactionWarp;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SetWarpCommandTest {

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
    public void executeSetWarpCommandNoFaction() {
        PlayerMock playerMock = serverMock.addPlayer("player");

        serverMock.dispatchCommand(playerMock, "f setwarp");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeSetWarpCommandBadSyntax() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        serverMock.dispatchCommand(playerMock, "f setwarp");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getCommands().setWarpCommand.syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeSetWarpCommandNotInFactionClaim() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        serverMock.dispatchCommand(playerMock, "f setwarp test");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().notInFactionLand.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeSetWarpCommandNoPermission() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, playerMock.getLocation().getChunk()));

        serverMock.dispatchCommand(playerMock, "f setwarp test");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotSetWarp
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeSetWarpCommandWarpAlreadyExists() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();

        IridiumFactions.getInstance().getFactionManager().getFactionUpgrade(faction, UpgradeType.WARPS_UPGRADE).setLevel(3);
        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.MANAGE_WARPS.getPermissionKey(), true);
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, playerMock.getLocation().getChunk()));
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction, playerMock.getLocation(), "test"));

        serverMock.dispatchCommand(playerMock, "f setwarp test");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().warpAlreadyExists
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeSetWarpCommandWarpLimitReached() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.MANAGE_WARPS.getPermissionKey(), true);
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, playerMock.getLocation().getChunk()));
        for (int i = 0; i < 10; i++) {
            IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction, playerMock.getLocation(), "warp_" + i));
        }

        serverMock.dispatchCommand(playerMock, "f setwarp test");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().warpLimitReached
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeSetWarpCommandSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.MANAGE_WARPS.getPermissionKey(), true);
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, playerMock.getLocation().getChunk()));

        serverMock.dispatchCommand(playerMock, "f setwarp test");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionWarpSet
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
        assertEquals(1, IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().getEntries(faction).size());
        assertEquals("test", IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().getEntries(faction).get(0).getName());
        assertEquals(playerMock.getLocation(), IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().getEntries(faction).get(0).getLocation());
        assertNull(IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().getEntries(faction).get(0).getPassword());
    }

    @Test
    public void executeSetWarpCommandSuccessfulWithPassword() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.MANAGE_WARPS.getPermissionKey(), true);
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, playerMock.getLocation().getChunk()));

        serverMock.dispatchCommand(playerMock, "f setwarp test password");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionWarpSet
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
        assertEquals(1, IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().getEntries(faction).size());
        assertEquals("test", IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().getEntries(faction).get(0).getName());
        assertEquals(playerMock.getLocation(), IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().getEntries(faction).get(0).getLocation());
        assertEquals("password", IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().getEntries(faction).get(0).getPassword());
    }

}