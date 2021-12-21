package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumfactions.FactionsMap;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Command which reloads all configuration files.
 */
public class MapCommand extends Command {

    /**
     * The default constructor.
     */
    public MapCommand() {
        super(Collections.singletonList("map"), "view your faction map", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, String[] args) {
        Player player = user.getPlayer();
        new FactionsMap(player).sendMap();
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
