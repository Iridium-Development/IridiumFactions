package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Command which reloads all configuration files.
 */
public class LeaveCommand extends Command {

    /**
     * The default constructor.
     */
    public LeaveCommand() {
        super(Collections.singletonList("leave"), "Leave your faction", "%prefix% &7/f leave", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();

        player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().leftFaction
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%name%", faction.getName())
        ));

        IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction).forEach(factionUser -> {
            Player factionPlayer = Bukkit.getPlayer(factionUser.getUuid());
            if (factionPlayer != null && factionPlayer != player) {
                factionPlayer.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().userLeftFaction
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%name%", faction.getName())
                        .replace("%player%", player.getName())
                ));
            }
        });

        user.setFaction(null);
        user.setFactionRank(FactionRank.TRUCE);

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
