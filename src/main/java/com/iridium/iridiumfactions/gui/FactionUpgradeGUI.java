package com.iridium.iridiumfactions.gui;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.gui.GUI;
import com.iridium.iridiumcore.utils.InventoryUtils;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.Upgrade;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.upgrades.UpgradeData;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class FactionUpgradeGUI implements GUI {
    private Faction faction;

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, IridiumFactions.getInstance().getInventories().upgradesGUI.size, StringUtils.color(IridiumFactions.getInstance().getInventories().upgradesGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        inventory.clear();
        InventoryUtils.fillInventory(inventory, IridiumFactions.getInstance().getInventories().upgradesGUI.background);

        for (Map.Entry<String, Upgrade<?>> upgrade : IridiumFactions.getInstance().getUpgradesList().entrySet()) {
            Item item = upgrade.getValue().item;
            int level = IridiumFactions.getInstance().getFactionManager().getFactionUpgrade(faction, upgrade.getKey()).getLevel();
            Map<Integer, ? extends UpgradeData> upgrades = upgrade.getValue().upgrades;
            List<Placeholder> placeholderList = new ArrayList<>();
            placeholderList.add(new Placeholder("level", String.valueOf(level)));
            placeholderList.add(new Placeholder("upgrade_cost", upgrades.containsKey(level + 1) ? String.valueOf(upgrades.get(level + 1).money) : "N/A"));
            placeholderList.addAll(upgrade.getValue().upgrades.get(level).getPlaceholders());

            inventory.setItem(item.slot, ItemStackUtils.makeItem(item, placeholderList));
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        for (Map.Entry<String, Upgrade<?>> upgrade : IridiumFactions.getInstance().getUpgradesList().entrySet()) {
            if (event.getSlot() == upgrade.getValue().item.slot) {
                IridiumFactions.getInstance().getCommands().upgradeCommand.execute(event.getWhoClicked(), new String[]{"", upgrade.getKey()});
                addContent(event.getInventory());
            }
        }
    }
}
