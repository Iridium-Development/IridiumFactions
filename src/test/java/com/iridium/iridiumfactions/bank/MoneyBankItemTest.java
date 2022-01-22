package com.iridium.iridiumfactions.bank;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumfactions.FactionBuilder;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.TestEconomyProvider;
import com.iridium.iridiumfactions.UserBuilder;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionBank;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyBankItemTest {

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
    public void MoneyBankItemWithdrawInsufficientFunds() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        FactionBank factionBank = IridiumFactions.getInstance().getFactionManager().getFactionBank(faction, IridiumFactions.getInstance().getBankItems().moneyBankItem);

        BankResponse bankResponse = IridiumFactions.getInstance().getBankItems().moneyBankItem.withdraw(playerMock, 64, factionBank);

        assertEquals(0, bankResponse.getAmount());
        assertEquals(BankResponse.BankResponseType.INSUFFICIENT_FUNDS, bankResponse.getBankResponseType());
    }

    @Test
    public void MoneyBankItemWithdrawSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        FactionBank factionBank = IridiumFactions.getInstance().getFactionManager().getFactionBank(faction, IridiumFactions.getInstance().getBankItems().moneyBankItem);
        factionBank.setNumber(64);
        TestEconomyProvider testEconomyProvider = new TestEconomyProvider();
        IridiumFactions.getInstance().setEconomy(testEconomyProvider);

        BankResponse bankResponse = IridiumFactions.getInstance().getBankItems().moneyBankItem.withdraw(playerMock, 1000, factionBank);

        assertEquals(64, bankResponse.getAmount());
        assertEquals(BankResponse.BankResponseType.SUCCESS, bankResponse.getBankResponseType());
        assertEquals(64, testEconomyProvider.getBalance(playerMock));
    }

    @Test
    public void MoneyBankItemDepositInsufficientFunds() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        FactionBank factionBank = IridiumFactions.getInstance().getFactionManager().getFactionBank(faction, IridiumFactions.getInstance().getBankItems().moneyBankItem);
        TestEconomyProvider testEconomyProvider = new TestEconomyProvider();
        IridiumFactions.getInstance().setEconomy(testEconomyProvider);

        BankResponse bankResponse = IridiumFactions.getInstance().getBankItems().moneyBankItem.deposit(playerMock, 64, factionBank);

        assertEquals(0, bankResponse.getAmount());
        assertEquals(BankResponse.BankResponseType.INSUFFICIENT_FUNDS, bankResponse.getBankResponseType());
    }

    @Test
    public void MoneyBankItemDepositSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        FactionBank factionBank = IridiumFactions.getInstance().getFactionManager().getFactionBank(faction, IridiumFactions.getInstance().getBankItems().moneyBankItem);
        TestEconomyProvider testEconomyProvider = new TestEconomyProvider();
        IridiumFactions.getInstance().setEconomy(testEconomyProvider);
        testEconomyProvider.depositPlayer(playerMock, 64);

        BankResponse bankResponse = IridiumFactions.getInstance().getBankItems().moneyBankItem.deposit(playerMock, 1000, factionBank);

        assertEquals(64, bankResponse.getAmount());
        assertEquals(BankResponse.BankResponseType.SUCCESS, bankResponse.getBankResponseType());
        assertEquals(0, testEconomyProvider.getBalance(playerMock));
    }

}