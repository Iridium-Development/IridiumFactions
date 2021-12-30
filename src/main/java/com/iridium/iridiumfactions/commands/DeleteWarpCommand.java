package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.PermissionType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionWarp;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Command which reloads all configuration files.
 */
public class DeleteWarpCommand extends Command {

    /**
     * The default constructor.
     */
    public DeleteWarpCommand() {
        super(Arrays.asList("delwarp", "deletewarp"), "Delete a Faction warp", "%prefix% &7/f deletewarp <name>", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();
        if (args.length != 2) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        String name = args[1];
        if (!IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, user, PermissionType.MANAGE_WARPS)) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotDeleteWarp
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
            ));
            return false;
        }

        Optional<FactionWarp> factionWarp = IridiumFactions.getInstance().getFactionManager().getFactionWarp(faction, name);

        if (!factionWarp.isPresent()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().unknownWarp
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
            ));
            return false;
        }

        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().delete(factionWarp.get());

        player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().deletingWarp
                .replace("%name%", factionWarp.get().getName())
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix))
        );
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
        Player player = (Player) commandSender;
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);
        Faction faction = user.getFaction();
        return IridiumFactions.getInstance().getFactionManager().getFactionWarps(faction).stream()
                .map(FactionWarp::getName)
                .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
    }

}
