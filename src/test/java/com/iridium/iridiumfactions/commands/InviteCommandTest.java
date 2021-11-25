package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.PermissionType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionInvite;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InviteCommandTest {

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
    public void executeInviteCommandBadSyntax() {
        PlayerMock playerMock = serverMock.addPlayer("Player");

        serverMock.dispatchCommand(playerMock, "f invite");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getCommands().inviteCommand.syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeInviteCommandNoFaction() {
        PlayerMock playerMock = serverMock.addPlayer("Player");

        serverMock.dispatchCommand(playerMock, "f invite Player");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeInviteCommandNoPermission() {
        PlayerMock playerMock = serverMock.addPlayer("Player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Faction faction = new Faction("Faction", 1);

        user.setFaction(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f invite Player");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotInvite.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeInviteCommandPlayerDoesntExist() {
        PlayerMock playerMock = serverMock.addPlayer("Player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Faction faction = new Faction("Faction", 1);

        user.setFaction(faction);
        user.setFactionRank(FactionRank.MEMBER);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.INVITE.getPermissionKey(), true);

        serverMock.dispatchCommand(playerMock, "f invite OtherPlayer");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().notAPlayer.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeInviteCommandPlayerAlreadyInYourFaction() {
        PlayerMock playerMock = serverMock.addPlayer("Player");
        PlayerMock otherPlayer = serverMock.addPlayer("OtherPlayer");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        User otherUser = IridiumFactions.getInstance().getUserManager().getUser(otherPlayer);
        Faction faction = new Faction("Faction", 1);

        user.setFaction(faction);
        user.setFactionRank(FactionRank.MEMBER);
        otherUser.setFaction(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.INVITE.getPermissionKey(), true);

        serverMock.dispatchCommand(playerMock, "f invite OtherPlayer");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().userAlreadyInFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeInviteCommandPlayerAlreadyInvited() {
        PlayerMock playerMock = serverMock.addPlayer("Player");
        PlayerMock otherPlayer = serverMock.addPlayer("OtherPlayer");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        User otherUser = IridiumFactions.getInstance().getUserManager().getUser(otherPlayer);
        Faction faction = new Faction("Faction", 1);

        user.setFaction(faction);
        user.setFactionRank(FactionRank.MEMBER);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionInviteTableManager().addEntry(new FactionInvite(faction, otherUser, user));
        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.INVITE.getPermissionKey(), true);

        serverMock.dispatchCommand(playerMock, "f invite OtherPlayer");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().inviteAlreadyPresent.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeInviteCommandSuccessful() {
        PlayerMock playerMock = serverMock.addPlayer("Player");
        PlayerMock otherPlayer = serverMock.addPlayer("OtherPlayer");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        User otherUser = IridiumFactions.getInstance().getUserManager().getUser(otherPlayer);
        Faction faction = new Faction("Faction", 1);

        user.setFaction(faction);
        user.setFactionRank(FactionRank.MEMBER);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.INVITE.getPermissionKey(), true);

        serverMock.dispatchCommand(playerMock, "f invite OtherPlayer");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionInviteSent
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", otherPlayer.getName())
        ));
        playerMock.assertNoMoreSaid();
        otherPlayer.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionInviteReceived
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", playerMock.getName())
        ));
        otherPlayer.assertNoMoreSaid();
        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionInvite(faction, otherUser).isPresent());
    }

}