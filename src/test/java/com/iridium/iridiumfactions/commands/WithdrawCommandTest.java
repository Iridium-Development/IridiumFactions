package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionBuilder;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.UserBuilder;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionBank;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WithdrawCommandTest {

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
    public void executeWithdrawCommandNoFaction() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        serverMock.dispatchCommand(playerMock, "f withdraw");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeWithdrawCommandBadSyntax() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        serverMock.dispatchCommand(playerMock, "f withdraw");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getCommands().withdrawCommand.syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeWithdrawCommandUnknownBankItem() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        serverMock.dispatchCommand(playerMock, "f withdraw invalid 10");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().noSuchBankItem.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeWithdrawCommandNotANumber() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        serverMock.dispatchCommand(playerMock, "f withdraw tnt nan");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().notANumber.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeWithdrawCommandInsufficientFunds() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        serverMock.dispatchCommand(playerMock, "f withdraw tnt 64");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().insufficientFundsToWithdrew
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%type%", "TnT")
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeWithdrawCommandSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        FactionBank factionBank = IridiumFactions.getInstance().getFactionManager().getFactionBank(faction, IridiumFactions.getInstance().getBankItems().tnTBankItem);
        factionBank.setNumber(64);

        serverMock.dispatchCommand(playerMock, "f withdraw tnt 1000");

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().bankWithdrew
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%amount%", "64.0")
                .replace("%type%", "TnT")
        ));
        playerMock.assertNoMoreSaid();
        assertEquals(0, factionBank.getNumber());
    }

}