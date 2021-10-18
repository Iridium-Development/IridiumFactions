package com.iridium.iridiumfactions.managers;

import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.configs.SQL;
import com.iridium.iridiumfactions.database.*;
import com.iridium.iridiumfactions.database.types.XMaterialType;
import com.iridium.iridiumfactions.managers.tablemanagers.FactionTableManager;
import com.iridium.iridiumfactions.managers.tablemanagers.ForeignFactionTableManager;
import com.iridium.iridiumfactions.managers.tablemanagers.UserTableManager;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.db.DatabaseTypeUtils;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.logger.NullLogBackend;
import com.j256.ormlite.support.ConnectionSource;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

/**
 * Class which handles the database connection and acts as a DAO.
 */
@Getter
public class DatabaseManager {

    @Getter(AccessLevel.NONE)
    private ConnectionSource connectionSource;

    private UserTableManager userTableManager;
    private FactionTableManager factionTableManager;
    private ForeignFactionTableManager<FactionInvite, Integer> factionInviteTableManager;
    private ForeignFactionTableManager<FactionClaim, Integer> factionClaimTableManager;
    private ForeignFactionTableManager<FactionPermission, Integer> factionPermissionTableManager;
    private ForeignFactionTableManager<FactionRelationship, Integer> factionRelationshipTableManager;

    public void init() throws SQLException {
        LoggerFactory.setLogBackendFactory(new NullLogBackend.NullLogBackendFactory());

        SQL sqlConfig = IridiumFactions.getInstance().getSql();
        String databaseURL = getDatabaseURL(sqlConfig);

        DataPersisterManager.registerDataPersisters(XMaterialType.getSingleton());

        this.connectionSource = new JdbcConnectionSource(
                databaseURL,
                sqlConfig.username,
                sqlConfig.password,
                DatabaseTypeUtils.createDatabaseType(databaseURL)
        );

        this.userTableManager = new UserTableManager(connectionSource, false);
        this.factionTableManager = new FactionTableManager(connectionSource, false);
        this.factionRelationshipTableManager = new ForeignFactionTableManager<>(connectionSource, FactionRelationship.class, false, Comparator.comparing(FactionRelationship::getFactionID).thenComparing(FactionRelationship::getFaction2ID));
        this.factionInviteTableManager = new ForeignFactionTableManager<>(connectionSource, FactionInvite.class, false, Comparator.comparing(FactionInvite::getFactionID).thenComparing(FactionInvite::getUser));
        this.factionClaimTableManager = new ForeignFactionTableManager<>(connectionSource, FactionClaim.class, false, Comparator.comparing(FactionClaim::getWorld).thenComparing(FactionClaim::getX).thenComparing(FactionClaim::getZ));
        this.factionPermissionTableManager = new ForeignFactionTableManager<>(connectionSource, FactionPermission.class, false, Comparator.comparing(FactionPermission::getId).thenComparing(FactionPermission::getRank).thenComparing(FactionPermission::getPermission));
    }

    /**
     * Database connection String used for establishing a connection.
     *
     * @return The database URL String
     */
    private @NotNull String getDatabaseURL(SQL sqlConfig) {
        switch (sqlConfig.driver) {
            case MYSQL:
                return "jdbc:" + sqlConfig.driver.name().toLowerCase() + "://" + sqlConfig.host + ":" + sqlConfig.port + "/" + sqlConfig.database + "?useSSL=" + sqlConfig.useSSL;
            case SQLITE:
                return "jdbc:sqlite:" + new File(IridiumFactions.getInstance().getDataFolder(), sqlConfig.database + ".db");
            default:
                throw new UnsupportedOperationException("Unsupported driver (how did we get here?): " + sqlConfig.driver.name());
        }
    }

    public CompletableFuture<Void> registerFaction(Faction faction) {
        return CompletableFuture.runAsync(() -> {
            factionTableManager.addEntry(faction);
            // Saving the object will also assign the Faction's ID
            factionTableManager.save();
            // Since the FactionID was null before we need to resort
            factionTableManager.sort();
        });
    }

}
