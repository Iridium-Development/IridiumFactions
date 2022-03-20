package com.iridium.iridiumfactions.listeners;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.UserBuilder;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerDeathListenerTest {

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
    public void playerDeathPowerLossSuccess() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);

        new PlayerDeathListener().monitorPlayerDeath(new PlayerDeathEvent(playerMock, List.of(), 0, ""));

        assertEquals(-3, user.getPower());
    }

    @Test
    public void playerDeathPowerLossMinimumReached() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        user.setPower(-10);

        new PlayerDeathListener().monitorPlayerDeath(new PlayerDeathEvent(playerMock, List.of(), 0, ""));

        assertEquals(-10, user.getPower());
    }


}