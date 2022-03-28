package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionType;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.RelationshipType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionRelationshipRequest;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Command which reloads all configuration files.
 */
public class DeclineRequestCommand extends Command {

    /**
     * The default constructor.
     */
    public DeclineRequestCommand() {
        super(Arrays.asList("declinerequest", "cancelrequest", "decline", "cancel"), "Decline or cancel a relationship request", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction userFaction, String[] args) {
        Player player = user.getPlayer();
        Optional<Faction> faction = IridiumFactions.getInstance().getFactionManager().getFactionViaNameOrPlayer(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
        if (!faction.isPresent() || faction.get().getFactionType() != FactionType.PLAYER_FACTION) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionDoesntExistByName.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        Optional<FactionRelationshipRequest> allyRequest = IridiumFactions.getInstance().getFactionManager().getFactionRelationshipRequest(faction.get(), userFaction, RelationshipType.ALLY);
        Optional<FactionRelationshipRequest> truceRequest = IridiumFactions.getInstance().getFactionManager().getFactionRelationshipRequest(faction.get(), userFaction, RelationshipType.TRUCE);
        if (!allyRequest.isPresent() && !truceRequest.isPresent()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().noRequestsPresent
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                    .replace("%faction%", faction.get().getName())
            ));
            return false;
        }

        allyRequest.ifPresent(relationshipRequest -> {
            boolean ourRequest = relationshipRequest.getFactionID() == userFaction.getId();
            String ourMessage = ourRequest ? IridiumFactions.getInstance().getMessages().requestsCanceled : IridiumFactions.getInstance().getMessages().requestDeclined;
            String theirMessage = ourRequest ? IridiumFactions.getInstance().getMessages().yourRequestsCanceled : IridiumFactions.getInstance().getMessages().yourRequestDeclined;

            IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipRequestTableManager().delete(relationshipRequest);

            IridiumFactions.getInstance().getFactionManager().getFactionMembers(userFaction).stream()
                    .map(User::getPlayer)
                    .filter(Objects::nonNull)
                    .forEach(factionMember ->
                            factionMember.sendMessage(StringUtils.color(ourMessage
                                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                                    .replace("%faction%", faction.get().getName())
                                    .replace("%player%", player.getName())
                                    .replace("%relationship%", "alliance")
                            ))
                    );
            IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction.get()).stream()
                    .map(User::getPlayer)
                    .filter(Objects::nonNull)
                    .forEach(factionMember ->
                            factionMember.sendMessage(StringUtils.color(theirMessage
                                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                                    .replace("%faction%", faction.get().getName())
                                    .replace("%player%", player.getName())
                                    .replace("%relationship%", "alliance")
                            ))
                    );
        });
        truceRequest.ifPresent(relationshipRequest -> {
            boolean ourRequest = relationshipRequest.getFactionID() == userFaction.getId();
            String ourMessage = ourRequest ? IridiumFactions.getInstance().getMessages().requestsCanceled : IridiumFactions.getInstance().getMessages().requestDeclined;
            String theirMessage = ourRequest ? IridiumFactions.getInstance().getMessages().yourRequestsCanceled : IridiumFactions.getInstance().getMessages().yourRequestDeclined;

            IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipRequestTableManager().delete(relationshipRequest);

            IridiumFactions.getInstance().getFactionManager().getFactionMembers(userFaction).stream()
                    .map(User::getPlayer)
                    .filter(Objects::nonNull)
                    .forEach(factionMember ->
                            factionMember.sendMessage(StringUtils.color(ourMessage
                                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                                    .replace("%faction%", faction.get().getName())
                                    .replace("%player%", player.getName())
                                    .replace("%relationship%", "truce")
                            ))
                    );
            IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction.get()).stream()
                    .map(User::getPlayer)
                    .filter(Objects::nonNull)
                    .forEach(factionMember ->
                            factionMember.sendMessage(StringUtils.color(theirMessage
                                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                                    .replace("%faction%", faction.get().getName())
                                    .replace("%player%", player.getName())
                                    .replace("%relationship%", "truce")
                            ))
                    );
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
        return null;
    }

}
