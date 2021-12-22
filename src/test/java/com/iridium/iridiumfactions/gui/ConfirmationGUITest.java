package com.iridium.iridiumfactions.gui;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumfactions.CooldownProvider;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.UserBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ConfirmationGUITest {

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
    public void confirmationGUIYes() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        AtomicBoolean hasAccepted = new AtomicBoolean(false);
        ConfirmationGUI confirmationGUI = new ConfirmationGUI(() -> hasAccepted.set(true), CooldownProvider.newInstance(Duration.ZERO));
        playerMock.openInventory(confirmationGUI.getInventory());

        confirmationGUI.onInventoryClick(new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, IridiumFactions.getInstance().getInventories().confirmationGUI.yes.slot, ClickType.LEFT, InventoryAction.UNKNOWN));
        assertTrue(hasAccepted.get());
        assertNull(playerMock.getOpenInventory().getTopInventory());
    }
    @Test
    public void confirmationGUINo() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        AtomicBoolean hasAccepted = new AtomicBoolean(false);
        ConfirmationGUI confirmationGUI = new ConfirmationGUI(() -> hasAccepted.set(true), CooldownProvider.newInstance(Duration.ZERO));
        playerMock.openInventory(confirmationGUI.getInventory());

        confirmationGUI.onInventoryClick(new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, IridiumFactions.getInstance().getInventories().confirmationGUI.no.slot, ClickType.LEFT, InventoryAction.UNKNOWN));
        assertFalse(hasAccepted.get());
        assertNull(playerMock.getOpenInventory().getTopInventory());
    }

}