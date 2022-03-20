package com.iridium.iridiumfactions.gui;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.gui.GUI;
import com.iridium.iridiumcore.utils.InventoryUtils;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.Booster;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.configs.inventories.NoItemGUI;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionBooster;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;

@AllArgsConstructor
public class FactionBoostersGUI implements GUI {
    private final Faction faction;

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = IridiumFactions.getInstance().getInventories().boostersGUI;
        Inventory inventory = Bukkit.createInventory(this, noItemGUI.size, StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        InventoryUtils.fillInventory(inventory, IridiumFactions.getInstance().getInventories().boostersGUI.background);

        for (Map.Entry<String, Booster> entry : IridiumFactions.getInstance().getBoosterList().entrySet()) {
            Item item = entry.getValue().item;
            FactionBooster islandBooster = IridiumFactions.getInstance().getFactionManager().getFactionBooster(faction, entry.getKey());
            long minutes = LocalDateTime.now().until(islandBooster.getTime(), ChronoUnit.MINUTES);
            long seconds = LocalDateTime.now().until(islandBooster.getTime(), ChronoUnit.SECONDS) - minutes * 60;
            inventory.setItem(item.slot, ItemStackUtils.makeItem(item, Arrays.asList(
                    new Placeholder("timeremaining_minutes", String.valueOf(Math.max(minutes, 0))),
                    new Placeholder("timeremaining_seconds", String.valueOf(Math.max(seconds, 0))),
                    new Placeholder("cost", IridiumFactions.getInstance().getNumberFormatter().format(entry.getValue().cost))
            )));
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
        for (Map.Entry<String, Booster> entry : IridiumFactions.getInstance().getBoosterList().entrySet()) {
            if (entry.getValue().item.slot == event.getSlot()) {
                IridiumFactions.getInstance().getCommands().boosterCommand.execute(event.getWhoClicked(), new String[]{"", entry.getKey()});
                return;
            }
        }
    }
}
