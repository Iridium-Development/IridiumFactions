package com.iridium.iridiumfactions.gui;

import com.iridium.iridiumcore.gui.GUI;
import com.iridium.iridiumcore.utils.InventoryUtils;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionWarp;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class FactionWarpsGUI implements GUI {
    private final Faction faction;

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, IridiumFactions.getInstance().getInventories().warpsGUI.size, StringUtils.color(IridiumFactions.getInstance().getInventories().warpsGUI.title.replace("%faction_name%", faction.getName())));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        InventoryUtils.fillInventory(inventory, IridiumFactions.getInstance().getInventories().warpsGUI.background);
        List<FactionWarp> factionWarpList = IridiumFactions.getInstance().getFactionManager().getFactionWarps(faction);
        for (int i = 0; i < Math.min(factionWarpList.size(), IridiumFactions.getInstance().getConfiguration().factionWarpSlots.size()); i++) {
            FactionWarp factionWarp = factionWarpList.get(i);
            ItemStack itemStack = ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().warpsGUI.item, Arrays.asList(
                    new Placeholder("faction_name", faction.getName()),
                    new Placeholder("warp_name", factionWarp.getName()),
                    new Placeholder("description", factionWarp.getDescription() != null ? factionWarp.getDescription() : "")
            ));
            Material material = factionWarp.getIcon().parseMaterial();
            if (material != null) itemStack.setType(material);
            inventory.setItem(IridiumFactions.getInstance().getConfiguration().factionWarpSlots.get(i), itemStack);
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        List<FactionWarp> factionWarpList = IridiumFactions.getInstance().getFactionManager().getFactionWarps(faction);
        for (int i = 0; i < Math.min(factionWarpList.size(), IridiumFactions.getInstance().getConfiguration().factionWarpSlots.size()); i++) {
            FactionWarp factionWarp = factionWarpList.get(i);
            if (inventoryClickEvent.getSlot() == IridiumFactions.getInstance().getConfiguration().factionWarpSlots.get(i)) {
                IridiumFactions.getInstance().getCommands().warpCommand.execute(inventoryClickEvent.getWhoClicked(), new String[]{"warp", factionWarp.getName()});
            }
        }
    }
}
