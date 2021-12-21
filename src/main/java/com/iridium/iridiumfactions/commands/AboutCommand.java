package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import org.bukkit.command.CommandSender;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Command which display plugin information to the user.
 */
public class AboutCommand extends Command {

    /**
     * The default constructor.
     */
    public AboutCommand() {
        super(Arrays.asList("about", "version"), "Display plugin info", "", Duration.ZERO);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage(StringUtils.color("&7Plugin Name: &cIridiumFactions"));
        sender.sendMessage(StringUtils.color("&7Plugin Version: &c" + IridiumFactions.getInstance().getDescription().getVersion()));
        sender.sendMessage(StringUtils.color("&7Plugin Author: &cPeaches_MLG"));
        sender.sendMessage(StringUtils.color("&7Plugin Donations: &cwww.patreon.com/Peaches_MLG"));

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
