package com.iridium.iridiumfactions.managers.tablemanagers;

import com.iridium.iridiumfactions.database.User;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.*;

/**
 * Used for handling Crud operations on a table + handling cache
 */
public class UserTableManager extends TableManager<User, Integer> {

    public UserTableManager(ConnectionSource connectionSource, boolean autoCommit) throws SQLException {
        super(connectionSource, User.class, autoCommit);
        sort();
    }

    /**
     * Sort the list of entries by UUID
     */
    public void sort() {
        getEntries().sort(Comparator.comparing(User::getUuid));
    }

    /**
     * Add an item to the list whilst maintaining sorted list
     *
     * @param user The item we are adding
     */
    public void addEntry(User user) {
        int index = Collections.binarySearch(getEntries(), user, Comparator.comparing(User::getUuid));
        getEntries().add(index < 0 ? -(index + 1) : index, user);
    }

    public Optional<User> getUser(UUID uuid) {
        int index = Collections.binarySearch(getEntries(), new User(uuid, ""), Comparator.comparing(User::getUuid));
        if (index < 0) return Optional.empty();
        return Optional.of(getEntries().get(index));
    }

    @Override
    public void clear() {
        super.clear();
    }
}
