package com.iridium.iridiumfactions.gui;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumcore.Item;
import com.iridium.iridiumfactions.FactionBuilder;
import com.iridium.iridiumfactions.FactionRank;
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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FactionRanksGUITest {

    private ServerMock serverMock;
    private Map<FactionRank, Item> items;

    @BeforeEach
    public void setup() {
        this.serverMock = MockBukkit.mock();
        MockBukkit.load(IridiumFactions.class);
        items = ImmutableMap.<FactionRank, Item>builder()
                .put(FactionRank.ENEMY, IridiumFactions.getInstance().getInventories().factionRanksGUI.enemy)
                .put(FactionRank.ALLY, IridiumFactions.getInstance().getInventories().factionRanksGUI.ally)
                .put(FactionRank.TRUCE, IridiumFactions.getInstance().getInventories().factionRanksGUI.truce)
                .put(FactionRank.MEMBER, IridiumFactions.getInstance().getInventories().factionRanksGUI.member)
                .put(FactionRank.MODERATOR, IridiumFactions.getInstance().getInventories().factionRanksGUI.moderator)
                .put(FactionRank.CO_OWNER, IridiumFactions.getInstance().getInventories().factionRanksGUI.coOwner)
                .put(FactionRank.OWNER, IridiumFactions.getInstance().getInventories().factionRanksGUI.owner)
                .build();
    }

    @AfterEach
    public void tearDown() {
        Bukkit.getScheduler().cancelTasks(IridiumFactions.getInstance());
        MockBukkit.unmock();
    }

    @Test
    public void onFactionRankGUIClick() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        FactionRanksGUI factionRanksGUI = new FactionRanksGUI(new FactionBuilder().build());
        for (FactionRank factionRank : items.keySet()) {
            playerMock.openInventory(factionRanksGUI.getInventory());

            factionRanksGUI.onInventoryClick(new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, items.get(factionRank).slot, ClickType.LEFT, InventoryAction.UNKNOWN));

            assertTrue(playerMock.getOpenInventory().getTopInventory().getHolder() instanceof FactionPermissionsGUI);
            assertEquals(factionRank, ((FactionPermissionsGUI) playerMock.getOpenInventory().getTopInventory().getHolder()).getFactionRank());
        }
    }

}