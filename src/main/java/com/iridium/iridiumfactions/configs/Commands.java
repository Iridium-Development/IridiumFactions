package com.iridium.iridiumfactions.configs;

import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;

public class Commands extends com.iridium.iridiumteams.configs.Commands<Faction, User> {
    public Commands() {
        super("iridiumfactions", "Faction", "f");
    }
}
