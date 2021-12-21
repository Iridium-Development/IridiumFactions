package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.FactionType;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionInvite;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Command which reloads all configuration files.
 */
public class JoinCommand extends Command {

    /**
     * The default constructor.
     */
    public JoinCommand() {
        super(Collections.singletonList("join"), "Join a faction", "%prefix% &7/f join <player>", "", Duration.ZERO);
    }
    @Override
    public boolean execute(User user, String[] args) {
        Player player = user.getPlayer();
        if (args.length != 2) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (user.getFaction().getFactionType() == FactionType.PLAYER_FACTION) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().alreadyHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        Optional<Faction> faction = IridiumFactions.getInstance().getFactionManager().getFactionViaNameOrPlayer(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
        if (!faction.isPresent()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionDoesntExistByName.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }

        Optional<FactionInvite> factionInvite = IridiumFactions.getInstance().getFactionManager().getFactionInvite(faction.get(), user);
        if (!factionInvite.isPresent() && !user.isBypassing()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().noInvite.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }

        user.setFaction(faction.get());
        user.setFactionRank(FactionRank.MEMBER);

        factionInvite.ifPresent(invite -> IridiumFactions.getInstance().getDatabaseManager().getFactionInviteTableManager().delete(invite));

        player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().joinedFaction
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%name%", faction.get().getName())
        ));

        IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction.get()).forEach(factionUser -> {
            Player factionPlayer = Bukkit.getPlayer(factionUser.getUuid());
            if (factionPlayer != null && factionPlayer != player) {
                factionPlayer.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().userJoinedFaction
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%name%", user.getFaction().getName())
                        .replace("%player%", player.getName())
                ));
            }
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
