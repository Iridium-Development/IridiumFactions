package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.BoosterType;
import com.iridium.iridiumfactions.FactionBuilder;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.UserBuilder;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlyCommandTest {

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
    public void executeFlyCommandNoFaction() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();

        serverMock.dispatchCommand(playerMock, "f fly");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeFlyCommandBoosterNotActive() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        serverMock.dispatchCommand(playerMock, "f fly");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().flightBoosterNotActive.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeFlyCommandToggleEnabledSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        IridiumFactions.getInstance().getFactionManager().getFactionBooster(faction, BoosterType.FLIGHT_BOOSTER).setTime(LocalDateTime.now().plusSeconds(600));

        serverMock.dispatchCommand(playerMock, "f fly");

        assertTrue(user.isFlying());
        assertTrue(playerMock.getAllowFlight());
        assertTrue(playerMock.isFlying());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().flightEnabled.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeFlyCommandToggleDisabledSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        user.setFlying(true);
        IridiumFactions.getInstance().getFactionManager().getFactionBooster(faction, BoosterType.FLIGHT_BOOSTER).setTime(LocalDateTime.now().plusSeconds(600));

        serverMock.dispatchCommand(playerMock, "f fly");

        assertFalse(user.isFlying());
        assertFalse(playerMock.getAllowFlight());
        assertFalse(playerMock.isFlying());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().flightDisabled.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeFlyCommandEnabledSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        IridiumFactions.getInstance().getFactionManager().getFactionBooster(faction, BoosterType.FLIGHT_BOOSTER).setTime(LocalDateTime.now().plusSeconds(600));

        serverMock.dispatchCommand(playerMock, "f fly enable");

        assertTrue(user.isFlying());
        assertTrue(playerMock.getAllowFlight());
        assertTrue(playerMock.isFlying());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().flightEnabled.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeFlyCommandDisabledSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        IridiumFactions.getInstance().getFactionManager().getFactionBooster(faction, BoosterType.FLIGHT_BOOSTER).setTime(LocalDateTime.now().plusSeconds(600));

        serverMock.dispatchCommand(playerMock, "f fly disable");

        assertFalse(user.isFlying());
        assertFalse(playerMock.getAllowFlight());
        assertFalse(playerMock.isFlying());
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().flightDisabled.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeFlyCommandInvalidSyntax() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        IridiumFactions.getInstance().getFactionManager().getFactionBooster(faction, BoosterType.FLIGHT_BOOSTER).setTime(LocalDateTime.now().plusSeconds(600));

        serverMock.dispatchCommand(playerMock, "f fly invalid");

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getConfiguration().prefix + " &7/f fly <enable/disable>"));
        playerMock.assertNoMoreSaid();
    }

}