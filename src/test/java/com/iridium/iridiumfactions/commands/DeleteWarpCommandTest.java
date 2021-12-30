package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.*;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionWarp;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeleteWarpCommandTest {

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
    public void executeDeleteWarpCommandNoFaction() {
        PlayerMock playerMock = serverMock.addPlayer("player");

        serverMock.dispatchCommand(playerMock, "f deletewarp");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDeleteWarpCommandBadSyntax() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        serverMock.dispatchCommand(playerMock, "f deletewarp");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getCommands().deleteWarpCommand.syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDeleteWarpCommandNoPermission() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        serverMock.dispatchCommand(playerMock, "f deletewarp test");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotDeleteWarp
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDeleteWarpCommandWarpDoestExist() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.MANAGE_WARPS.getPermissionKey(), true);

        serverMock.dispatchCommand(playerMock, "f deletewarp test");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().unknownWarp
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDeleteWarpCommandSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.MEMBER).build();

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.MANAGE_WARPS.getPermissionKey(), true);
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction, playerMock.getLocation(), "test"));

        serverMock.dispatchCommand(playerMock, "f deletewarp test");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().deletingWarp
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%name%", "test")
        ));
        playerMock.assertNoMoreSaid();
        assertEquals(0, IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().getEntries().size());
    }
}