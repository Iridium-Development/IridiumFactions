package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.PermissionType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Command which reloads all configuration files.
 */
public class KickCommand extends Command {

    /**
     * The default constructor.
     */
    public KickCommand() {
        super(Collections.singletonList("kick"), "Kick a user from your faction", "%prefix% &7/f kick <player>", "", true, Duration.ZERO);
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
        Optional<Faction> faction = user.getFaction();
        if (!faction.isPresent()) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (!IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction.get(), user, PermissionType.KICK)) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotKick
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
            ));
            return false;
        }
        Player kickedPlayer = Bukkit.getServer().getPlayer(args[1]);
        if (kickedPlayer == null) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().notAPlayer.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        User offlinePlayerUser = IridiumFactions.getInstance().getUserManager().getUser(kickedPlayer);
        if (faction.get().getId() != offlinePlayerUser.getFactionID()) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().userNotInFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (offlinePlayerUser.getFactionRank() == FactionRank.OWNER) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotKickOwner.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        offlinePlayerUser.setFaction(null);
        kickedPlayer.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().youHaveBeenKicked
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", player.getName())
        ));
        IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction.get()).stream().map(User::getPlayer).filter(Objects::nonNull).forEach(player1 -> {
            player1.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().playerKicked
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                    .replace("%player%", kickedPlayer.getName())
                    .replace("%kicker%", player.getName())
            ));
        });
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
