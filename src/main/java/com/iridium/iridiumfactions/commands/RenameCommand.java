package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
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
        super(Arrays.asList("rename", "name", "tag"), "change your faction name", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();
        if (!IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, user, PermissionType.RENAME)) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotChangeName
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
            ));
            return false;
        }
        String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        Optional<Faction> factionWithName = IridiumFactions.getInstance().getFactionManager().getFactionViaName(name);
        if (factionWithName.isPresent() && factionWithName.get().getId() != faction.getId()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionNameAlreadyExists.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (name.length() < IridiumFactions.getInstance().getConfiguration().minFactionNameLength) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionNameTooShort
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                    .replace("%min_length%", String.valueOf(IridiumFactions.getInstance().getConfiguration().minFactionNameLength))
            ));
            return false;
        }
        if (name.length() > IridiumFactions.getInstance().getConfiguration().maxFactionNameLength) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionNameTooLong
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
