package com.iridium.iridiumfactions.gui;

import com.iridium.iridiumcore.gui.PagedGUI;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.configs.inventories.NoItemGUI;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;

public class FactionMembersGUI extends PagedGUI<User> {

    private final Faction faction;

    public FactionMembersGUI(Faction faction) {
        super(1, IridiumFactions.getInstance().getInventories().invitesGUI.size, IridiumFactions.getInstance().getInventories().invitesGUI.background, IridiumFactions.getInstance().getInventories().previousPage, IridiumFactions.getInstance().getInventories().nextPage);
        this.faction = faction;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = IridiumFactions.getInstance().getInventories().membersGUI;
        Inventory inventory = Bukkit.createInventory(this, getSize(), StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public Collection<User> getPageObjects() {
        return IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction);
    }

    @Override
    public ItemStack getItemStack(User user) {
        return ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().membersGUI.item, Arrays.asList(
                new Placeholder("player_name", user.getName()),
                new Placeholder("player_rank", user.getFactionRank().name()),
                new Placeholder("player_join", user.getJoinTime().format(DateTimeFormatter.ofPattern(IridiumFactions.getInstance().getConfiguration().dateTimeFormat)))
        ));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        super.onInventoryClick(event);
        User user = getItem(event.getSlot());
        if (user == null) return;

        switch (event.getClick()) {
            case LEFT:
                if (user.getFactionRank() != FactionRank.MEMBER) {
                    IridiumFactions.getInstance().getCommands().demoteCommand.execute(event.getWhoClicked(), new String[]{"demote", user.getName()});
                } else {
                    IridiumFactions.getInstance().getCommands().kickCommand.execute(event.getWhoClicked(), new String[]{"kick", user.getName()});
                }
                break;
            case RIGHT:
                IridiumFactions.getInstance().getCommands().promoteCommand.execute(event.getWhoClicked(), new String[]{"promote", user.getName()});
                break;
            default:
                return;
        }

        addContent(event.getInventory());
    }
}