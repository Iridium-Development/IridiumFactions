package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.RelationshipType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
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
        super(Arrays.asList("info", "who"), "View information about a faction", "", true, Duration.ZERO);
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
        if (args.length == 1) {
            if (!user.getFaction().isPresent()) {
                sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
                return false;
            }
            sendFactionInfo(player, user.getFaction().get());
            return true;
        }
        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer != null) {
            User factionUser = IridiumFactions.getInstance().getUserManager().getUser(targetPlayer);
            Optional<Faction> factionByPlayer = factionUser.getFaction();
            if (factionByPlayer.isPresent()) {
                sendFactionInfo(player, factionByPlayer.get());
                return true;
            }
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().playerNoFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        String factionName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        Optional<Faction> factionByName = IridiumFactions.getInstance().getFactionManager().getFactionViaName(factionName);
        if (factionByName.isPresent()) {
            sendFactionInfo(player, factionByName.get());
            return true;
        }
        sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionDoesntExistByName.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        return false;
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
        faction.getValue().thenAccept(value -> faction.getRank().thenAccept(rank -> {
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
                        .replace("%faction_value%", String.valueOf(value))
                        .replace("%faction_rank%", String.valueOf(rank))
                ));
            }
        }));
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
