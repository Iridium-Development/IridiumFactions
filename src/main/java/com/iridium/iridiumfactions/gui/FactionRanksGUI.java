package com.iridium.iridiumfactions.gui;

import com.iridium.iridiumcore.gui.GUI;
import com.iridium.iridiumcore.utils.InventoryUtils;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.configs.inventories.FactionRanksInventoryConfig;
import com.iridium.iridiumfactions.database.Faction;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * GUI which allows users to select ranks to edit in the {@link com.iridium.iridiumfactions.FactionRank}.
 */
@AllArgsConstructor
public class FactionRanksGUI implements GUI {

    private final Faction faction;

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, IridiumFactions.getInstance().getInventories().factionRanksGUI.size, StringUtils.color(IridiumFactions.getInstance().getInventories().factionRanksGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        inventory.clear();
        FactionRanksInventoryConfig factionRanks = IridiumFactions.getInstance().getInventories().factionRanksGUI;
        InventoryUtils.fillInventory(inventory, factionRanks.background);
        inventory.setItem(factionRanks.owner.slot, ItemStackUtils.makeItem(factionRanks.owner));
        inventory.setItem(factionRanks.coOwner.slot, ItemStackUtils.makeItem(factionRanks.coOwner));
        inventory.setItem(factionRanks.moderator.slot, ItemStackUtils.makeItem(factionRanks.moderator));
        inventory.setItem(factionRanks.member.slot, ItemStackUtils.makeItem(factionRanks.member));
        inventory.setItem(factionRanks.truce.slot, ItemStackUtils.makeItem(factionRanks.truce));
        inventory.setItem(factionRanks.ally.slot, ItemStackUtils.makeItem(factionRanks.ally));
        inventory.setItem(factionRanks.enemy.slot, ItemStackUtils.makeItem(factionRanks.enemy));
    }

    /**
     * Called when there is a click in this GUI.
     * Cancelled automatically.
     *
     * @param event The InventoryClickEvent provided by Bukkit
     */

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        FactionRanksInventoryConfig factionRanks = IridiumFactions.getInstance().getInventories().factionRanksGUI;
        if (event.getSlot() == factionRanks.owner.slot) {
            event.getWhoClicked().openInventory(new FactionPermissionsGUI(1, faction, FactionRank.OWNER).getInventory());
        }
        if (event.getSlot() == factionRanks.coOwner.slot) {
            event.getWhoClicked().openInventory(new FactionPermissionsGUI(1, faction, FactionRank.CO_OWNER).getInventory());
        }
        if (event.getSlot() == factionRanks.moderator.slot) {
            event.getWhoClicked().openInventory(new FactionPermissionsGUI(1, faction, FactionRank.MODERATOR).getInventory());
        }
        if (event.getSlot() == factionRanks.member.slot) {
            event.getWhoClicked().openInventory(new FactionPermissionsGUI(1, faction, FactionRank.MEMBER).getInventory());
        }
        if (event.getSlot() == factionRanks.truce.slot) {
            event.getWhoClicked().openInventory(new FactionPermissionsGUI(1, faction, FactionRank.TRUCE).getInventory());
        }
        if (event.getSlot() == factionRanks.ally.slot) {
            event.getWhoClicked().openInventory(new FactionPermissionsGUI(1, faction, FactionRank.ALLY).getInventory());
        }
        if (event.getSlot() == factionRanks.enemy.slot) {
            event.getWhoClicked().openInventory(new FactionPermissionsGUI(1, faction, FactionRank.ENEMY).getInventory());
        }
    }

}