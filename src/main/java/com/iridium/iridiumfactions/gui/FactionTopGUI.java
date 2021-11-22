package com.iridium.iridiumfactions.gui;

import com.iridium.iridiumcore.gui.GUI;
import com.iridium.iridiumcore.utils.InventoryUtils;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.PlaceholderBuilder;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.managers.FactionManager;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FactionTopGUI implements GUI {

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, IridiumFactions.getInstance().getInventories().factionTopGUI.size, StringUtils.color(IridiumFactions.getInstance().getInventories().factionTopGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        List<Faction> factions = IridiumFactions.getInstance().getFactionManager().getFactions(FactionManager.SortType.VALUE);
        InventoryUtils.fillInventory(inventory, IridiumFactions.getInstance().getInventories().factionTopGUI.background);
        for (int rank : IridiumFactions.getInstance().getConfiguration().factionTopSlots.keySet()) {
            int slot = IridiumFactions.getInstance().getConfiguration().factionTopSlots.get(rank);
            if (factions.size() >= rank) {
                inventory.setItem(slot, ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().factionTopGUI.item, PlaceholderBuilder.getFactionPlaceholders(factions.get(rank - 1))));
            } else {
                inventory.setItem(slot, ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().factionTopGUI.filler));
            }
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
    }

}
