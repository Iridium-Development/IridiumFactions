package com.iridium.iridiumfactions.managers.tablemanagers;

import com.iridium.iridiumfactions.database.FactionData;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

public class ForeignFactionTableManager<T extends FactionData, S> extends TableManager<T, S> {

    private final Comparator<T> comparator;

    public ForeignFactionTableManager(ConnectionSource connectionSource, Class<T> clazz, boolean autoCommit, Comparator<T> comparator) throws SQLException {
        super(connectionSource, clazz, autoCommit, comparator);
        this.comparator = comparator;
        sort();
    }

    public Optional<T> getEntry(T t) {
        int index = Collections.binarySearch(getEntries(), t, comparator);
        if (index < 0) return Optional.empty();
        return Optional.of(getEntries().get(index));
    }

    public void sort() {
        getEntries().sort(comparator);
    }
}
