package com.iridium.iridiumfactions.managers.tablemanagers;

import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumteams.database.Team;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Optional;

public class FactionTableManager extends TableManager<Integer, Faction, Integer> {

    public FactionTableManager(ConnectionSource connectionSource) throws SQLException {
        super(Team::getId, connectionSource, Faction.class);
    }

    public Optional<Faction> getFaction(int id) {
        return super.getEntry(id);
    }

    public Optional<Faction> getFaction(String name) {
        return super.getEntry(faction -> faction.getName().equalsIgnoreCase(name));
    }
}
