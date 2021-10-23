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
    private ForeignFactionTableManager<FactionRelationshipRequest, Integer> factionRelationshipRequestTableManager;

    public void init() throws SQLException {
        LoggerFactory.setLogBackendFactory(new NullLogBackend.NullLogBackendFactory());

        SQL sqlConfig = IridiumFactions.getInstance().getSql();
        String databaseURL = getDatabaseURL(sqlConfig);

        DataPersisterManager.registerDataPersisters(XMaterialType.getSingleton());

        try {
            this.connectionSource = new JdbcConnectionSource(
                    databaseURL,
                    sqlConfig.username,
                    sqlConfig.password,
                    DatabaseTypeUtils.createDatabaseType(databaseURL)
            );
        } catch (Exception ignored) {

        }

        if (connectionSource != null) {
            this.userTableManager = new UserTableManager(connectionSource);
            this.factionTableManager = new FactionTableManager(connectionSource);
            this.factionRelationshipTableManager = new ForeignFactionTableManager<>(connectionSource, FactionRelationship.class, Comparator.comparing(FactionRelationship::getFactionID).thenComparing(FactionRelationship::getFaction2ID));
            this.factionInviteTableManager = new ForeignFactionTableManager<>(connectionSource, FactionInvite.class, Comparator.comparing(FactionInvite::getFactionID).thenComparing(FactionInvite::getUser));
            this.factionClaimTableManager = new ForeignFactionTableManager<>(connectionSource, FactionClaim.class, Comparator.comparing(FactionClaim::getWorld).thenComparing(FactionClaim::getX).thenComparing(FactionClaim::getZ));
            this.factionPermissionTableManager = new ForeignFactionTableManager<>(connectionSource, FactionPermission.class, Comparator.comparing(FactionPermission::getFactionID).thenComparing(FactionPermission::getRank).thenComparing(FactionPermission::getPermission));
            this.factionRelationshipRequestTableManager = new ForeignFactionTableManager<>(connectionSource, FactionRelationshipRequest.class, Comparator.comparing(FactionRelationshipRequest::getFactionID).thenComparing(FactionRelationshipRequest::getFaction2ID).thenComparing(FactionRelationshipRequest::getRelationshipType));
        } else {
            this.userTableManager = new UserTableManager();
            this.factionTableManager = new FactionTableManager();
            this.factionRelationshipTableManager = new ForeignFactionTableManager<>(FactionRelationship.class, Comparator.comparing(FactionRelationship::getFactionID).thenComparing(FactionRelationship::getFaction2ID));
            this.factionInviteTableManager = new ForeignFactionTableManager<>(FactionInvite.class, Comparator.comparing(FactionInvite::getFactionID).thenComparing(FactionInvite::getUser));
            this.factionClaimTableManager = new ForeignFactionTableManager<>(FactionClaim.class, Comparator.comparing(FactionClaim::getWorld).thenComparing(FactionClaim::getX).thenComparing(FactionClaim::getZ));
            this.factionPermissionTableManager = new ForeignFactionTableManager<>(FactionPermission.class, Comparator.comparing(FactionPermission::getFactionID).thenComparing(FactionPermission::getRank).thenComparing(FactionPermission::getPermission));
            this.factionRelationshipRequestTableManager = new ForeignFactionTableManager<>(FactionRelationshipRequest.class, Comparator.comparing(FactionRelationshipRequest::getFactionID).thenComparing(FactionRelationshipRequest::getFaction2ID).thenComparing(FactionRelationshipRequest::getRelationshipType));
        }
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
