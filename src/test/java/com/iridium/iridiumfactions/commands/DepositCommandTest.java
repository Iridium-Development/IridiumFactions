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
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DepositCommandTest {

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
    public void executeDepositCommandNoFaction() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        serverMock.dispatchCommand(playerMock, "f deposit");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDepositCommandBadSyntax() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        serverMock.dispatchCommand(playerMock, "f deposit");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getCommands().depositCommand.syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDepositCommandUnknownBankItem() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        serverMock.dispatchCommand(playerMock, "f deposit invalid 10");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().noSuchBankItem.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDepositCommandNotANumber() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        serverMock.dispatchCommand(playerMock, "f deposit tnt nan");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().notANumber.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDepositCommandInsufficientFunds() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        serverMock.dispatchCommand(playerMock, "f deposit tnt 64");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().insufficientFundsToDeposit
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%type%", "TnT")
        ));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeDepositCommandSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        playerMock.getInventory().addItem(new ItemStack(Material.TNT, 64));

        serverMock.dispatchCommand(playerMock, "f deposit tnt 1000");

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().bankDeposited
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%amount%", "64.0")
                .replace("%type%", "TnT")
        ));
        playerMock.assertNoMoreSaid();
        assertEquals(64, IridiumFactions.getInstance().getFactionManager().getFactionBank(faction, IridiumFactions.getInstance().getBankItems().tnTBankItem).getNumber());
    }

}