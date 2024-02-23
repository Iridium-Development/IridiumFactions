package com.iridium.iridiumfactions.managers;

import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import com.iridium.iridiumteams.gui.InventoryConfigGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandManager extends com.iridium.iridiumteams.managers.CommandManager<Faction, User> {
    public CommandManager(String command) {
        super(IridiumFactions.getInstance(), "&c", command);
    }

    @Override
    public void registerCommands() {
        super.registerCommands();
        registerCommand(IridiumFactions.getInstance().getCommands().claimCommand);
        registerCommand(IridiumFactions.getInstance().getCommands().unClaimCommand);
        registerCommand(IridiumFactions.getInstance().getCommands().unClaimAllCommand);
        registerCommand(IridiumFactions.getInstance().getCommands().mapCommand);
    }

    @Override
    public void noArgsDefault(@NotNull CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            User user = IridiumFactions.getInstance().getUserManager().getUser(player);
            if (IridiumFactions.getInstance().getTeamManager().getTeamViaID(user.getTeamID()).isPresent()) {
                player.openInventory(new InventoryConfigGUI(IridiumFactions.getInstance().getInventories().factionMenu).getInventory());
                return;
            }
            if (IridiumFactions.getInstance().getConfiguration().createRequiresName) {
                Bukkit.getServer().dispatchCommand(commandSender, "f help");
                return;
            }
            Bukkit.getServer().dispatchCommand(commandSender, "f create");
        }
    }
}
