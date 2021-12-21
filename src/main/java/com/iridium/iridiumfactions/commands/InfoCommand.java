package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.RelationshipType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Command which reloads all configuration files.
 */
public class InfoCommand extends Command {

    /**
     * The default constructor.
     */
    public InfoCommand() {
        super(Arrays.asList("info", "who"), "View information about a faction", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, String[] args) {
        Player player = user.getPlayer();
        if (args.length == 1) {
            sendFactionInfo(player, user.getFaction());
            return true;
        }
        Optional<Faction> faction = IridiumFactions.getInstance().getFactionManager().getFactionViaNameOrPlayer(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
        if (!faction.isPresent()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionDoesntExistByName.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        sendFactionInfo(player, faction.get());
        return true;
    }

    public void sendFactionInfo(Player player, Faction faction) {
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);
        RelationshipType relationshipType = IridiumFactions.getInstance().getFactionManager().getFactionRelationship(user, faction);
        player.sendMessage(StringUtils.color(StringUtils.getCenteredMessage(IridiumFactions.getInstance().getConfiguration().factionInfoTitle.replace("%faction%", relationshipType.getColor() + faction.getName()), IridiumFactions.getInstance().getConfiguration().factionInfoTitleFiller)));
        List<String> users = IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction).stream()
                .map(User::getName)
                .collect(Collectors.toList());
        List<String> onlineUsers = IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction).stream()
                .filter(u -> u.getPlayer() != null)
                .map(User::getName)
                .collect(Collectors.toList());
        List<String> offlineUsers = IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction).stream()
                .filter(u -> u.getPlayer() == null)
                .map(User::getName)
                .collect(Collectors.toList());
        for (String line : IridiumFactions.getInstance().getConfiguration().factionInfo) {
            player.sendMessage(StringUtils.color(line
                    .replace("%faction_description%", faction.getDescription())
                    .replace("%faction_total_power%", String.valueOf(faction.getTotalPower()))
                    .replace("%faction_remaining_power%", String.valueOf(faction.getRemainingPower()))
                    .replace("%faction_land%", String.valueOf(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction).size()))
                    .replace("%faction_members_online%", String.join(", ", onlineUsers))
                    .replace("%faction_members_offline%", String.join(", ", offlineUsers))
                    .replace("%faction_members_online_count%", String.valueOf(onlineUsers.size()))
                    .replace("%faction_members_offline_count%", String.valueOf(offlineUsers.size()))
                    .replace("%faction_members_count%", String.valueOf(users.size()))
                    .replace("%faction_value%", String.valueOf(faction.getValue()))
                    .replace("%faction_rank%", String.valueOf(faction.getRank()))
            ));
        }
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
