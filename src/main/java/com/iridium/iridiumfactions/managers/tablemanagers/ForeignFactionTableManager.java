package com.iridium.iridiumfactions.managers.tablemanagers;

import com.iridium.iridiumcore.utils.SortedList;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionData;
import com.j256.ormlite.support.ConnectionSource;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.*;

public class ForeignFactionTableManager<T extends FactionData, S> extends TableManager<T, S> {

    private final Comparator<T> comparator;
    private final SortedList<T> factionsSortedList;

    public ForeignFactionTableManager(ConnectionSource connectionSource, Class<T> clazz, Comparator<T> comparator) throws SQLException {
        super(connectionSource, clazz, comparator);
        this.comparator = comparator;
        this.factionsSortedList = new SortedList<>(Comparator.comparing(FactionData::getFactionID));
        this.factionsSortedList.addAll(getEntries());
        sort();
    }

    @Override
    public void addEntry(T t) {
        super.addEntry(t);
        factionsSortedList.add(t);
    }

    public Optional<T> getEntry(T t) {
        int index = Collections.binarySearch(getEntries(), t, comparator);
        if (index < 0) return Optional.empty();
        return Optional.of(getEntries().get(index));
    }

    /**
     * Gets all entries associated with a faction
     *
     * @param faction the specified faction
     */
    public List<T> getEntries(@NotNull Faction faction) {
        int index = Collections.binarySearch(getEntries(), new FactionData(faction), Comparator.comparing(FactionData::getFactionID));
        if (index < 0) return Collections.emptyList();

        int currentIndex = index - 1;
        List<T> result = new ArrayList<>();
        result.add(getEntries().get(index));

        while (true) {
            if (currentIndex < 0) break;
            FactionData factionData = getEntries().get(currentIndex);
            if (factionData == null) {
                currentIndex--;
                continue;
            }
            if (faction.getId() == factionData.getFactionID()){
                result.add(getEntries().get(currentIndex));
                currentIndex--;
            } else {
                break;
            }
        }

        currentIndex = index + 1;

        while (true) {
            if (currentIndex >= getEntries().size()) break;
            FactionData factionData = getEntries().get(currentIndex);
            if (factionData == null) {
                currentIndex++;
                continue;
            }
            if (faction.getId() == factionData.getFactionID()){
                result.add(getEntries().get(currentIndex));
                currentIndex++;
            } else {
                break;
            }
        }
        return result;
    }

    public void sort() {
        getEntries().sort(comparator);
        factionsSortedList.sort(Comparator.comparing(FactionData::getFactionID));
    }
}
