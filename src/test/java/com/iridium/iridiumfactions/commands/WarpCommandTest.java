package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionBuilder;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.UserBuilder;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionClaim;
import com.iridium.iridiumfactions.database.FactionWarp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class WarpCommandTest {

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
    public void executeWarpCommandNoFaction() {
        PlayerMock playerMock = serverMock.addPlayer("player");

        serverMock.dispatchCommand(playerMock, "f warp");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeWarpCommandBadSyntax() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        serverMock.dispatchCommand(playerMock, "f warp");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getCommands().warpCommand.syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeWarpCommandWarpDoesntExist() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        serverMock.dispatchCommand(playerMock, "f warp test");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().unknownWarp.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeWarpCommandNotInFactionClaim() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction, playerMock.getLocation(), "test"));

        serverMock.dispatchCommand(playerMock, "f warp test");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().notInFactionLand.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeWarpCommandNoPassword() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        Location warpLocation = playerMock.getLocation().clone().add(100, 100, 100);
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, warpLocation.getChunk()));
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction, warpLocation, "test", "password"));

        serverMock.dispatchCommand(playerMock, "f warp test");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().incorrectPassword.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
        assertNotEquals(warpLocation, playerMock.getLocation());
    }

    @Test
    public void executeWarpCommandIncorrectPassword() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        Location warpLocation = playerMock.getLocation().clone().add(100, 100, 100);
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, warpLocation.getChunk()));
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction, warpLocation, "test", "password"));

        serverMock.dispatchCommand(playerMock, "f warp test Password");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().incorrectPassword.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
        assertNotEquals(warpLocation, playerMock.getLocation());
    }

    @Test
    public void executeWarpCommandSuccessfulWithPassword() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        Location warpLocation = playerMock.getLocation().clone().add(100, 100, 100);
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, warpLocation.getChunk()));
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction, warpLocation, "test", "password"));

        serverMock.dispatchCommand(playerMock, "f warp test password");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().teleportingWarp.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)).replace("%name%", "test"));
        playerMock.assertNoMoreSaid();
        assertEquals(warpLocation, playerMock.getLocation());
    }

    @Test
    public void executeWarpCommandSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        Location warpLocation = playerMock.getLocation().clone().add(100, 100, 100);
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, warpLocation.getChunk()));
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction, warpLocation, "test"));

        serverMock.dispatchCommand(playerMock, "f warp test");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().teleportingWarp.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)).replace("%name%", "test"));
        playerMock.assertNoMoreSaid();
        assertEquals(warpLocation, playerMock.getLocation());
    }

    @Test
    public void warpCommandTabComplete() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction, null, "home"));
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction, null, "farm"));
        assertEquals(Arrays.asList("farm", "home"), IridiumFactions.getInstance().getCommands().warpCommand.onTabComplete(playerMock, null, null, new String[]{"f", "warp", ""}));
        assertEquals(List.of("farm"), IridiumFactions.getInstance().getCommands().warpCommand.onTabComplete(playerMock, null, null, new String[]{"f", "warp", "f"}));
    }
}