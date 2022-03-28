package com.iridium.iridiumfactions.gui;

import com.iridium.iridiumcore.gui.PagedGUI;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.configs.inventories.NoItemGUI;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionRelationshipRequest;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class FactionRelationshipRequestsGUI extends PagedGUI<FactionRelationshipRequest> {

    private final Faction faction;

    public FactionRelationshipRequestsGUI(Faction faction) {
        super(1, IridiumFactions.getInstance().getInventories().relationshipRequestsGUI.size, IridiumFactions.getInstance().getInventories().relationshipRequestsGUI.background, IridiumFactions.getInstance().getInventories().previousPage, IridiumFactions.getInstance().getInventories().nextPage);
        this.faction = faction;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = IridiumFactions.getInstance().getInventories().relationshipRequestsGUI;
        Inventory inventory = Bukkit.createInventory(this, getSize(), StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public Collection<FactionRelationshipRequest> getPageObjects() {
        return IridiumFactions.getInstance().getFactionManager().getFactionRelationshipRequests(faction);
    }

    @Override
    public ItemStack getItemStack(FactionRelationshipRequest relationshipRequest) {
        int factionID = relationshipRequest.getFactionID() == faction.getId() ? relationshipRequest.getFaction2ID() : relationshipRequest.getFactionID();
        Faction faction = IridiumFactions.getInstance().getFactionManager().getFactionViaId(factionID);
        return ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().relationshipRequestsGUI.item, Arrays.asList(
                new Placeholder("faction_name", faction.getName()),
                new Placeholder("faction_owner", faction.getOwner().getName()),
                new Placeholder("relationship_type", IridiumFactions.getInstance().getConfiguration().factionRankNames.get(relationshipRequest.getRelationshipType().toRank())),
                new Placeholder("player_name", relationshipRequest.getUser().map(User::getName).orElse("N/A"))
        ));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        super.onInventoryClick(event);
        User user = IridiumFactions.getInstance().getUserManager().getUser((OfflinePlayer) event.getWhoClicked());
        FactionRelationshipRequest relationshipRequest = getItem(event.getSlot());
        if (relationshipRequest == null) return;
        if (event.getClick() == ClickType.LEFT) {
            if (relationshipRequest.getFactionID() == user.getFactionID()) return;
            switch (relationshipRequest.getRelationshipType()) {
                case ALLY:
                    IridiumFactions.getInstance().getCommands().allyCommand.execute(event.getWhoClicked(), new String[]{"ally", relationshipRequest.getFaction().getName()});
                    break;
                case TRUCE:
                    IridiumFactions.getInstance().getCommands().truceCommand.execute(event.getWhoClicked(), new String[]{"truce", relationshipRequest.getFaction().getName()});
                    break;
            }
        } else {
            int factionID = relationshipRequest.getFactionID() == user.getFactionID() ? relationshipRequest.getFaction2ID() : relationshipRequest.getFactionID();
            IridiumFactions.getInstance().getCommands().declineRequestCommand.execute(event.getWhoClicked(), new String[]{"decline", IridiumFactions.getInstance().getFactionManager().getFactionViaId(factionID).getName()});
        }
    }
}
