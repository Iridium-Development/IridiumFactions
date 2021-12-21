package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
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

/**
 * Command which reloads all configuration files.
 */
public class KickCommand extends Command {

    /**
     * The default constructor.
     */
    public KickCommand() {
        super(Collections.singletonList("kick"), "Kick a user from your faction", "%prefix% &7/f kick <player>", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();
        if (args.length != 2) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (!IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, user, PermissionType.KICK)) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotKick
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
            ));
            return false;
        }
        Player kickedPlayer = Bukkit.getServer().getPlayer(args[1]);
        if (kickedPlayer == null) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().notAPlayer.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        User offlinePlayerUser = IridiumFactions.getInstance().getUserManager().getUser(kickedPlayer);
        if (faction.getId() != offlinePlayerUser.getFactionID()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().userNotInFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (offlinePlayerUser.getFactionRank().getLevel() >= user.getFactionRank().getLevel()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotKickHigherRank.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        offlinePlayerUser.setFaction(null);
        kickedPlayer.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().youHaveBeenKicked
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%player%", player.getName())
        ));
        IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction).stream().map(User::getPlayer).filter(Objects::nonNull).forEach(player1 ->
                player1.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().playerKicked
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%player%", kickedPlayer.getName())
                        .replace("%kicker%", player.getName())
                ))
        );
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
