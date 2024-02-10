package com.iridium.iridiumfactions.commands;

import com.iridium.iridiumfactions.FactionsMap;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import com.iridium.iridiumteams.IridiumTeams;
import com.iridium.iridiumteams.commands.Command;
import org.bukkit.entity.Player;

import java.util.Collections;

public class MapCommand extends Command<Faction, User> {

    public MapCommand() {
        super(Collections.singletonList("map"), "View your faction map", "%prefix% &7/f map", "", 0);
    }

    @Override
    public boolean execute(User user, String[] args, IridiumTeams<Faction, User> iridiumTeams) {
        Player player = user.getPlayer();
        new FactionsMap(player).sendMap();
        return true;
    }

}
