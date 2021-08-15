package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Command which reloads all configuration files.
 */
public class CreateCommand extends Command {

    /**
     * The default constructor.
     */
    public CreateCommand() {
        super(Collections.singletonList("create"), "Create a new faction", "%prefix% &7/f create <name>", "", true, Duration.ZERO);
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
        if (user.getFaction().isPresent()) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().alreadyHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        String factionName = args[1];
        if (IridiumFactions.getInstance().getFactionManager().getFactionViaName(factionName).isPresent()) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionNameAlreadyExists.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        IridiumFactions.getInstance().getFactionManager().createFaction(player, factionName).thenAccept(faction -> {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionCreated.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
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
        // We currently don't want to tab-completion here
        // Return a new List so it isn't a list of online players
        return Collections.emptyList();
    }

}