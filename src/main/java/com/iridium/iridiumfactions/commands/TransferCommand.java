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

/**
 * Command which reloads all configuration files.
 */
public class TransferCommand extends Command {

    /**
     * The default constructor.
     */
    public TransferCommand() {
        super(Collections.singletonList("transfer"), "Transfer Faction ownership", "%prefix% &7Transfer faction ownership to someone else", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();
        if (args.length != 2) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
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
        if (faction.getId() != targetUser.getFactionID()) {
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
            IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction).forEach(user1 -> {
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
