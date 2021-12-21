package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.UserBuilder;
import com.iridium.iridiumfactions.database.Faction;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class RecalculateCommandTest {

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
    public void executePromoteCommandNoPermission() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();

        serverMock.dispatchCommand(playerMock, "f recalculate");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().noPermission.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executePromoteCommandSuccessful() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        playerMock.setOp(true);

        serverMock.dispatchCommand(playerMock, "f recalculate");
        int interval = 1;
        List<Faction> factionList = IridiumFactions.getInstance().getFactionManager().getFactions();
        int seconds = (factionList.size() * interval / 20) % 60;
        int minutes = (factionList.size() * interval / 20) / 60;
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().calculatingFactions
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix))
                .replace("%minutes%", String.valueOf(minutes))
                .replace("%seconds%", String.valueOf(seconds))
                .replace("%amount%", String.valueOf(factionList.size()))
        );
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executePromoteCommandAlreadyRunning() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        playerMock.setOp(true);

        serverMock.dispatchCommand(playerMock, "f recalculate");
        int interval = 1;
        List<Faction> factionList = IridiumFactions.getInstance().getFactionManager().getFactions();
        int seconds = (factionList.size() * interval / 20) % 60;
        int minutes = (factionList.size() * interval / 20) / 60;
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().calculatingFactions
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix))
                .replace("%minutes%", String.valueOf(minutes))
                .replace("%seconds%", String.valueOf(seconds))
                .replace("%amount%", String.valueOf(factionList.size()))
        );
        playerMock.assertNoMoreSaid();
        serverMock.dispatchCommand(playerMock, "f recalculate");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().calculationAlreadyInProcess.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

}