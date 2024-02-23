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

public class UnClaimAllCommand extends Command<Faction, User> {

    public UnClaimAllCommand() {
        super(Collections.singletonList("unclaimall"), "UnClaim all your faction land", "%prefix% &7/f unclaimall (faction)", "", 0);
    }

    @Override
    public boolean execute(User user, String[] args, IridiumTeams<Faction, User> iridiumTeams) {
        Player player = user.getPlayer();
        Faction faction = user.getFaction();
        if (args.length == 1) {
            Optional<Faction> factionByName = IridiumFactions.getInstance().getFactionManager().getTeamViaName(args[0]);
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
        IridiumFactions.getInstance().getFactionManager().unClaimAllFactionLand(faction, player);
        return false;
    }

}
