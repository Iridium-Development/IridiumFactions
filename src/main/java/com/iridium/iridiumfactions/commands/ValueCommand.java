package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Command which shows the Island's value.
 */
public class ValueCommand extends Command {

    /**
     * The default constructor.
     */
    public ValueCommand() {
        super(Collections.singletonList("value"), "View your Faction value", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();
        player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().islandValue
                .replace("%rank%", String.valueOf(faction.getRank()))
                .replace("%value%", String.valueOf(faction.getValue()))
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
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
        // We currently don't want to tab-completion here
        // Return a new List, so it isn't a list of online players
        return Collections.emptyList();
    }

}
