package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.User;
import com.iridium.iridiumfactions.gui.ConfirmationGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Command which reloads all configuration files.
 */
public class DeleteCommand extends Command {

    /**
     * The default constructor.
     */
    public DeleteCommand() {
        super(Collections.singletonList("delete"), "Delete your faction", "", true, Duration.ZERO);
    }

    /**
     * Executes the command for the specified {@link CommandSender} with the provided arguments.
     * Not called when the command execution was invalid (no permission, no player or command disabled).
     * Reloads all configuration files.
     *
     * @param sender The CommandSender which executes this command
     * @param args   The arguments used with this command. They contain the sub-command
     */
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);
        if (!user.getFaction().isPresent()) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (!user.getFactionRank().equals(FactionRank.OWNER)) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotDeleteFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }

        player.openInventory(new ConfirmationGUI(() -> IridiumFactions.getInstance().getFactionManager().deleteFaction(user.getFaction().get(), user), getCooldownProvider()).getInventory());

        // Always return false because the cooldown is set by the ConfirmationGUI
        return false;
    }

    /**
     * Handles tab-completion for this command.
     *
     * @param commandSender The CommandSender which tries to tab-complete
     * @param command       The command
     * @param label         The label of the command
     * @param args          The arguments already provided by the sender
     * @return The list of tab completions for this command
     */
    @Override
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String label, String[] args) {
        return null;
    }

}