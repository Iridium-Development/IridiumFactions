package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionBuilder;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.UserBuilder;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.gui.FactionRelationshipsGUI;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RelationshipsCommandTest {

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
    public void executeRelationshipsCommandNoFaction() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();

        serverMock.dispatchCommand(playerMock, "f relationships");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeRelationshipsCommandBadSyntax() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        serverMock.dispatchCommand(playerMock, "f relationships");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getCommands().relationshipsCommand.syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeRelationshipsCommandBadSyntaxInvalidRelationship() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        serverMock.dispatchCommand(playerMock, "f relationships truce");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getCommands().relationshipsCommand.syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeRelationshipsCommandSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();

        serverMock.dispatchCommand(playerMock, "f relationships ally");

        assertTrue(playerMock.getOpenInventory().getTopInventory().getHolder() instanceof FactionRelationshipsGUI);
        playerMock.assertNoMoreSaid();
    }
}