package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumfactions.IridiumFactions;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AboutCommandTest {

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
    public void executeAboutCommand() {
        PlayerMock playerMock = serverMock.addPlayer("player");
        serverMock.dispatchCommand(playerMock, "f about");
        playerMock.assertSaid("§7Plugin Name: §cIridiumFactions");
        playerMock.assertSaid("§7Plugin Version: §c" + IridiumFactions.getInstance().getDescription().getVersion());
        playerMock.assertSaid("§7Plugin Author: §cPeaches_MLG");
        playerMock.assertSaid("§7Plugin Donations: §cwww.patreon.com/Peaches_MLG");
        playerMock.assertNoMoreSaid();
    }

}