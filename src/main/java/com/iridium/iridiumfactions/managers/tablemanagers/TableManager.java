package com.iridium.iridiumfactions.managers.tablemanagers;

import com.iridium.iridiumcore.utils.SortedList;
import com.iridium.iridiumfactions.IridiumFactions;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Used for handling Crud operations on a table + handling cache
 *
 * @param <T> The Table Class
 * @param <S> The Table Primary Id Class
 */
public class TableManager<T, S> {
    private final SortedList<T> entries;
    private Dao<T, S> dao;
    private final Class<T> clazz;

    private final ConnectionSource connectionSource;

    public TableManager(ConnectionSource connectionSource, Class<T> clazz, Comparator<T> comparator) throws SQLException {
        this.connectionSource = connectionSource;
        this.entries = new SortedList<>(comparator);
        if (!IridiumFactions.getInstance().isTesting()) {
            TableUtils.createTableIfNotExists(connectionSource, clazz);
            this.dao = DaoManager.createDao(connectionSource, clazz);
            this.dao.setAutoCommit(getDatabaseConnection(), false);
            this.entries.addAll(dao.queryForAll());
        }
        this.clazz = clazz;
    }

    /**
     * A TableManager used for UnitTesting
     *
     * @param clazz      The class
     * @param comparator The comparator
     */
    public TableManager(Class<T> clazz, Comparator<T> comparator) {
        this.connectionSource = null;
        this.entries = new SortedList<>(comparator);
        this.clazz = clazz;
    }

    /**
     * Saves everything to the Database
     */
    public void save() {
        if (IridiumFactions.getInstance().isTesting()) return;
        try {
            List<T> entryList = new ArrayList<>(entries);
            for (T t : entryList) {
                dao.createOrUpdate(t);
            }
            dao.commit(getDatabaseConnection());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Adds an entry to list
     *
     * @param t the item we are adding
     */
    public void addEntry(T t) {
        entries.add(t);
    }

    /**
     * Gets all T's from cache
     *
     * @return The list of all T's
     */
    public List<T> getEntries() {
        return entries;
    }

    /**
     * Delete T from the database
     *
     * @param t the variable we are deleting
     */
    public CompletableFuture<Void> delete(T t) {
        entries.remove(t);
        return CompletableFuture.runAsync(() -> {
            if (IridiumFactions.getInstance().isTesting()) return;
            try {
                dao.delete(t);
                dao.commit(getDatabaseConnection());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    /**
     * Delete all t's in the database
     *
     * @param t The collection of variables we are deleting
     */
    public CompletableFuture<Void> delete(Collection<T> t) {
        entries.removeAll(t);
        return CompletableFuture.runAsync(() -> {
            if (IridiumFactions.getInstance().isTesting()) return;
            try {
                dao.delete(t);
                dao.commit(getDatabaseConnection());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    /**
     * Returns a connection from the connection source.
     *
     * @return The connection to the database for operations
     * @throws SQLException If there is an error with the exception
     */
    private DatabaseConnection getDatabaseConnection() throws SQLException {
        return connectionSource.getReadWriteConnection(null);
    }

    /**
     * Clear all entries in the database & cache
     */
    public void clear() {
        try {
            TableUtils.clearTable(connectionSource, clazz);
            dao.commit(getDatabaseConnection());
            entries.clear();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Returns the Dao for this class
     *
     * @return The dao
     */
    public Dao<T, S> getDao() {
        return dao;
    }
}
