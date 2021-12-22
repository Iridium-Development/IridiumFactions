package com.iridium.iridiumfactions.gui;

import com.iridium.iridiumcore.gui.GUI;
import com.iridium.iridiumcore.utils.InventoryUtils;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.Permission;
import com.iridium.iridiumfactions.PermissionType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

/**
 * GUI which allows users to alter the Factions's permissions.
 */
@AllArgsConstructor
@Getter
public class FactionPermissionsGUI implements GUI {

    private int page;
    private final Faction faction;
    private final FactionRank factionRank;

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, IridiumFactions.getInstance().getInventories().factionPermissionsGUI.size, StringUtils.color(IridiumFactions.getInstance().getInventories().factionPermissionsGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        inventory.clear();
        InventoryUtils.fillInventory(inventory, IridiumFactions.getInstance().getInventories().factionPermissionsGUI.background);
        inventory.setItem(inventory.getSize() - 3, ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().nextPage));
        inventory.setItem(inventory.getSize() - 7, ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().previousPage));

        for (Map.Entry<String, Permission> permission : IridiumFactions.getInstance().getPermissionList().entrySet()) {
            if (permission.getValue().getPage() != page) continue;
            boolean allowed = IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, factionRank, permission.getValue(), permission.getKey());
            inventory.setItem(permission.getValue().getItem().slot, ItemStackUtils.makeItem(permission.getValue().getItem(), Collections.singletonList(new Placeholder("permission", allowed ? IridiumFactions.getInstance().getPermissions().allowed : IridiumFactions.getInstance().getPermissions().denied))));
        }
    }

    /**
     * Called when there is a click in this GUI.
     * Cancelled automatically.
     *
     * @param event The InventoryClickEvent provided by Bukkit
     */
    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        for (Map.Entry<String, Permission> permission : IridiumFactions.getInstance().getPermissionList().entrySet()) {
            if (permission.getValue().getItem().slot != event.getSlot()) continue;
            if (permission.getValue().getPage() != page) continue;

            User user = IridiumFactions.getInstance().getUserManager().getUser((Player) event.getWhoClicked());
            if (user.getFactionRank().getLevel() <= factionRank.getLevel() || !IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, user, PermissionType.CHANGE_PERMISSIONS)) {
                event.getWhoClicked().sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotChangePermissions.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            } else {
                boolean allowed = IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, factionRank, permission.getValue(), permission.getKey());
                IridiumFactions.getInstance().getFactionManager().setFactionPermission(faction, factionRank, permission.getKey(), !allowed);
                event.getWhoClicked().openInventory(getInventory());
            }
            return;
        }

        if (event.getSlot() == IridiumFactions.getInstance().getInventories().factionPermissionsGUI.size - 7 && page > 1) {
            page--;
            event.getWhoClicked().openInventory(getInventory());
            return;
        }

        if (event.getSlot() == IridiumFactions.getInstance().getInventories().factionPermissionsGUI.size - 3 && IridiumFactions.getInstance().getPermissionList().values().stream().anyMatch(permission -> permission.getPage() == page + 1)) {
            page++;
            event.getWhoClicked().openInventory(getInventory());
        }
    }
}
