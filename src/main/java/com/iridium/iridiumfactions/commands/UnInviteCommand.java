package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
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
        super(Collections.singletonList("uninvite"), "Uninvite a user from your faction", "%prefix% &7/f uninvite <player>", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();
        if (args.length != 2) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        Player revokee = Bukkit.getServer().getPlayer(args[1]);
        if (revokee == null) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().notAPlayer.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        User offlinePlayerUser = IridiumFactions.getInstance().getUserManager().getUser(revokee);
        Optional<FactionInvite> factionInvite = IridiumFactions.getInstance().getFactionManager().getFactionInvite(user.getFaction(), offlinePlayerUser);
        if (!factionInvite.isPresent()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().noActiveInvite.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
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
