package com.iridium.iridiumfactions.gui;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumfactions.FactionBuilder;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.UserBuilder;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionClaim;
import com.iridium.iridiumfactions.database.FactionWarp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FactionWarpsGUITest {

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
    public void factionWarpsGUINoWarps() {
        FactionWarpsGUI factionWarpsGUI = new FactionWarpsGUI(new FactionBuilder().build());
        Inventory inventory = factionWarpsGUI.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (IridiumFactions.getInstance().getInventories().warpsGUI.background.items.containsKey(i)) {
                assertEquals(ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().warpsGUI.background.items.get(i)), inventory.getItem(i));
            } else {
                assertEquals(ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().warpsGUI.background.filler), inventory.getItem(i));
            }
        }
    }

    @Test
    public void factionWarpsGUIOneWarp() {
        Faction faction = new FactionBuilder().build();
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction, null, "test"));
        FactionWarpsGUI factionWarpsGUI = new FactionWarpsGUI(faction);
        Inventory inventory = factionWarpsGUI.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i == 9) {
                assertEquals(ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().warpsGUI.item, Arrays.asList(
                        new Placeholder("faction_name", faction.getName()),
                        new Placeholder("warp_name", "test"),
                        new Placeholder("description", "")
                )), inventory.getItem(i));
            } else if (IridiumFactions.getInstance().getInventories().warpsGUI.background.items.containsKey(i)) {
                assertEquals(ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().warpsGUI.background.items.get(i)), inventory.getItem(i));
            } else {
                assertEquals(ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().warpsGUI.background.filler), inventory.getItem(i));
            }
        }
    }

    @Test
    public void factionWarpsGUIOneWarpCustomMaterial() {
        Faction faction = new FactionBuilder().build();
        FactionWarp factionWarp = new FactionWarp(faction, null, "test");
        factionWarp.setIcon(XMaterial.BEDROCK);
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(factionWarp);
        FactionWarpsGUI factionWarpsGUI = new FactionWarpsGUI(faction);
        Inventory inventory = factionWarpsGUI.getInventory();

        ItemStack itemStack = ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().warpsGUI.item, Arrays.asList(
                new Placeholder("faction_name", faction.getName()),
                new Placeholder("warp_name", "test"),
                new Placeholder("description", "")
        ));
        itemStack.setType(Material.BEDROCK);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i == 9) {
                assertEquals(itemStack, inventory.getItem(i));
            } else if (IridiumFactions.getInstance().getInventories().warpsGUI.background.items.containsKey(i)) {
                assertEquals(ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().warpsGUI.background.items.get(i)), inventory.getItem(i));
            } else {
                assertEquals(ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().warpsGUI.background.filler), inventory.getItem(i));
            }
        }
    }

    @Test
    public void factionWarpsGUIOneWarpCustomDescription() {
        Faction faction = new FactionBuilder().build();
        FactionWarp factionWarp = new FactionWarp(faction, null, "test");
        factionWarp.setDescription("Custom Description");
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(factionWarp);
        FactionWarpsGUI factionWarpsGUI = new FactionWarpsGUI(faction);
        Inventory inventory = factionWarpsGUI.getInventory();

        for (int i = 0; i < inventory.getSize(); i++) {
            if (i == 9) {
                assertEquals(ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().warpsGUI.item, Arrays.asList(
                        new Placeholder("faction_name", faction.getName()),
                        new Placeholder("warp_name", "test"),
                        new Placeholder("description", "Custom Description")
                )), inventory.getItem(i));
            } else if (IridiumFactions.getInstance().getInventories().warpsGUI.background.items.containsKey(i)) {
                assertEquals(ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().warpsGUI.background.items.get(i)), inventory.getItem(i));
            } else {
                assertEquals(ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().warpsGUI.background.filler), inventory.getItem(i));
            }
        }
    }

    @Test
    public void factionWarpsGUITwoWarps() {
        Faction faction = new FactionBuilder().build();
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction, null, "Warp 1"));
        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction, null, "Warp 2"));
        FactionWarpsGUI factionWarpsGUI = new FactionWarpsGUI(faction);
        Inventory inventory = factionWarpsGUI.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i == 9) {
                assertEquals(ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().warpsGUI.item, Arrays.asList(
                        new Placeholder("faction_name", faction.getName()),
                        new Placeholder("warp_name", "Warp 1"),
                        new Placeholder("description", "")
                )), inventory.getItem(i));
            } else if (i == 11) {
                assertEquals(ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().warpsGUI.item, Arrays.asList(
                        new Placeholder("faction_name", faction.getName()),
                        new Placeholder("warp_name", "Warp 2"),
                        new Placeholder("description", "")
                )), inventory.getItem(i));
            } else if (IridiumFactions.getInstance().getInventories().warpsGUI.background.items.containsKey(i)) {
                assertEquals(ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().warpsGUI.background.items.get(i)), inventory.getItem(i));
            } else {
                assertEquals(ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().warpsGUI.background.filler), inventory.getItem(i));
            }
        }
    }

    @Test
    public void factionWarpsGUITeleportWarp() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        Location warpLocation = playerMock.getLocation().clone().add(100, 100, 100);

        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction, warpLocation, "test"));
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, warpLocation.getChunk()));
        FactionWarpsGUI factionWarpsGUI = new FactionWarpsGUI(faction);

        playerMock.openInventory(factionWarpsGUI.getInventory());
        factionWarpsGUI.onInventoryClick(new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, 9, ClickType.LEFT, InventoryAction.UNKNOWN));

        assertEquals(warpLocation, playerMock.getLocation());
    }

    @Test
    public void factionWarpsGUIDeleteWarp() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).withFactionRank(FactionRank.OWNER).build();
        Location warpLocation = playerMock.getLocation().clone().add(100, 100, 100);

        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction, warpLocation, "test"));
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, warpLocation.getChunk()));
        FactionWarpsGUI factionWarpsGUI = new FactionWarpsGUI(faction);

        playerMock.openInventory(factionWarpsGUI.getInventory());
        factionWarpsGUI.onInventoryClick(new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, 9, ClickType.RIGHT, InventoryAction.UNKNOWN));

        assertNotEquals(warpLocation, playerMock.getLocation());
        assertEquals(0, IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().getEntries().size());
    }
}