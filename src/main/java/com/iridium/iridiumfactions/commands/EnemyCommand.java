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
public class EnemyCommand extends Command {

    /**
     * The default constructor.
     */
    public EnemyCommand() {
        super(Collections.singletonList("enemy"), "Set a Faction as your enemy", "", true, Duration.ZERO);
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
        if (user.getFaction().getFactionType() != FactionType.PLAYER_FACTION) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        Optional<Faction> faction = IridiumFactions.getInstance().getFactionManager().getFactionViaNameOrPlayer(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
        if (!faction.isPresent() || faction.get().getFactionType() != FactionType.PLAYER_FACTION) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionDoesntExistByName.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (faction.get().getId() == user.getFactionID()) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotRelationshipYourFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        switch (IridiumFactions.getInstance().getFactionManager().sendFactionRelationshipRequest(user, faction.get(), RelationshipType.ENEMY)) {
            case SAME_RELATIONSHIP:
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().alreadyEnemies
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%player%", player.getName())
                        .replace("%faction%", faction.get().getName())
                ));
                return false;
            case SET:
                IridiumFactions.getInstance().getFactionManager().getFactionMembers(userFaction).stream().map(User::getPlayer).filter(Objects::nonNull).forEach(p ->
                        p.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionEnemied
                                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                                .replace("%player%", player.getName())
                                .replace("%faction%", faction.get().getName())
                        ))
                );
                IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction.get()).stream().map(User::getPlayer).filter(Objects::nonNull).forEach(p ->
                        p.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().yourFactionEnemied
                                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                                .replace("%player%", player.getName())
                                .replace("%faction%", faction.get().getName())
                        ))
                );
                return true;
        }
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
