package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionType;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.RelationshipType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.*;

/**
 * Command which reloads all configuration files.
 */
public class TruceCommand extends Command {

    /**
     * The default constructor.
     */
    public TruceCommand() {
        super(Collections.singletonList("truce"), "Set a Truce with another Faction", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction userFaction, String[] args) {
        Player player = user.getPlayer();
        Optional<Faction> faction = IridiumFactions.getInstance().getFactionManager().getFactionViaNameOrPlayer(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
        if (!faction.isPresent() || faction.get().getFactionType() != FactionType.PLAYER_FACTION) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionDoesntExistByName.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (faction.get().getId() == user.getFactionID()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotRelationshipYourFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        switch (IridiumFactions.getInstance().getFactionManager().sendFactionRelationshipRequest(user, faction.get(), RelationshipType.TRUCE)) {
            case SET:
                IridiumFactions.getInstance().getFactionManager().getFactionMembers(userFaction).stream().map(User::getPlayer).filter(Objects::nonNull).forEach(p ->
                        p.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionUnAllied
                                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                                .replace("%player%", player.getName())
                                .replace("%faction%", faction.get().getName())
                        ))
                );
                IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction.get()).stream().map(User::getPlayer).filter(Objects::nonNull).forEach(p ->
                        p.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().yourFactionAlianceRevoked
                                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                                .replace("%player%", player.getName())
                                .replace("%faction%", faction.get().getName())
                        ))
                );
                return true;
            case SAME_RELATIONSHIP:
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().alreadyTruced
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%player%", player.getName())
                        .replace("%faction%", faction.get().getName())
                ));
                return false;
            case REQUEST_SENT:
                IridiumFactions.getInstance().getFactionManager().getFactionMembers(userFaction).stream().map(User::getPlayer).filter(Objects::nonNull).forEach(p ->
                        p.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().truceRequestSent
                                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                                .replace("%player%", player.getName())
                                .replace("%faction%", faction.get().getName())
                        ))
                );
                IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction.get()).stream().map(User::getPlayer).filter(Objects::nonNull).forEach(p ->
                        p.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().truceRequestReceived
                                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                                .replace("%player%", player.getName())
                                .replace("%faction%", userFaction.getName())
                        ))
                );
        }
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
