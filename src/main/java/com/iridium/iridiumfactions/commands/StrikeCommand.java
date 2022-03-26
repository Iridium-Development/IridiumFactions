package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionStrike;
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
 * Command which allows admins to bypass Factions restrictions.
 */
public class StrikeCommand extends Command {

    /**
     * The default constructor.
     */
    public StrikeCommand() {
        super(Collections.singletonList("strike"), "Issue a faction strike", "%prefix% &7/f strike <faction> <reason>", "iridiumfactions.strike", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, String[] args) {
        Player player = user.getPlayer();
        if (args.length < 3) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        Optional<Faction> faction = IridiumFactions.getInstance().getFactionManager().getFactionViaNameOrPlayer(String.join(" ", args[1]));
        if (!faction.isPresent()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionDoesntExistByName.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        IridiumFactions.getInstance().getDatabaseManager().getFactionStrikeTableManager().addEntry(new FactionStrike(faction.get(), message, user));

        player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().issuedFactionStrike
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%faction%", faction.get().getName())
                .replace("%reason%", message)
        ));

        IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction.get()).forEach(factionUser -> {
            Player factionPlayer = Bukkit.getPlayer(factionUser.getUuid());
            if (factionPlayer != null && factionPlayer != player) {
                factionPlayer.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionStrikeIssued
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%player%", player.getName())
                        .replace("%reason%", message)
                ));
            }
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
        // Return a new List, so it isn't a list of online players
        return Collections.emptyList();
    }

}
