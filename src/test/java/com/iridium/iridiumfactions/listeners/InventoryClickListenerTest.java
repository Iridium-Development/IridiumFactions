package com.iridium.iridiumfactions.listeners;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.TestGUI;
import com.iridium.iridiumfactions.UserBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryClickListenerTest {

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
    public void onInventoryClickNotIridiumGUI() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        Inventory inventory = Bukkit.createInventory(null, 27, "Test");
        playerMock.openInventory(inventory);
        InventoryClickEvent inventoryClickEvent = new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.UNKNOWN);
        serverMock.getPluginManager().callEvent(inventoryClickEvent);
        assertFalse(inventoryClickEvent.isCancelled());
        assertEquals(inventory, playerMock.getOpenInventory().getTopInventory());
    }

    @Test
    public void onInventoryClickIridiumGUI() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        TestGUI testGUI = new TestGUI();
        Inventory inventory = testGUI.getInventory();
        playerMock.openInventory(inventory);

        InventoryClickEvent inventoryClickEvent = new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.UNKNOWN);
        serverMock.getPluginManager().callEvent(inventoryClickEvent);

        assertTrue(inventoryClickEvent.isCancelled());
        assertTrue(testGUI.hasBeenClicked());
    }

}