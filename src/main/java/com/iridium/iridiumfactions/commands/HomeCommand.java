package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Command which reloads all configuration files.
 */
public class HomeCommand extends Command {

    /**
     * The default constructor.
     */
    public HomeCommand() {
        super(Collections.singletonList("home"), "Teleport to your Faction home", "", true, Duration.ZERO);
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
        Optional<Faction> faction = user.getFaction();
        if (!faction.isPresent()) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        Location home = faction.get().getHome();
        if (home == null) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().homeNotSet.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(home).map(Faction::getId).orElse(0) != faction.get().getId()) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().homeNotInFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        player.teleport(home);
        sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionHomeSet.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
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
