package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import com.iridium.iridiumfactions.gui.ConfirmationGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Command which reloads all configuration files.
 */
public class TransferCommand extends Command {

    /**
     * The default constructor.
     */
    public TransferCommand() {
        super(Collections.singletonList("transfer"), "Transfer Faction ownership", "", true, Duration.ZERO);
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
        Optional<Faction> faction = user.getFaction();
        if (!faction.isPresent()) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (!user.getFactionRank().equals(FactionRank.OWNER) && !user.isBypassing()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().mustBeOwnerToTransfer.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }

        Player targetPlayer = Bukkit.getServer().getPlayer(args[1]);
        if (targetPlayer == null) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().notAPlayer.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        User targetUser = IridiumFactions.getInstance().getUserManager().getUser(targetPlayer);
        if (faction.get().getId() != targetUser.getFactionID()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().userNotInFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (targetPlayer.getUniqueId().equals(player.getUniqueId()) && !user.isBypassing()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotTransferToYourself.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }

        player.openInventory(new ConfirmationGUI(() -> {
            user.setFactionRank(FactionRank.CO_OWNER);
            targetUser.setFactionRank(FactionRank.OWNER);
            IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction.get()).forEach(user1 -> {
                if (user1.getFactionRank() == FactionRank.OWNER && user1 != targetUser) {
                    user1.setFactionRank(FactionRank.CO_OWNER);
                }
                Player p = user1.getPlayer();
                if (p != null) {
                    p.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionOwnershipTransferred
                            .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                            .replace("%old_owner%", user.getName())
                            .replace("%new_owner%", targetUser.getName())
                    ));
                }
            });
        }, getCooldownProvider()).getInventory());

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
