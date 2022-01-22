package com.iridium.iridiumfactions.bank;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumcore.utils.InventoryUtils;
import com.iridium.iridiumfactions.FactionBuilder;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.UserBuilder;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionBank;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TnTBankItemTest {

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
    public void TnTBankItemWithdrawInsufficientFunds() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        FactionBank factionBank = IridiumFactions.getInstance().getFactionManager().getFactionBank(faction, IridiumFactions.getInstance().getBankItems().tnTBankItem);

        BankResponse bankResponse = IridiumFactions.getInstance().getBankItems().tnTBankItem.withdraw(playerMock, 64, factionBank);

        assertEquals(0, bankResponse.getAmount());
        assertEquals(BankResponse.BankResponseType.INSUFFICIENT_FUNDS, bankResponse.getBankResponseType());
    }

    @Test
    public void TnTBankItemWithdrawSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        FactionBank factionBank = IridiumFactions.getInstance().getFactionManager().getFactionBank(faction, IridiumFactions.getInstance().getBankItems().tnTBankItem);
        factionBank.setNumber(64);

        BankResponse bankResponse = IridiumFactions.getInstance().getBankItems().tnTBankItem.withdraw(playerMock, 1000, factionBank);

        assertEquals(64, bankResponse.getAmount());
        assertEquals(BankResponse.BankResponseType.SUCCESS, bankResponse.getBankResponseType());
        assertEquals(64, InventoryUtils.getAmount(playerMock.getInventory(), XMaterial.TNT));
    }

    @Test
    public void TnTBankItemDepositInsufficientFunds() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        FactionBank factionBank = IridiumFactions.getInstance().getFactionManager().getFactionBank(faction, IridiumFactions.getInstance().getBankItems().tnTBankItem);

        BankResponse bankResponse = IridiumFactions.getInstance().getBankItems().tnTBankItem.deposit(playerMock, 64, factionBank);

        assertEquals(0, bankResponse.getAmount());
        assertEquals(BankResponse.BankResponseType.INSUFFICIENT_FUNDS, bankResponse.getBankResponseType());
    }

    @Test
    public void TnTBankItemDepositSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        playerMock.getInventory().addItem(new ItemStack(Material.TNT, 64));
        FactionBank factionBank = IridiumFactions.getInstance().getFactionManager().getFactionBank(faction, IridiumFactions.getInstance().getBankItems().tnTBankItem);

        BankResponse bankResponse = IridiumFactions.getInstance().getBankItems().tnTBankItem.deposit(playerMock, 1000, factionBank);

        assertEquals(64, bankResponse.getAmount());
        assertEquals(BankResponse.BankResponseType.SUCCESS, bankResponse.getBankResponseType());
        assertEquals(0, InventoryUtils.getAmount(playerMock.getInventory(), XMaterial.TNT));
    }

}