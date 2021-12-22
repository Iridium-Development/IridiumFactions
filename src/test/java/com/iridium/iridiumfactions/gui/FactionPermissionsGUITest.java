package com.iridium.iridiumfactions.gui;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.*;
import com.iridium.iridiumfactions.database.Faction;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FactionPermissionsGUITest {

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
    public void factionPermissionsGUINextPageError() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        FactionPermissionsGUI factionPermissionsGUI = new FactionPermissionsGUI(1, new FactionBuilder().build(), FactionRank.MEMBER);
        playerMock.openInventory(factionPermissionsGUI.getInventory());

        factionPermissionsGUI.onInventoryClick(new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, IridiumFactions.getInstance().getInventories().factionPermissionsGUI.size - 3, ClickType.LEFT, InventoryAction.UNKNOWN));
        assertEquals(1, factionPermissionsGUI.getPage());
    }

    @Test
    public void factionPermissionsGUINextPageSuccess() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        FactionPermissionsGUI factionPermissionsGUI = new FactionPermissionsGUI(0, new FactionBuilder().build(), FactionRank.MEMBER);
        playerMock.openInventory(factionPermissionsGUI.getInventory());

        factionPermissionsGUI.onInventoryClick(new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, IridiumFactions.getInstance().getInventories().factionPermissionsGUI.size - 3, ClickType.LEFT, InventoryAction.UNKNOWN));
        assertEquals(1, factionPermissionsGUI.getPage());
    }

    @Test
    public void factionPermissionsGUIPreviousPageError() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        FactionPermissionsGUI factionPermissionsGUI = new FactionPermissionsGUI(1, new FactionBuilder().build(), FactionRank.MEMBER);
        playerMock.openInventory(factionPermissionsGUI.getInventory());

        factionPermissionsGUI.onInventoryClick(new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, IridiumFactions.getInstance().getInventories().factionPermissionsGUI.size - 7, ClickType.LEFT, InventoryAction.UNKNOWN));
        assertEquals(1, factionPermissionsGUI.getPage());
    }

    @Test
    public void factionPermissionsGUIPreviousPageSuccess() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        FactionPermissionsGUI factionPermissionsGUI = new FactionPermissionsGUI(2, new FactionBuilder().build(), FactionRank.MEMBER);
        playerMock.openInventory(factionPermissionsGUI.getInventory());

        factionPermissionsGUI.onInventoryClick(new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, IridiumFactions.getInstance().getInventories().factionPermissionsGUI.size - 7, ClickType.LEFT, InventoryAction.UNKNOWN));
        assertEquals(1, factionPermissionsGUI.getPage());
    }

    @Test
    public void factionPermissionsGUIChangePermissionsNoPermission() {
        Map.Entry<String, Permission> permission = IridiumFactions.getInstance().getPermissionList().entrySet().stream().filter(perm -> perm.getValue().getPage() == 1).findFirst().get();
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFactionRank(FactionRank.CO_OWNER).withFaction(faction).build();
        FactionPermissionsGUI factionPermissionsGUI = new FactionPermissionsGUI(permission.getValue().getPage(), faction, FactionRank.MEMBER);
        playerMock.openInventory(factionPermissionsGUI.getInventory());

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.CO_OWNER, PermissionType.CHANGE_PERMISSIONS.getPermissionKey(), false);

        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, FactionRank.MEMBER, permission.getValue(), permission.getKey()));

        factionPermissionsGUI.onInventoryClick(new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, permission.getValue().getItem().slot, ClickType.LEFT, InventoryAction.UNKNOWN));

        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, FactionRank.MEMBER, permission.getValue(), permission.getKey()));

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotChangePermissions.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void factionPermissionsGUIChangePermissionsHigherRank() {
        Map.Entry<String, Permission> permission = IridiumFactions.getInstance().getPermissionList().entrySet().stream().filter(perm -> perm.getValue().getPage() == 1).findFirst().get();
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFactionRank(FactionRank.MEMBER).withFaction(faction).build();
        FactionPermissionsGUI factionPermissionsGUI = new FactionPermissionsGUI(permission.getValue().getPage(), faction, FactionRank.CO_OWNER);
        playerMock.openInventory(factionPermissionsGUI.getInventory());

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.MEMBER, PermissionType.CHANGE_PERMISSIONS.getPermissionKey(), true);

        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, FactionRank.MEMBER, permission.getValue(), permission.getKey()));

        factionPermissionsGUI.onInventoryClick(new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, permission.getValue().getItem().slot, ClickType.LEFT, InventoryAction.UNKNOWN));

        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, FactionRank.MEMBER, permission.getValue(), permission.getKey()));

        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotChangePermissions.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void factionPermissionsGUIChangePermissionsSuccess() {
        Map.Entry<String, Permission> permission = IridiumFactions.getInstance().getPermissionList().entrySet().stream().filter(perm -> perm.getValue().getPage() == 1).findFirst().get();
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFactionRank(FactionRank.CO_OWNER).withFaction(faction).build();
        FactionPermissionsGUI factionPermissionsGUI = new FactionPermissionsGUI(permission.getValue().getPage(), faction, FactionRank.MEMBER);
        playerMock.openInventory(factionPermissionsGUI.getInventory());

        IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, FactionRank.CO_OWNER, PermissionType.CHANGE_PERMISSIONS.getPermissionKey(), true);

        assertTrue(IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, FactionRank.MEMBER, permission.getValue(), permission.getKey()));

        factionPermissionsGUI.onInventoryClick(new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, permission.getValue().getItem().slot, ClickType.LEFT, InventoryAction.UNKNOWN));

        assertFalse(IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, FactionRank.MEMBER, permission.getValue(), permission.getKey()));
    }

}