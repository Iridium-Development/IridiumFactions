package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.BoosterType;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionBooster;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * Command which reloads all configuration files.
 */
public class FlyCommand extends Command {

    /**
     * The default constructor.
     */
    public FlyCommand() {
        super(Arrays.asList("fly", "flight"), "Toggle Faction Flight", "%prefix% &7/f fly", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();

        boolean flight = !user.isFlying();
        if (args.length == 2) {
            if (!args[1].equalsIgnoreCase("enable") && !args[1].equalsIgnoreCase("disable") && !args[1].equalsIgnoreCase("on") && !args[1].equalsIgnoreCase("off")) {
                player.sendMessage(StringUtils.color(syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
                return false;
            }

            flight = args[1].equalsIgnoreCase("enable") || args[1].equalsIgnoreCase("on");
        }

        FactionBooster factionBooster = IridiumFactions.getInstance().getFactionManager().getFactionBooster(faction, BoosterType.FLIGHT_BOOSTER);
        if (!factionBooster.isActive() && !player.hasPermission("iridiumfactions.fly")) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().flightBoosterNotActive.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }

        user.setFlying(flight);
        player.setAllowFlight(flight);
        player.setFlying(flight);

        if (flight) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().flightEnabled.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        } else {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().flightDisabled.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
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
        return Arrays.asList("enable", "disable", "on", "off");
    }

}
