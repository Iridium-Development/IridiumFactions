package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionType;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.RelationshipType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Command which reloads all configuration files.
 */
public class AllyCommand extends Command {

    /**
     * The default constructor.
     */
    public AllyCommand() {
        super(Collections.singletonList("ally"), "Ally with another Faction", "", true, Duration.ZERO);
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
        Faction userFaction = user.getFaction();
        if (userFaction.getFactionType() != FactionType.PLAYER_FACTION) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        Faction faction = getFaction(args);
        if (faction.getFactionType() != FactionType.PLAYER_FACTION) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionDoesntExistByName.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (faction.getId() == user.getFactionID()) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotRelationshipYourFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }

        switch (IridiumFactions.getInstance().getFactionManager().sendFactionRelationshipRequest(user, faction, RelationshipType.ALLY)) {
            case SAME_RELATIONSHIP:
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().alreadyAllied
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%player%", player.getName())
                        .replace("%faction%", faction.getName())
                ));
                return false;
            case REQUEST_SENT:
                IridiumFactions.getInstance().getFactionManager().getFactionMembers(userFaction).stream().map(User::getPlayer).filter(Objects::nonNull).forEach(p ->
                        p.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().allianceRequestSent
                                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                                .replace("%player%", player.getName())
                                .replace("%faction%", faction.getName())
                        ))
                );
                IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction).stream().map(User::getPlayer).filter(Objects::nonNull).forEach(p ->
                        p.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().allianceRequestReceived
                                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                                .replace("%player%", player.getName())
                                .replace("%faction%", userFaction.getName())
                        ))
                );
                return true;
        }
        return false;
    }


    public Faction getFaction(String[] args) {
        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer != null) {
            return IridiumFactions.getInstance().getUserManager().getUser(targetPlayer).getFaction();
        }
        String factionName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        return IridiumFactions.getInstance().getFactionManager().getFactionViaName(factionName).orElse(IridiumFactions.getInstance().getFactionManager().getFactionViaId(-1));
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
