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

class DemoteCommandTest {

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
    public void executeDemoteCommandBadSyntax() {
        PlayerMock playerMock = serverMock.addPlayer("player");

        serverMock.dispatchCommand(playerMock, "f demote");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getCommands().demoteCommand.syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDemoteCommandNoFaction() {
        PlayerMock playerMock = serverMock.addPlayer("player");

        serverMock.dispatchCommand(playerMock, "f demote Player");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDemoteCommandUserNoFaction() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        PlayerMock otherPlayer = serverMock.addPlayer("otherPlayer");
        User otherUser = IridiumFactions.getInstance().getUserManager().getUser(otherPlayer);
        Faction faction = new Faction("Faction", 1);

        user.setFaction(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f demote otherPlayer");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().userNotInFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDemoteCommandUserRankMember() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        PlayerMock otherPlayer = serverMock.addPlayer("otherPlayer");
        User otherUser = IridiumFactions.getInstance().getUserManager().getUser(otherPlayer);
        Faction faction = new Faction("Faction", 1);

        otherUser.setFaction(faction);
        otherUser.setFactionRank(FactionRank.MEMBER);
        user.setFaction(faction);
        user.setFactionRank(FactionRank.CO_OWNER);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f demote otherPlayer");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotDemoteUser.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDemoteCommandUserRankHigher() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        PlayerMock otherPlayer = serverMock.addPlayer("otherPlayer");
        User otherUser = IridiumFactions.getInstance().getUserManager().getUser(otherPlayer);
        Faction faction = new Faction("Faction", 1);

        otherUser.setFaction(faction);
        otherUser.setFactionRank(FactionRank.OWNER);
        user.setFaction(faction);
        user.setFactionRank(FactionRank.CO_OWNER);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f demote otherPlayer");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotDemoteUser.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDemoteCommandNoPermission() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        PlayerMock otherPlayer = serverMock.addPlayer("otherPlayer");
        User otherUser = IridiumFactions.getInstance().getUserManager().getUser(otherPlayer);
        Faction faction = new Faction("Faction", 1);

        otherUser.setFaction(faction);
        otherUser.setFactionRank(FactionRank.MODERATOR);
        user.setFaction(faction);
        user.setFactionRank(FactionRank.CO_OWNER);
        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.CO_OWNER, PermissionType.DEMOTE.getPermissionKey(), false);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f demote otherPlayer");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotDemoteUser.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDemoteCommandSuccessful() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        PlayerMock otherPlayer = serverMock.addPlayer("otherPlayer");
        User otherUser = IridiumFactions.getInstance().getUserManager().getUser(otherPlayer);
        Faction faction = new Faction("Faction", 1);

        otherUser.setFaction(faction);
        otherUser.setFactionRank(FactionRank.MODERATOR);
        user.setFaction(faction);
        user.setFactionRank(FactionRank.CO_OWNER);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f demote otherPlayer");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().demotedPlayer
                .replace("%promoter%", playerMock.getName())
                .replace("%player%", otherPlayer.getName())
                .replace("%rank%", FactionRank.MEMBER.getDisplayName())
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
        otherPlayer.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().userDemotedPlayer
                .replace("%promoter%", playerMock.getName())
                .replace("%player%", otherPlayer.getName())
                .replace("%rank%", FactionRank.MEMBER.getDisplayName())
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        otherPlayer.assertNoMoreSaid();
        assertEquals(otherUser.getFactionRank(), FactionRank.MEMBER);
    }

}