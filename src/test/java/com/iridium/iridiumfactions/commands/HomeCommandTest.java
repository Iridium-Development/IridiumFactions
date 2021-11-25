package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionClaim;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HomeCommandTest {

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
    public void executeHomeCommandNoFaction() {
        PlayerMock playerMock = serverMock.addPlayer("Player");

        serverMock.dispatchCommand(playerMock, "f home");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeHomeCommandNoFactionHomeSet() {
        PlayerMock playerMock = serverMock.addPlayer("Player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Faction faction = new Faction("Faction", 1);

        user.setFaction(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f home");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().homeNotSet.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeHomeCommandFactionHomeNotInTerritory() {
        PlayerMock playerMock = serverMock.addPlayer("Player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Faction faction = new Faction("Faction", 1);

        user.setFaction(faction);
        faction.setHome(playerMock.getLocation());
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);

        serverMock.dispatchCommand(playerMock, "f home");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().homeNotInFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeHomeCommandSuccessful() {
        PlayerMock playerMock = serverMock.addPlayer("Player");
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        Faction faction = new Faction("Faction", 1);

        user.setFaction(faction);
        faction.setHome(playerMock.getLocation().add(100, 0, 100));
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, faction.getHome().getChunk()));

        serverMock.dispatchCommand(playerMock, "f home");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().teleportingFactionHome.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
        assertEquals(playerMock.getLocation(), faction.getHome());
    }

}