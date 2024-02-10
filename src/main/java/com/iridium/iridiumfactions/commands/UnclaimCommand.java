package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionType;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import com.iridium.iridiumteams.IridiumTeams;
import com.iridium.iridiumteams.commands.Command;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Optional;

public class UnclaimCommand extends Command<Faction, User> {

    public UnclaimCommand() {
        super(Collections.singletonList("unclaim"), "Unclaim land for your faction", "%prefix% &7/f unclaim (size) (faction)", "", 0);
    }

    @Override
    public boolean execute(User user, String[] args, IridiumTeams<Faction, User> iridiumTeams) {
        Player player = user.getPlayer();
        Faction faction = user.getFaction();
        if (args.length == 2) {
            Optional<Faction> factionByName = IridiumFactions.getInstance().getFactionManager().getTeamViaName(args[1]);
            if (!factionByName.isPresent()) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().teamDoesntExistByName.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
                return false;
            }
            if (!user.isBypassing()) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().noPermission.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
                return false;
            }
            faction = factionByName.get();
        } else {
            if (faction.getFactionType() != FactionType.PLAYER_FACTION) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveTeam.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
                return false;
            }
        }
        if (args.length == 0) {
            IridiumFactions.getInstance().getFactionManager().unclaimFactionLand(faction, player.getLocation().getChunk(), player);
            return true;
        }
        try {
            int radius = Integer.parseInt(args[0]);
            IridiumFactions.getInstance().getFactionManager().unclaimFactionLand(faction, player.getLocation().getChunk(), radius, player);
            return true;
        } catch (NumberFormatException exception) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().notANumber.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        }
        return false;
    }

}
