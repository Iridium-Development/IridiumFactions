package com.iridium.iridiumfactions.gui;

import com.iridium.iridiumcore.gui.PagedGUI;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.RelationshipType;
import com.iridium.iridiumfactions.configs.inventories.NoItemGUI;
import com.iridium.iridiumfactions.database.Faction;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class FactionRelationshipsGUI extends PagedGUI<Faction> {

    private final Faction faction;
    private final RelationshipType relationshipType;

    public FactionRelationshipsGUI(Faction faction, RelationshipType relationshipType) {
        super(1, IridiumFactions.getInstance().getInventories().relationshipsGUI.size, IridiumFactions.getInstance().getInventories().relationshipsGUI.background, IridiumFactions.getInstance().getInventories().previousPage, IridiumFactions.getInstance().getInventories().nextPage);
        this.faction = faction;
        this.relationshipType = relationshipType;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = IridiumFactions.getInstance().getInventories().relationshipsGUI;
        Inventory inventory = Bukkit.createInventory(this, getSize(), StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public Collection<Faction> getPageObjects() {
        return IridiumFactions.getInstance().getFactionManager().getFactionRelationships(faction, relationshipType);
    }

    @Override
    public ItemStack getItemStack(Faction faction) {
        return ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().relationshipsGUI.item, Arrays.asList(
                new Placeholder("faction_name", faction.getName()),
                new Placeholder("faction_owner", faction.getOwner().getName()),
                new Placeholder("relationship_type", IridiumFactions.getInstance().getConfiguration().factionRankNames.get(relationshipType.toRank()))
        ));
    }
}
