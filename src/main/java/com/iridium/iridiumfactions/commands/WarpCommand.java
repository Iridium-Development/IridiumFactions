package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionWarp;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Command which reloads all configuration files.
 */
public class WarpCommand extends Command {

    /**
     * The default constructor.
     */
    public WarpCommand() {
        super(Collections.singletonList("warp"), "Teleport to a faction warp", "%prefix% &7/f warp <name> (password)", "", Duration.ZERO);
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
        Optional<FactionWarp> factionWarp = IridiumFactions.getInstance().getFactionManager().getFactionWarp(faction, name);
        if (!factionWarp.isPresent()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().unknownWarp.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        if (IridiumFactions.getInstance().getFactionManager().getFactionViaLocation(factionWarp.get().getLocation()) != faction) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().notInFactionLand.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }

        if (factionWarp.get().getPassword() != null) {
            if (!factionWarp.get().getPassword().equals(password)) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().incorrectPassword.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
                return false;
            }
        }

        player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().teleportingWarp
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%name%", factionWarp.get().getName())
        ));

        player.setFallDistance(0);
        player.teleport(factionWarp.get().getLocation());

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
