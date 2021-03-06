package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionType;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Arrays;
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
        super(Collections.singletonList("create"), "Create a new faction", "%prefix% &7/f create <name>", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, String[] args) {
        Player player = user.getPlayer();
        if (args.length < 2) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (user.getFaction().getFactionType() == FactionType.PLAYER_FACTION) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().alreadyHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }

        String factionName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (IridiumFactions.getInstance().getFactionManager().getFactionViaName(factionName).isPresent()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionNameAlreadyExists.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        IridiumFactions.getInstance().getFactionManager().createFaction(player, factionName).thenAccept(faction ->
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionCreated.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)))
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
        // We currently don't want to tab-completion here
        // Return a new List so it isn't a list of online players
        return Collections.emptyList();
    }

}
