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
import java.util.Collections;
import java.util.List;

/**
 * Command which reloads all configuration files.
 */
public class SetWarpCommand extends Command {

    /**
     * The default constructor.
     */
    public SetWarpCommand() {
        super(Arrays.asList("setwarp", "createwarp"), "Creates a Faction warp", "%prefix% &7/f setwarp <name> (password)", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();
        if (args.length < 2) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        String name = args[1];
        String password = args.length > 2 ? args[2] : null;
        if (IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(player.getLocation()).getId() != faction.getId()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().notInFactionLand.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (!IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, user, PermissionType.SETWARP)) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotSetWarp
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
            ));
            return false;
        }

        if (IridiumFactions.getInstance().getFactionManager().getFactionWarp(faction, name).isPresent()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().warpAlreadyExists
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
            ));
            return false;
        }

        IridiumFactions.getInstance().getDatabaseManager().getFactionWarpTableManager().addEntry(new FactionWarp(faction, player.getLocation(), name, password));

        player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionWarpSet
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
        ));
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
        // Return a new List so it isn't a list of online players
        return Collections.emptyList();
    }

}
