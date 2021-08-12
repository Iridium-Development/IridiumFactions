package com.iridium.iridiumfactions.managers.tablemanagers;

import com.iridium.iridiumcore.utils.SortedList;
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
    private final SortedList<Faction> factionNameEntries;

    public FactionTableManager(ConnectionSource connectionSource, boolean autoCommit) throws SQLException {
        super(connectionSource, Faction.class, autoCommit, Comparator.comparing(Faction::getId));
        this.factionNameEntries = new SortedList<>(Comparator.comparing(Faction::getName));
        this.factionNameEntries.addAll(getDao().queryForAll());
        sort();
    }

    @Override
    public void addEntry(Faction faction) {
        super.addEntry(faction);
    }

    /**
     * Sort the list of entries by UUID
     */
    public void sort() {
        getEntries().sort(Comparator.comparing(Faction::getId));
        factionNameEntries.sort(Comparator.comparing(Faction::getName));
    }

    public Optional<Faction> getFaction(int id) {
        int index = Collections.binarySearch(getEntries(), new Faction(id), Comparator.comparing(Faction::getId));
        if (index < 0) return Optional.empty();
        return Optional.of(getEntries().get(index));
    }

    public Optional<Faction> getFaction(String name) {
        int index = Collections.binarySearch(factionNameEntries, new Faction(name), Comparator.comparing(Faction::getName));
        if (index < 0) return Optional.empty();
        return Optional.of(factionNameEntries.get(index));
    }
}
