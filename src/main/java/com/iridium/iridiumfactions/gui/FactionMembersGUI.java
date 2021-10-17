package com.iridium.iridiumfactions.gui;

import com.iridium.iridiumcore.gui.GUI;
import com.iridium.iridiumcore.utils.InventoryUtils;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.configs.inventories.NoItemGUI;
import com.iridium.iridiumfactions.configs.inventories.SingleItemGUI;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class FactionMembersGUI implements GUI {

    private final Faction faction;

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = IridiumFactions.getInstance().getInventories().membersGUI;
        Inventory inventory = Bukkit.createInventory(this, noItemGUI.size, StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        SingleItemGUI singleItemGUI = IridiumFactions.getInstance().getInventories().membersGUI;
        InventoryUtils.fillInventory(inventory, singleItemGUI.background);
        AtomicInteger slot = new AtomicInteger(0);
        for (User user : IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction)) {
            int itemSlot = slot.getAndIncrement();
            inventory.setItem(itemSlot, ItemStackUtils.makeItem(singleItemGUI.item, Arrays.asList(
                    new Placeholder("player_name", user.getName()),
                    new Placeholder("player_rank", user.getFactionRank().name()),
                    new Placeholder("player_join", user.getJoinTime().format(DateTimeFormatter.ofPattern(IridiumFactions.getInstance().getConfiguration().dateTimeFormat)))
            )));
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }
}
