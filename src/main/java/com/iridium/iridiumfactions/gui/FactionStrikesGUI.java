package com.iridium.iridiumfactions.gui;

import com.iridium.iridiumcore.gui.PagedGUI;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.configs.inventories.NoItemGUI;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionStrike;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

public class FactionStrikesGUI extends PagedGUI<FactionStrike> {

    private final Faction faction;

    public FactionStrikesGUI(Faction faction) {
        super(1, IridiumFactions.getInstance().getInventories().strikesGUI.size, IridiumFactions.getInstance().getInventories().strikesGUI.background, IridiumFactions.getInstance().getInventories().previousPage, IridiumFactions.getInstance().getInventories().nextPage);
        this.faction = faction;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = IridiumFactions.getInstance().getInventories().strikesGUI;
        Inventory inventory = Bukkit.createInventory(this, getSize(), StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public Collection<FactionStrike> getPageObjects() {
        return IridiumFactions.getInstance().getFactionManager().getFactionStrikes(faction);
    }

    @Override
    public ItemStack getItemStack(FactionStrike factionStrike) {
        User user = IridiumFactions.getInstance().getUserManager().getUserByUUID(factionStrike.getUser()).orElse(new User(UUID.randomUUID(), "N/A"));
        return ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().strikesGUI.item, Arrays.asList(new Placeholder("player_name", user.getName()), new Placeholder("reason", factionStrike.getReason())));
    }
}
