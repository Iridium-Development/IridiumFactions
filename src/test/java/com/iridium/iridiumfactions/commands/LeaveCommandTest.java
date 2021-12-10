package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionBuilder;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.UserBuilder;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LeaveCommandTest {

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
    public void executeLeaveCommandNoFaction() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();

        serverMock.dispatchCommand(playerMock, "f leave");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeLeaveCommandSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        PlayerMock otherPlayer = new UserBuilder(serverMock).withFaction(faction).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);

        serverMock.dispatchCommand(playerMock, "f leave");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().leftFaction
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%name%", faction.getName())
        ));
        playerMock.assertNoMoreSaid();

        otherPlayer.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().userLeftFaction
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%name%", faction.getName())
                .replace("%player%", playerMock.getName())
        ));
        otherPlayer.assertNoMoreSaid();

        assertEquals(user.getFactionID(), 0);
        assertEquals(user.getFactionRank(), FactionRank.TRUCE);
    }

}