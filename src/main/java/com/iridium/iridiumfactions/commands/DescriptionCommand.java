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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Command which reloads all configuration files.
 */
public class DescriptionCommand extends Command {

    /**
     * The default constructor.
     */
    public DescriptionCommand() {
        super(Collections.singletonList("description"), "change your faction description", "%prefix% &7/f description <name>", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();
        if (args.length != 2) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (!IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, user, PermissionType.DESCRIPTION)) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotChangeDescription
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
            ));
            return false;
        }
        String description = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        faction.setDescription(description);
        IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction).stream().map(User::getPlayer).filter(Objects::nonNull).forEach(member ->
                member.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionDescriptionChanged
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%player%", player.getName())
                        .replace("%description%", description)
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
