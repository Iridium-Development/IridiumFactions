package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateCommandTest {

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
    public void executeCreateCommandAlreadyInFaction() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Faction faction = new Faction("Faction", 1);

        user.setFaction(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f create Test");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().alreadyHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeCreateCommandFactionNameWilderness() {
        PlayerMock playerMock = serverMock.addPlayer("player");

        serverMock.dispatchCommand(playerMock, "f create Wilderness");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionNameAlreadyExists.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeCreateCommandFactionNameAlreadyExists() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        Faction faction = new Faction("Faction", 1);

        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f create Faction");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionNameAlreadyExists.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeCreateCommandFactionNameAlreadyExistsCaseInsensitive() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        Faction faction = new Faction("Faction", 1);

        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f create FACTION");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionNameAlreadyExists.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

}