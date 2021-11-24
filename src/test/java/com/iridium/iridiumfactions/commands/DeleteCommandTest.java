package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import com.iridium.iridiumfactions.gui.ConfirmationGUI;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DeleteCommandTest {

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
    public void executeDeleteCommandNoFaction() {
        PlayerMock playerMock = serverMock.addPlayer("player");

        serverMock.dispatchCommand(playerMock, "f delete");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDeleteCommandNotOwnerOrBypassing() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Faction faction = new Faction("Faction", 1);

        user.setFaction(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f delete");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotDeleteFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDeleteCommandOwner() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Faction faction = new Faction("Faction", 1);

        user.setFactionRank(FactionRank.OWNER);
        user.setFaction(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f delete");
        assertTrue(playerMock.getOpenInventory().getTopInventory().getHolder() instanceof ConfirmationGUI);
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDeleteCommandBypass() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Faction faction = new Faction("Faction", 1);

        user.setBypassing(true);
        user.setFaction(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f delete");
        assertTrue(playerMock.getOpenInventory().getTopInventory().getHolder() instanceof ConfirmationGUI);
        playerMock.assertNoMoreSaid();
    }

}