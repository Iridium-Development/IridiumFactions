package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.PermissionType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KickCommandTest {

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
    public void executeKickCommandBadSyntax() {
        PlayerMock playerMock = serverMock.addPlayer("player");

        serverMock.dispatchCommand(playerMock, "f kick");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getCommands().kickCommand.syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeKickCommandNoFaction() {
        PlayerMock playerMock = serverMock.addPlayer("player");

        serverMock.dispatchCommand(playerMock, "f kick OtherPlayer");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeKickCommandNoPermission() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Faction faction = new Faction("Faction", 1);

        user.setFaction(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f kick OtherPlayer");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotKick.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeKickCommandNoPlayerExists() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Faction faction = new Faction("Faction", 1);

        user.setFaction(faction);
        user.setFactionRank(FactionRank.MEMBER);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.KICK.getPermissionKey(), true);

        serverMock.dispatchCommand(playerMock, "f kick OtherPlayer");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().notAPlayer.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeKickCommandPlayerNotInFaction() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        PlayerMock otherPlayer = serverMock.addPlayer("otherPlayer");
        User otherUser = IridiumFactions.getInstance().getUserManager().getUser(otherPlayer);
        Faction faction = new Faction("Faction", 1);

        user.setFaction(faction);
        user.setFactionRank(FactionRank.MEMBER);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.KICK.getPermissionKey(), true);

        serverMock.dispatchCommand(playerMock, "f kick OtherPlayer");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().userNotInFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeKickCommandPlayerHigherRank() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        PlayerMock otherPlayer = serverMock.addPlayer("otherPlayer");
        User otherUser = IridiumFactions.getInstance().getUserManager().getUser(otherPlayer);
        Faction faction = new Faction("Faction", 1);

        user.setFaction(faction);
        user.setFactionRank(FactionRank.MEMBER);
        otherUser.setFaction(faction);
        otherUser.setFactionRank(FactionRank.MEMBER);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.KICK.getPermissionKey(), true);

        serverMock.dispatchCommand(playerMock, "f kick OtherPlayer");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotKickHigherRank.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeKickCommandSuccessful() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        PlayerMock otherPlayer = serverMock.addPlayer("otherPlayer");
        User otherUser = IridiumFactions.getInstance().getUserManager().getUser(otherPlayer);
        Faction faction = new Faction("Faction", 1);

        user.setFaction(faction);
        user.setFactionRank(FactionRank.MODERATOR);
        otherUser.setFaction(faction);
        otherUser.setFactionRank(FactionRank.MEMBER);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.KICK.getPermissionKey(), true);

        serverMock.dispatchCommand(playerMock, "f kick OtherPlayer");
        assertEquals(0, otherUser.getFactionID());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().playerKicked
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", otherPlayer.getName())
                .replace("%kicker%", playerMock.getName())
        ));
        playerMock.assertNoMoreSaid();
        otherPlayer.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().youHaveBeenKicked
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", playerMock.getName())
        ));
        otherPlayer.assertNoMoreSaid();
    }

}