package com.iridium.iridiumfactions.gui;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumcore.utils.InventoryUtils;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionBuilder;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.UserBuilder;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionBank;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FactionBankGUITest {

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
    public void withdraw() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        FactionBank factionBank = IridiumFactions.getInstance().getFactionManager().getFactionBank(faction, IridiumFactions.getInstance().getBankItems().tnTBankItem);
        factionBank.setNumber(1000);
        FactionBankGUI factionBankGUI = new FactionBankGUI(faction);

        playerMock.openInventory(factionBankGUI.getInventory());
        factionBankGUI.onInventoryClick(new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, IridiumFactions.getInstance().getBankItems().tnTBankItem.getItem().slot, ClickType.LEFT, InventoryAction.UNKNOWN));

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().bankWithdrew
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%amount%", "64.0")
                .replace("%type%", "TnT")
        ));
        playerMock.assertNoMoreSaid();
        assertEquals(1000 - IridiumFactions.getInstance().getBankItems().tnTBankItem.getDefaultAmount(), factionBank.getNumber());
        assertEquals(64, InventoryUtils.getAmount(playerMock.getInventory(), XMaterial.TNT));
    }

    @Test
    public void withdrawAll() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        FactionBank factionBank = IridiumFactions.getInstance().getFactionManager().getFactionBank(faction, IridiumFactions.getInstance().getBankItems().tnTBankItem);
        factionBank.setNumber(1000);
        FactionBankGUI factionBankGUI = new FactionBankGUI(faction);

        playerMock.openInventory(factionBankGUI.getInventory());
        factionBankGUI.onInventoryClick(new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, IridiumFactions.getInstance().getBankItems().tnTBankItem.getItem().slot, ClickType.SHIFT_LEFT, InventoryAction.UNKNOWN));

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().bankWithdrew
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%amount%", "1000.0")
                .replace("%type%", "TnT")
        ));
        playerMock.assertNoMoreSaid();
        assertEquals(0, factionBank.getNumber());
        assertEquals(1000, InventoryUtils.getAmount(playerMock.getInventory(), XMaterial.TNT));
    }

    @Test
    public void deposit() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        FactionBank factionBank = IridiumFactions.getInstance().getFactionManager().getFactionBank(faction, IridiumFactions.getInstance().getBankItems().tnTBankItem);
        playerMock.getInventory().addItem(new ItemStack(Material.TNT, 64));
        FactionBankGUI factionBankGUI = new FactionBankGUI(faction);

        playerMock.openInventory(factionBankGUI.getInventory());
        factionBankGUI.onInventoryClick(new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, IridiumFactions.getInstance().getBankItems().tnTBankItem.getItem().slot, ClickType.RIGHT, InventoryAction.UNKNOWN));

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().bankDeposited
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%amount%", "64.0")
                .replace("%type%", "TnT")
        ));
        playerMock.assertNoMoreSaid();
        assertEquals(64, factionBank.getNumber());
        assertEquals(0, InventoryUtils.getAmount(playerMock.getInventory(), XMaterial.TNT));
    }

    @Test
    public void depositAll() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        FactionBank factionBank = IridiumFactions.getInstance().getFactionManager().getFactionBank(faction, IridiumFactions.getInstance().getBankItems().tnTBankItem);
        playerMock.getInventory().addItem(new ItemStack(Material.TNT, 64));
        playerMock.getInventory().addItem(new ItemStack(Material.TNT, 32));
        playerMock.getInventory().addItem(new ItemStack(Material.TNT, 32));
        FactionBankGUI factionBankGUI = new FactionBankGUI(faction);

        playerMock.openInventory(factionBankGUI.getInventory());
        factionBankGUI.onInventoryClick(new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, IridiumFactions.getInstance().getBankItems().tnTBankItem.getItem().slot, ClickType.SHIFT_RIGHT, InventoryAction.UNKNOWN));

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().bankDeposited
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%amount%", "128.0")
                .replace("%type%", "TnT")
        ));
        playerMock.assertNoMoreSaid();
        assertEquals(128, factionBank.getNumber());
        assertEquals(0, InventoryUtils.getAmount(playerMock.getInventory(), XMaterial.TNT));
    }

}