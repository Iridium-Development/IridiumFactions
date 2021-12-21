package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.PermissionType;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class PromoteCommand extends Command {

    /**
     * The default constructor.
     */
    public PromoteCommand() {
        super(Collections.singletonList("promote"), "Promote a user in your Faction", "%prefix% &7/f promote <player>", "", Duration.ZERO);
    }

    @Override
    public boolean execute(User user, Faction faction, String[] args) {
        Player player = user.getPlayer();
        if (args.length != 2) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }

        OfflinePlayer targetPlayer = Bukkit.getServer().getOfflinePlayer(args[1]);
        User targetUser = IridiumFactions.getInstance().getUserManager().getUser(targetPlayer);

        if (faction.getId() != targetUser.getFactionID()) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().userNotInFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }

        FactionRank nextRank = FactionRank.getByLevel(targetUser.getFactionRank().getLevel() + 1);
        if (nextRank == null || nextRank.getLevel() >= user.getFactionRank().getLevel() || !IridiumFactions.getInstance().getFactionManager().getFactionPermission(faction, user, PermissionType.PROMOTE)) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotPromoteUser.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
            return false;
        }

        targetUser.setFactionRank(nextRank);

        for (User member : IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction)) {
            Player islandMember = Bukkit.getPlayer(member.getUuid());
            if (islandMember != null) {
                if (islandMember.equals(player)) {
                    islandMember.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().promotedPlayer
                            .replace("%player%", targetUser.getName())
                            .replace("%rank%", nextRank.getDisplayName())
                            .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                    ));
                } else {
                    islandMember.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().userPromotedPlayer
                            .replace("%promoter%", player.getName())
                            .replace("%player%", targetUser.getName())
                            .replace("%rank%", nextRank.getDisplayName())
                            .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                    ));
                }
            }
        }
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
