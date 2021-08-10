package com.iridium.iridiumfactions.managers.tablemanagers;

import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

/**
 * Used for handling Crud operations on a table + handling cache
 */
public class FactionTableManager extends TableManager<Faction, Integer> {

    public FactionTableManager(ConnectionSource connectionSource, boolean autoCommit) throws SQLException {
        super(connectionSource, Faction.class, autoCommit, Comparator.comparing(Faction::getId));
        sort();
    }

    /**
     * Sort the list of entries by UUID
     */
    public void sort() {
        getEntries().sort(Comparator.comparing(Faction::getId));
    }

    public Optional<Faction> getFaction(int id){
        int index = Collections.binarySearch(getEntries(), new Faction(id), Comparator.comparing(Faction::getId));
        if (index < 0) return Optional.empty();
        return Optional.of(getEntries().get(index));
    }
}
