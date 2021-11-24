package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.RelationshipType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnemyCommandTest {

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
    public void executeEnemyCommandNoFaction() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        serverMock.dispatchCommand(playerMock, "f enemy Test");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeEnemyCommandNoFactionExists() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Faction faction = new Faction("Test", 1);

        user.setFaction(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f enemy Test1");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionDoesntExistByName.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeEnemyCommandNonPlayerFaction() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Faction faction = new Faction("Test", 1);

        user.setFaction(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f enemy Wilderness");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionDoesntExistByName.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeEnemyCommandOwnFaction() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Faction faction = new Faction("Test", 1);

        user.setFaction(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f enemy Test");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotRelationshipYourFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeEnemyCommandRequestSent() {
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

        serverMock.dispatchCommand(playerMock, "f enemy Test2");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionEnemied
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", playerMock.getName())
                .replace("%faction%", otherFaction.getName())
        ));
        playerMock.assertNoMoreSaid();

        otherPlayerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().yourFactionEnemied
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", playerMock.getName())
                .replace("%faction%", faction.getName())
        ));
        otherPlayerMock.assertNoMoreSaid();
    }

    @Test
    public void executeEnemyCommandAlreadyAllied() {
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
        IridiumFactions.getInstance().getFactionManager().setFactionRelationship(faction, otherFaction, RelationshipType.ENEMY);

        serverMock.dispatchCommand(playerMock, "f enemy Test2");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().alreadyEnemies
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
        playerMock.assertNoMoreSaid();
    }
}