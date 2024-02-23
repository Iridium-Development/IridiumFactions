package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import com.iridium.iridiumteams.IridiumTeams;
import com.iridium.iridiumteams.commands.Command;
import org.bukkit.entity.Player;

import java.util.Collections;

public class UnclaimCommand extends Command<Faction, User> {

    public UnclaimCommand() {
        super(Collections.singletonList("unclaim"), "Unclaim land for your faction", "%prefix% &7/f unclaim (size)", "", 0);
    }

    @Override
    public boolean execute(User user, String[] args, IridiumTeams<Faction, User> iridiumTeams) {
        Player player = user.getPlayer();
        if (args.length == 0) {
            IridiumFactions.getInstance().getFactionManager().unClaimFactionLand(player.getLocation().getChunk(), player);
            return true;
        }
        try {
            int radius = Integer.parseInt(args[0]);
            IridiumFactions.getInstance().getFactionManager().unClaimFactionLand(player.getLocation().getChunk(), radius, player);
            return true;
        } catch (NumberFormatException exception) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().notANumber.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        }
        return false;
    }

}
