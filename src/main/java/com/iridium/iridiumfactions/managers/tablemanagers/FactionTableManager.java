package com.iridium.iridiumfactions.managers.tablemanagers;

import com.iridium.iridiumcore.utils.SortedList;
import com.iridium.iridiumfactions.database.Faction;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Used for handling Crud operations on a table + handling cache
 */
public class FactionTableManager extends TableManager<Faction, Integer> {
    private final SortedList<Faction> factionNameEntries;

    public FactionTableManager(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Faction.class, Comparator.comparing(Faction::getId));
        this.factionNameEntries = new SortedList<>(Comparator.comparing(Faction::getName, String.CASE_INSENSITIVE_ORDER));
        this.factionNameEntries.addAll(getEntries());
        sort();
    }

    @Override
    public void addEntry(Faction faction) {
        super.addEntry(faction);
        factionNameEntries.add(faction);
    }

    @Override
    public CompletableFuture<Void> delete(Faction faction) {
        factionNameEntries.remove(faction);
        return super.delete(faction);
    }

    @Override
    public CompletableFuture<Void> delete(Collection<Faction> factions) {
        factionNameEntries.removeAll(factions);
        return super.delete(factions);
    }

    /**
     * Sort the list of entries by UUID
     */
    public void sort() {
        getEntries().sort(Comparator.comparing(Faction::getId));
        factionNameEntries.sort(Comparator.comparing(Faction::getName, String.CASE_INSENSITIVE_ORDER));
    }

    public Optional<Faction> getFaction(int id) {
        int index = Collections.binarySearch(getEntries(), new Faction(id), Comparator.comparing(Faction::getId));
        if (index < 0) return Optional.empty();
        return Optional.of(getEntries().get(index));
    }

    public Optional<Faction> getFaction(String name) {
        int index = Collections.binarySearch(factionNameEntries, new Faction(name), Comparator.comparing(Faction::getName, String.CASE_INSENSITIVE_ORDER));
        if (index < 0) return Optional.empty();
        return Optional.of(factionNameEntries.get(index));
    }
}
