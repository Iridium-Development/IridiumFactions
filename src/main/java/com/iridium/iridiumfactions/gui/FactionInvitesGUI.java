package com.iridium.iridiumfactions.gui;

import com.iridium.iridiumcore.gui.PagedGUI;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.configs.inventories.NoItemGUI;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionInvite;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

public class FactionInvitesGUI extends PagedGUI<FactionInvite> {

    private final Faction faction;

    public FactionInvitesGUI(Faction faction) {
        super(1, IridiumFactions.getInstance().getInventories().invitesGUI.size, IridiumFactions.getInstance().getInventories().invitesGUI.background, IridiumFactions.getInstance().getInventories().previousPage, IridiumFactions.getInstance().getInventories().nextPage);
        this.faction = faction;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = IridiumFactions.getInstance().getInventories().invitesGUI;
        Inventory inventory = Bukkit.createInventory(this, getSize(), StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public Collection<FactionInvite> getPageObjects() {
        return IridiumFactions.getInstance().getFactionManager().getFactionInvites(faction);
    }

    @Override
    public ItemStack getItemStack(FactionInvite factionInvite) {
        User user = IridiumFactions.getInstance().getUserManager().getUserByUUID(factionInvite.getUser()).orElse(new User(UUID.randomUUID(), "N/A"));
        return ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().invitesGUI.item, Arrays.asList(new Placeholder("player_name", user.getName()), new Placeholder("player_rank", user.getFactionRank().name())));
    }
}
