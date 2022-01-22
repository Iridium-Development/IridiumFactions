package com.iridium.iridiumfactions.managers;

import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.configs.SQL;
import com.iridium.iridiumfactions.database.*;
import com.iridium.iridiumfactions.database.types.InventoryType;
import com.iridium.iridiumfactions.database.types.LocationType;
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
    private ForeignFactionTableManager<FactionBlocks, Integer> factionBlocksTableManager;
    private ForeignFactionTableManager<FactionSpawners, Integer> factionSpawnersTableManager;
    private ForeignFactionTableManager<FactionWarp, Integer> factionWarpTableManager;
    private ForeignFactionTableManager<FactionUpgrade, Integer> factionUpgradeTableManager;
    private ForeignFactionTableManager<FactionChest, Integer> factionChestTableManager;
    private ForeignFactionTableManager<FactionAccess, Integer> factionAccessTableManager;

    public void init() throws SQLException {
        LoggerFactory.setLogBackendFactory(new NullLogBackend.NullLogBackendFactory());

        SQL sqlConfig = IridiumFactions.getInstance().getSql();
        String databaseURL = getDatabaseURL(sqlConfig);

        DataPersisterManager.registerDataPersisters(XMaterialType.getSingleton());
        DataPersisterManager.registerDataPersisters(LocationType.getSingleton());
        DataPersisterManager.registerDataPersisters(InventoryType.getSingleton());

        if (!IridiumFactions.getInstance().isTesting()) {

            this.connectionSource = new JdbcConnectionSource(
                    databaseURL,
                    sqlConfig.username,
                    sqlConfig.password,
                    DatabaseTypeUtils.createDatabaseType(databaseURL)
            );
        }

        this.userTableManager = new UserTableManager(connectionSource);
        this.factionTableManager = new FactionTableManager(connectionSource);
        this.factionRelationshipTableManager = new ForeignFactionTableManager<>(connectionSource, FactionRelationship.class, Comparator.comparing(FactionRelationship::getFactionID).thenComparing(FactionRelationship::getFaction2ID));
        this.factionInviteTableManager = new ForeignFactionTableManager<>(connectionSource, FactionInvite.class, Comparator.comparing(FactionInvite::getFactionID).thenComparing(FactionInvite::getUser));
        this.factionClaimTableManager = new ForeignFactionTableManager<>(connectionSource, FactionClaim.class, Comparator.comparing(FactionClaim::getWorld).thenComparing(FactionClaim::getX).thenComparing(FactionClaim::getZ));
        this.factionPermissionTableManager = new ForeignFactionTableManager<>(connectionSource, FactionPermission.class, Comparator.comparing(FactionPermission::getFactionID).thenComparing(FactionPermission::getRank).thenComparing(FactionPermission::getPermission));
        this.factionRelationshipRequestTableManager = new ForeignFactionTableManager<>(connectionSource, FactionRelationshipRequest.class, Comparator.comparing(FactionRelationshipRequest::getFactionID).thenComparing(FactionRelationshipRequest::getFaction2ID).thenComparing(FactionRelationshipRequest::getRelationshipType));
        this.factionBlocksTableManager = new ForeignFactionTableManager<>(connectionSource, FactionBlocks.class, Comparator.comparing(FactionBlocks::getFactionID).thenComparing(FactionBlocks::getMaterial));
        this.factionSpawnersTableManager = new ForeignFactionTableManager<>(connectionSource, FactionSpawners.class, Comparator.comparing(FactionSpawners::getFactionID).thenComparing(FactionSpawners::getSpawnerType));
        this.factionWarpTableManager = new ForeignFactionTableManager<>(connectionSource, FactionWarp.class, Comparator.comparing(FactionWarp::getFactionID).thenComparing(FactionWarp::getName));
        this.factionUpgradeTableManager = new ForeignFactionTableManager<>(connectionSource, FactionUpgrade.class, Comparator.comparing(FactionUpgrade::getFactionID).thenComparing(FactionUpgrade::getUpgrade));
        this.factionChestTableManager = new ForeignFactionTableManager<>(connectionSource, FactionChest.class, Comparator.comparing(FactionChest::getFactionID).thenComparing(FactionChest::getPage));
        this.factionAccessTableManager = new ForeignFactionTableManager<>(connectionSource, FactionAccess.class, Comparator.comparing(FactionAccess::getFactionID).thenComparing(FactionAccess::getClaimID).thenComparing(FactionAccess::getFactionRank));
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
