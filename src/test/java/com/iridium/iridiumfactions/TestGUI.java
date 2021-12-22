package com.iridium.iridiumfactions;

import com.iridium.iridiumcore.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class TestGUI implements GUI {

    private boolean hasBeenClicked = false;

    @NotNull
    @Override
    public Inventory getInventory() {
        return Bukkit.createInventory(this, 27, "TestGUI");
    }

    @Override
    public void addContent(Inventory inventory) {

    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        hasBeenClicked = true;
    }

    public boolean hasBeenClicked() {
        return hasBeenClicked;
    }
}
