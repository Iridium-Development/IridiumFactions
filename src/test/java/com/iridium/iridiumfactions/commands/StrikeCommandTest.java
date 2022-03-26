package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionBuilder;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.UserBuilder;
import com.iridium.iridiumfactions.database.Faction;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StrikeCommandTest {

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
    public void executeStrikeCommandInvalidSyntax() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        playerMock.setOp(true);

        serverMock.dispatchCommand(playerMock, "f strike test");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getCommands().strikeCommand.syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeStrikeCommandUnknownFaction() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        playerMock.setOp(true);

        serverMock.dispatchCommand(playerMock, "f strike test reason");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionDoesntExistByName.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeStrikeCommandSuccessful() {
        Faction faction = new FactionBuilder("test").build();
        PlayerMock factionMember = new UserBuilder(serverMock).withFaction(faction).build();
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        playerMock.setOp(true);

        serverMock.dispatchCommand(playerMock, "f strike test strike reason");

        assertEquals(1, IridiumFactions.getInstance().getFactionManager().getFactionStrikes(faction).size());

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().issuedFactionStrike
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%faction%", "test")
                .replace("%reason%", "strike reason")
        ));
        playerMock.assertNoMoreSaid();

        factionMember.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionStrikeIssued
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", playerMock.getName())
                .replace("%reason%", "strike reason")
        ));
        factionMember.assertNoMoreSaid();
    }
}