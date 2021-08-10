package com.iridium.iridiumfactions.managers;

import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;

import java.util.Optional;

public class FactionManager {

    public Optional<Faction> getFactionById(int id) {
        return IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().getFaction(id);
    }

}
