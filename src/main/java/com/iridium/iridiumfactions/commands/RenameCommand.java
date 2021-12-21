package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionType;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.PermissionType;
import com.iridium.iridiumfactions.database.Faction;
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
public class RenameCommand extends Command {

    /**
     * The default constructor.
     */
    public RenameCommand() {
        super(Arrays.asList("rename", "name", "tag"), "change your faction name", "", true, Duration.ZERO);
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
        Faction faction = user.getFaction();
        if (faction.getFactionType() != FactionType.PLAYER_FACTION) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (!IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, user, PermissionType.RENAME)) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotChangeName
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
            ));
            return false;
        }
        String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        Optional<Faction> factionWithName = IridiumFactions.getInstance().getFactionManager().getFactionViaName(name);
        if (factionWithName.isPresent() && factionWithName.get().getId() != faction.getId()) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionNameAlreadyExists.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (name.length() < IridiumFactions.getInstance().getConfiguration().minFactionNameLength) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionNameTooShort
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                    .replace("%min_length%", String.valueOf(IridiumFactions.getInstance().getConfiguration().minFactionNameLength))
            ));
            return false;
        }
        if (name.length() > IridiumFactions.getInstance().getConfiguration().maxFactionNameLength) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionNameTooLong
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                    .replace("%max_length%", String.valueOf(IridiumFactions.getInstance().getConfiguration().maxFactionNameLength))
            ));
            return false;
        }
        faction.setName(name);
        IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction).stream().map(User::getPlayer).filter(Objects::nonNull).forEach(member ->
                member.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionNameChanged
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%player%", player.getName())
                        .replace("%name%", name)
                ))
        );
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
