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

/**
 * Command which reloads all configuration files.
 */
public class HomeCommand extends Command {

    /**
     * The default constructor.
     */
    public HomeCommand() {
        super(Collections.singletonList("home"), "Teleport to your Faction home", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();
        Location home = faction.getHome();
        if (home == null) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().homeNotSet.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(home).getId() != faction.getId()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().homeNotInFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        player.teleport(home);
        player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().teleportingFactionHome.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
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
