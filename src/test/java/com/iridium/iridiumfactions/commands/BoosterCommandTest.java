package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.*;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.gui.FactionBoostersGUI;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoosterCommandTest {

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
    public void executeBoostersCommandNoFaction() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();

        serverMock.dispatchCommand(playerMock, "f booster");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeBoostersCommandSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        serverMock.dispatchCommand(playerMock, "f booster");
        assertTrue(playerMock.getOpenInventory().getTopInventory().getHolder() instanceof FactionBoostersGUI);
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeBoostersCommandUnknownBooster() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        serverMock.dispatchCommand(playerMock, "f booster unknownBooster");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().unknownBooster.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeBoostersCommandMaxLevel() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        IridiumFactions.getInstance().getFactionManager().getFactionBooster(faction, BoosterType.FLIGHT_BOOSTER).setTime(LocalDateTime.now().plusSeconds(600));
        serverMock.dispatchCommand(playerMock, "f booster " + BoosterType.FLIGHT_BOOSTER.getName());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().boosterAlreadyActive.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeBoostersCommandCannotAfford() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        IridiumFactions.getInstance().setEconomy(new TestEconomyProvider());

        serverMock.dispatchCommand(playerMock, "f booster " + BoosterType.FLIGHT_BOOSTER.getName());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotAfford.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeBoostersCommandUpgradeSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        TestEconomyProvider testEconomyProvider = new TestEconomyProvider();
        IridiumFactions.getInstance().setEconomy(testEconomyProvider);
        testEconomyProvider.depositPlayer(playerMock, IridiumFactions.getInstance().getBoosters().flightBooster.cost);

        serverMock.dispatchCommand(playerMock, "f booster " + BoosterType.FLIGHT_BOOSTER.getName());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().successfullyBoughtBooster
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%booster%", IridiumFactions.getInstance().getBoosters().flightBooster.name)
                .replace("%cost%", IridiumFactions.getInstance().getNumberFormatter().format(IridiumFactions.getInstance().getBoosters().flightBooster.cost))
        ));
        playerMock.assertNoMoreSaid();
        assertEquals(0, testEconomyProvider.getBalance(playerMock));
        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionBooster(faction, BoosterType.FLIGHT_BOOSTER).isActive());
    }

    @Test
    public void boostersCommandTabComplete() {
        assertEquals(List.of("flight"), IridiumFactions.getInstance().getCommands().boosterCommand.onTabComplete(null, null, null, null));
    }

}