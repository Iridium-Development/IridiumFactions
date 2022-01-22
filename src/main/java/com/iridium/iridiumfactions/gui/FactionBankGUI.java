package com.iridium.iridiumfactions.gui;

import com.iridium.iridiumcore.gui.GUI;
import com.iridium.iridiumcore.utils.InventoryUtils;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.bank.BankItem;
import com.iridium.iridiumfactions.configs.inventories.NoItemGUI;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionBank;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Optional;

@AllArgsConstructor
public class FactionBankGUI implements GUI {
    private Faction faction;

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = IridiumFactions.getInstance().getInventories().bankGUI;
        Inventory inventory = Bukkit.createInventory(this, noItemGUI.size, StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        inventory.clear();
        InventoryUtils.fillInventory(inventory, IridiumFactions.getInstance().getInventories().bankGUI.background);

        for (BankItem bankItem : IridiumFactions.getInstance().getBankItemList()) {
            FactionBank islandBank = IridiumFactions.getInstance().getFactionManager().getFactionBank(faction, bankItem);
            inventory.setItem(bankItem.getItem().slot, ItemStackUtils.makeItem(bankItem.getItem(), Collections.singletonList(new Placeholder("amount", IridiumFactions.getInstance().getNumberFormatter().format(islandBank.getNumber())))));
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Optional<BankItem> bankItem = IridiumFactions.getInstance().getBankItemList().stream().filter(item -> item.getItem().slot == event.getSlot()).findFirst();
        if (!bankItem.isPresent()) return;

        switch (event.getClick()) {
            case LEFT:
                IridiumFactions.getInstance().getCommands().withdrawCommand.execute(event.getWhoClicked(), new String[]{"", bankItem.get().getName(), String.valueOf(bankItem.get().getDefaultAmount())});
                break;
            case RIGHT:
                IridiumFactions.getInstance().getCommands().depositCommand.execute(event.getWhoClicked(), new String[]{"", bankItem.get().getName(), String.valueOf(bankItem.get().getDefaultAmount())});
                break;
            case SHIFT_LEFT:
                IridiumFactions.getInstance().getCommands().withdrawCommand.execute(event.getWhoClicked(), new String[]{"", bankItem.get().getName(), String.valueOf(Double.MAX_VALUE)});
                break;
            case SHIFT_RIGHT:
                IridiumFactions.getInstance().getCommands().depositCommand.execute(event.getWhoClicked(), new String[]{"", bankItem.get().getName(), String.valueOf(Double.MAX_VALUE)});
                break;
        }

        addContent(event.getInventory());
    }
}
