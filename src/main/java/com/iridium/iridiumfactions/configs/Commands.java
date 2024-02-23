package com.iridium.iridiumfactions.configs;

import com.iridium.iridiumfactions.commands.ClaimCommand;
import com.iridium.iridiumfactions.commands.MapCommand;
import com.iridium.iridiumfactions.commands.UnclaimCommand;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;

public class Commands extends com.iridium.iridiumteams.configs.Commands<Faction, User> {
    public Commands() {
        super("iridiumfactions", "Faction", "f");
    }

    public ClaimCommand claimCommand = new ClaimCommand();
    public UnclaimCommand unclaimCommand = new UnclaimCommand();
    public MapCommand mapCommand = new MapCommand();
}
