package com.iridium.iridiumfactions.configs;

import com.iridium.iridiumfactions.commands.ClaimCommand;
import com.iridium.iridiumfactions.commands.MapCommand;
import com.iridium.iridiumfactions.commands.UnClaimAllCommand;
import com.iridium.iridiumfactions.commands.UnClaimCommand;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;

public class Commands extends com.iridium.iridiumteams.configs.Commands<Faction, User> {
    public Commands() {
        super("iridiumfactions", "Faction", "f");
    }

    public ClaimCommand claimCommand = new ClaimCommand();
    public UnClaimCommand unClaimCommand = new UnClaimCommand();
    public UnClaimAllCommand unClaimAllCommand = new UnClaimAllCommand();
    public MapCommand mapCommand = new MapCommand();
}
