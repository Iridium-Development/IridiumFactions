package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.PermissionType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionInvite;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Command which reloads all configuration files.
 */
public class InviteCommand extends Command {

    /**
     * The default constructor.
     */
    public InviteCommand() {
        super(Collections.singletonList("invite"), "Invite a user to your faction", "%prefix% &7/f invite <player>", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();
        if (args.length != 2) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (!IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, user, PermissionType.INVITE)) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotInvite
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
            ));
            return false;
        }
        Player invitee = Bukkit.getServer().getPlayer(args[1]);
        if (invitee == null) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().notAPlayer.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        User offlinePlayerUser = IridiumFactions.getInstance().getUserManager().getUser(invitee);
        if (offlinePlayerUser.getFactionID() == faction.getId()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().userAlreadyInFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (IridiumFactions.getInstance().getFactionManager().getFactionInvite(user.getFaction(), offlinePlayerUser).isPresent()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().inviteAlreadyPresent.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }

        IridiumFactions.getInstance().getDatabaseManager().getFactionInviteTableManager().addEntry(new FactionInvite(user.getFaction(), offlinePlayerUser, user));
        player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionInviteSent
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", offlinePlayerUser.getName())
        ));
        invitee.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionInviteReceived
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", player.getName())
        ));
        return true;
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
