package com.iridium.iridiumfactions.gui;

import com.iridium.iridiumcore.gui.GUI;
import com.iridium.iridiumcore.utils.InventoryUtils;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionRank;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class FactionMembersGUI implements GUI {

    private final Faction faction;
    private final Map<Integer, User> members = new HashMap<>();

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
        members.clear();
        SingleItemGUI singleItemGUI = IridiumFactions.getInstance().getInventories().membersGUI;
        InventoryUtils.fillInventory(inventory, singleItemGUI.background);
        AtomicInteger slot = new AtomicInteger(0);
        List<User> users = IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction);
        users.sort(Comparator.comparing(User::getFactionRank));
        for (User user : users) {
            int itemSlot = slot.getAndIncrement();
            members.put(itemSlot, user);
            inventory.setItem(itemSlot, ItemStackUtils.makeItem(singleItemGUI.item, Arrays.asList(
                    new Placeholder("player_name", user.getName()),
                    new Placeholder("player_rank", user.getFactionRank().name()),
                    new Placeholder("player_join", user.getJoinTime().format(DateTimeFormatter.ofPattern(IridiumFactions.getInstance().getConfiguration().dateTimeFormat)))
            )));
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (members.containsKey(event.getSlot())) {
            User user = members.get(event.getSlot());

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
}
