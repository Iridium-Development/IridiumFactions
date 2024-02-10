package com.iridium.iridiumfactions.managers.tablemanagers;

import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.managers.DatabaseKey;
import com.iridium.iridiumteams.database.TeamData;
import com.j256.ormlite.support.ConnectionSource;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public class ForeignFactionTableManager<Key, Value extends TeamData> extends TableManager<Key, Value, Integer> {

    public ForeignFactionTableManager(DatabaseKey<Key, Value> databaseKey, ConnectionSource connectionSource, Class<Value> clazz) throws SQLException {
        super(databaseKey, connectionSource, clazz);
    }

    public List<Value> getEntries(@NotNull Faction faction) {
        return super.getEntries(value -> value.getTeamID() == faction.getId());
    }
}
