package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionInvite;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Command which reloads all configuration files.
 */
public class JoinCommand extends Command {

    /**
     * The default constructor.
     */
    public JoinCommand() {
        super(Collections.singletonList("join"), "Join a faction", "%prefix% &7/f join <player>", "", true, Duration.ZERO);
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
        if (args.length != 2) {
            sender.sendMessage(StringUtils.color(syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        Player player = (Player) sender;
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);
        if (user.getFaction().isPresent()) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().alreadyHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }
        Optional<Faction> faction = Optional.empty();
        Player factionOwner = Bukkit.getServer().getPlayer(args[1]);
        if (factionOwner != null) {
            faction = IridiumFactions.getInstance().getUserManager().getUser(factionOwner).getFaction();
        }
        if (!faction.isPresent()) {
            faction = IridiumFactions.getInstance().getFactionManager().getFactionViaName(args[1]);
        }
        if (!faction.isPresent()) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().noFactionExists.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }

        Optional<FactionInvite> factionInvite = IridiumFactions.getInstance().getFactionManager().getFactionInvite(faction.get(), user);
        if (!factionInvite.isPresent() && !user.isBypassing()) {
            sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().noInvite.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }

        user.setFaction(faction.get());
        user.setFactionRank(FactionRank.MEMBER);

        factionInvite.ifPresent(invite -> IridiumFactions.getInstance().getDatabaseManager().getFactionInviteTableManager().delete(invite));

        sender.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().joinedFaction
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%name%", faction.get().getName())
        ));

        IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction.get()).forEach(factionUser -> {
            Player factionPlayer = Bukkit.getPlayer(factionUser.getUuid());
            if (factionPlayer != null && factionPlayer != player) {
                factionPlayer.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().userJoinedFaction
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%name%", user.getFaction().get().getName())
                        .replace("%player%", player.getName())
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
        return null;
    }

}
