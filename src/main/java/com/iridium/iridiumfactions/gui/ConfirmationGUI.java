package com.iridium.iridiumfactions.gui;

import com.iridium.iridiumcore.gui.GUI;
import com.iridium.iridiumcore.utils.InventoryUtils;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.CooldownProvider;
import com.iridium.iridiumfactions.IridiumFactions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * GUI which executes code upon confirmation.
 */
public class ConfirmationGUI implements GUI {

    private final @NotNull Runnable runnable;
    private final @NotNull CooldownProvider<CommandSender> cooldownProvider;

    /**
     * The default constructor.
     *
     * @param runnable         The code that should be run when the user confirms his action
     * @param cooldownProvider The provider for cooldowns that should be started on success
     */
    public ConfirmationGUI(@NotNull Runnable runnable, @NotNull CooldownProvider<CommandSender> cooldownProvider) {
        this.runnable = runnable;
        this.cooldownProvider = cooldownProvider;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, IridiumFactions.getInstance().getInventories().confirmationGUI.size, StringUtils.color(IridiumFactions.getInstance().getInventories().confirmationGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        inventory.clear();
        InventoryUtils.fillInventory(inventory, IridiumFactions.getInstance().getInventories().confirmationGUI.background);

        inventory.setItem(IridiumFactions.getInstance().getInventories().confirmationGUI.no.slot, ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().confirmationGUI.no));
        inventory.setItem(IridiumFactions.getInstance().getInventories().confirmationGUI.yes.slot, ItemStackUtils.makeItem(IridiumFactions.getInstance().getInventories().confirmationGUI.yes));
    }

    /**
     * Called when there is a click in this GUI.
     * Cancelled automatically.
     *
     * @param event The InventoryClickEvent provided by Bukkit
     */
    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getSlot() == IridiumFactions.getInstance().getInventories().confirmationGUI.no.slot) {
            player.closeInventory();
        } else if (event.getSlot() == IridiumFactions.getInstance().getInventories().confirmationGUI.yes.slot) {
            runnable.run();
            player.closeInventory();
            cooldownProvider.applyCooldown(player);
        }
    }
}
