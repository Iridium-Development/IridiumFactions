package com.iridium.iridiumfactions.listeners;

import com.iridium.iridiumcore.GUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof GUI) {
            event.setCancelled(true);
            if(event.getClickedInventory() == event.getInventory()) {
                ((GUI) event.getInventory().getHolder()).onInventoryClick(event);
            }
        }
    }
}
