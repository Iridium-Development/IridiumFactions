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
 * Command which reloads all configuration files.
 */
public class UnClaimCommand extends Command {

    /**
     * The default constructor.
     */
    public UnClaimCommand() {
        super(Collections.singletonList("unclaim"), "Un-Claim land for your faction", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();
        if (args.length == 1) {
            IridiumFactions.getInstance().getFactionManager().unClaimFactionLand(faction, player.getLocation().getChunk(), player);
            return true;
        }
        try {
            int radius = Integer.parseInt(args[1]);
            IridiumFactions.getInstance().getFactionManager().unClaimFactionLand(faction, player.getLocation().getChunk(), radius, player);
            return true;
        } catch (NumberFormatException exception) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().notANumber.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        }
        return false;
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
