package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.RelationshipType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionRelationshipRequest;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AllyCommandTest {


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
    public void executeAllyCommandNoFaction() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        serverMock.dispatchCommand(playerMock, "f ally Test");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeAllyCommandNoFactionExists() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Faction faction = new Faction("Test", 1);

        user.setFaction(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f ally Test1");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionDoesntExistByName.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeAllyCommandNonPlayerFaction() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Faction faction = new Faction("Test", 1);

        user.setFaction(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f ally Wilderness");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionDoesntExistByName.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeAllyCommandOwnFaction() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Faction faction = new Faction("Test", 1);

        user.setFaction(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f ally Test");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotRelationshipYourFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeAllyCommandRequestSent() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        PlayerMock otherPlayerMock = serverMock.addPlayer("OtherPlayer");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        User otherUser = IridiumFactions.getInstance().getUserManager().getUser(otherPlayerMock);
        Faction faction = new Faction("Test", 1);
        Faction otherFaction = new Faction("Test2", 2);

        user.setFaction(faction);
        otherUser.setFaction(otherFaction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(otherFaction);

        serverMock.dispatchCommand(playerMock, "f ally Test2");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().allianceRequestSent
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", playerMock.getName())
                .replace("%faction%", otherFaction.getName())
        ));
        playerMock.assertNoMoreSaid();

        otherPlayerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().allianceRequestReceived
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", playerMock.getName())
                .replace("%faction%", faction.getName())
        ));
        otherPlayerMock.assertNoMoreSaid();
    }

    @Test
    public void executeAllyCommandRequestAccepted() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        PlayerMock otherPlayerMock = serverMock.addPlayer("OtherPlayer");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        User otherUser = IridiumFactions.getInstance().getUserManager().getUser(otherPlayerMock);
        Faction faction = new Faction("Test", 1);
        Faction otherFaction = new Faction("Test2", 2);

        user.setFaction(faction);
        otherUser.setFaction(otherFaction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(otherFaction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipRequestTableManager().addEntry(new FactionRelationshipRequest(faction, otherFaction, RelationshipType.ALLY, user));

        serverMock.dispatchCommand(otherPlayerMock, "f ally Test");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionAllied
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", playerMock.getName())
                .replace("%faction%", otherFaction.getName())
        ));
        playerMock.assertNoMoreSaid();

        otherPlayerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionAllied
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", otherPlayerMock.getName())
                .replace("%faction%", faction.getName())
        ));
        otherPlayerMock.assertNoMoreSaid();
    }

    @Test
    public void executeAllyCommandRequestAlreadySent() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        PlayerMock otherPlayerMock = serverMock.addPlayer("OtherPlayer");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        User otherUser = IridiumFactions.getInstance().getUserManager().getUser(otherPlayerMock);
        Faction faction = new Faction("Test", 1);
        Faction otherFaction = new Faction("Test2", 2);

        user.setFaction(faction);
        otherUser.setFaction(otherFaction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(otherFaction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipRequestTableManager().addEntry(new FactionRelationshipRequest(faction, otherFaction, RelationshipType.ALLY, user));

        serverMock.dispatchCommand(playerMock, "f ally Test2");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().allianceRequestAlreadySent
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeAllyCommandAlreadyAllied() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        PlayerMock otherPlayerMock = serverMock.addPlayer("OtherPlayer");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        User otherUser = IridiumFactions.getInstance().getUserManager().getUser(otherPlayerMock);
        Faction faction = new Faction("Test", 1);
        Faction otherFaction = new Faction("Test2", 2);

        user.setFaction(faction);
        otherUser.setFaction(otherFaction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(otherFaction);
        IridiumFactions.getInstance().getFactionManager().setFactionRelationship(faction, otherFaction, RelationshipType.ALLY);

        serverMock.dispatchCommand(playerMock, "f ally Test2");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().alreadyAllied
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
    }
}