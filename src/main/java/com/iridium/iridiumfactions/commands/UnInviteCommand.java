package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.FactionInvite;
import com.iridium.iridiumfactions.database.User;
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
public class UnInviteCommand extends Command {

    /**
     * The default constructor.
     */
    public UnInviteCommand() {
        super(Collections.singletonList("uninvite"), "Uninvite a user from your faction", "%prefix% &7/f uninvite <player>", "", true, Duration.ZERO);
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
        if (args.length != 2) {
            sender.sendMessage(StringUtils.color(syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        Player player = (Player) sender;
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);
        if (!user.getFaction().isPresent()) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        Player revokee = Bukkit.getServer().getPlayer(args[1]);
        if (revokee == null) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().notAPlayer.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        User offlinePlayerUser = IridiumFactions.getInstance().getUserManager().getUser(revokee);
        Optional<FactionInvite> factionInvite = IridiumFactions.getInstance().getFactionManager().getFactionInvite(user.getFaction().get(), offlinePlayerUser);
        if (!factionInvite.isPresent()) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().noActiveInvite.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }

        IridiumFactions.getInstance().getDatabaseManager().getFactionInviteTableManager().delete(factionInvite.get());
        player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionInviteRevoked
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", offlinePlayerUser.getName())
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
