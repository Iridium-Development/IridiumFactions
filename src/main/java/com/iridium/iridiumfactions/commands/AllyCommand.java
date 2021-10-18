package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
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
import java.util.Optional;

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
        if (!user.getFaction().isPresent()) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        Optional<Faction> faction = getFaction(player, args);
        if(!faction.isPresent()){
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionDoesntExistByName.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        IridiumFactions.getInstance().getFactionManager().setFactionRelationship(user.getFaction().get(), faction.get(), RelationshipType.ALLY);
        return false;
    }

    public Optional<Faction> getFaction(Player player, String[] args) {
        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer != null) {
            User factionUser = IridiumFactions.getInstance().getUserManager().getUser(targetPlayer);
            Optional<Faction> factionByPlayer = factionUser.getFaction();
            if (factionByPlayer.isPresent()) {
                return factionByPlayer;
            }
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().playerNoFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return Optional.empty();
        }
        String factionName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        return IridiumFactions.getInstance().getFactionManager().getFactionViaName(factionName);
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
