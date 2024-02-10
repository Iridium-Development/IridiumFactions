package com.iridium.iridiumfactions.managers;

import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.configs.SQL;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.managers.tablemanagers.FactionTableManager;
import com.iridium.iridiumfactions.managers.tablemanagers.ForeignFactionTableManager;
import com.iridium.iridiumfactions.managers.tablemanagers.TableManager;
import com.iridium.iridiumfactions.managers.tablemanagers.UserTableManager;
import com.iridium.iridiumteams.database.*;
import com.iridium.iridiumteams.database.types.*;
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
import java.util.concurrent.CompletableFuture;

@Getter
public class DatabaseManager {

    @Getter(AccessLevel.NONE)
    private ConnectionSource connectionSource;

    private UserTableManager userTableManager;
    private FactionTableManager factionTableManager;
    private TableManager<String, TeamMissionData, Integer> teamMissionDataTableManager;
    private ForeignFactionTableManager<String, TeamInvite> invitesTableManager;
    private ForeignFactionTableManager<String, TeamTrust> trustTableManager;
    private ForeignFactionTableManager<String, TeamPermission> permissionsTableManager;
    private ForeignFactionTableManager<String, TeamBank> bankTableManager;
    private ForeignFactionTableManager<String, TeamEnhancement> enhancementTableManager;
    private ForeignFactionTableManager<String, TeamBlock> teamBlockTableManager;
    private ForeignFactionTableManager<String, TeamSpawners> teamSpawnerTableManager;
    private ForeignFactionTableManager<String, TeamWarp> teamWarpTableManager;
    private ForeignFactionTableManager<String, TeamMission> teamMissionTableManager;
    private ForeignFactionTableManager<String, TeamReward> teamRewardsTableManager;
    private ForeignFactionTableManager<String, TeamSetting> teamSettingsTableManager;

    public void init() throws SQLException {
        LoggerFactory.setLogBackendFactory(new NullLogBackend.NullLogBackendFactory());

        SQL sqlConfig = IridiumFactions.getInstance().getSql();
        String databaseURL = getDatabaseURL(sqlConfig);

        DataPersisterManager.registerDataPersisters(XMaterialType.getSingleton());
        DataPersisterManager.registerDataPersisters(LocationType.getSingleton());
        DataPersisterManager.registerDataPersisters(InventoryType.getSingleton());
        DataPersisterManager.registerDataPersisters(LocalDateTimeType.getSingleton());
        DataPersisterManager.registerDataPersisters(RewardType.getSingleton(IridiumFactions.getInstance()));

        this.connectionSource = new JdbcConnectionSource(
                databaseURL,
                sqlConfig.username,
                sqlConfig.password,
                DatabaseTypeUtils.createDatabaseType(databaseURL)
        );

        this.userTableManager = new UserTableManager(connectionSource);
        this.factionTableManager = new FactionTableManager(connectionSource);
        this.teamMissionDataTableManager = new TableManager<>(teamMissionData -> teamMissionData.getMissionID()+"-"+teamMissionData.getMissionIndex() ,connectionSource, TeamMissionData.class);
        this.invitesTableManager = new ForeignFactionTableManager<>(teamInvite -> teamInvite.getTeamID()+"-"+teamInvite.getUser().toString(), connectionSource, TeamInvite.class);
        this.trustTableManager = new ForeignFactionTableManager<>(teamTrust -> teamTrust.getTeamID()+"-"+teamTrust.getUser().toString(), connectionSource, TeamTrust.class);
        this.permissionsTableManager = new ForeignFactionTableManager<>(teamPermission -> teamPermission.getTeamID()+"-"+teamPermission.getPermission()+"-"+teamPermission.getRank(), connectionSource, TeamPermission.class);
        this.bankTableManager = new ForeignFactionTableManager<>(teamBank -> teamBank.getTeamID()+"-"+teamBank.getBankItem(), connectionSource, TeamBank.class);
        this.enhancementTableManager = new ForeignFactionTableManager<>(teamEnhancement -> teamEnhancement.getTeamID()+"-"+teamEnhancement.getEnhancementName(), connectionSource, TeamEnhancement.class);
        this.teamBlockTableManager = new ForeignFactionTableManager<>(teamBlock -> teamBlock.getTeamID()+"-"+teamBlock.getXMaterial().name(), connectionSource, TeamBlock.class);
        this.teamSpawnerTableManager = new ForeignFactionTableManager<>(teamSpawner -> teamSpawner.getTeamID()+"-"+teamSpawner.getEntityType().name(), connectionSource, TeamSpawners.class);
        this.teamWarpTableManager = new ForeignFactionTableManager<>(teamWarp -> teamWarp.getTeamID()+"-"+teamWarp.getName(), connectionSource, TeamWarp.class);
        this.teamMissionTableManager = new ForeignFactionTableManager<>(teamMission -> teamMission.getTeamID()+"-"+teamMission.getMissionName(), connectionSource, TeamMission.class);
        this.teamRewardsTableManager = new ForeignFactionTableManager<>(teamRewards -> String.valueOf(teamRewards.getId()), connectionSource, TeamReward.class);
        this.teamSettingsTableManager = new ForeignFactionTableManager<>(teamSetting -> teamSetting.getTeamID()+"-"+teamSetting.getSetting(), connectionSource, TeamSetting.class);
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
            // Saving the object will also assign the Factions's ID
            factionTableManager.save(faction);

            factionTableManager.addEntry(faction);
        });
    }

}
