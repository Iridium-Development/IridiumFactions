package com.iridium.iridiumfactions;

import com.iridium.iridiumcore.IridiumCore;
import com.iridium.iridiumfactions.commands.CommandManager;
import com.iridium.iridiumfactions.configs.*;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.listeners.*;
import com.iridium.iridiumfactions.managers.DatabaseManager;
import com.iridium.iridiumfactions.managers.FactionManager;
import com.iridium.iridiumfactions.managers.UserManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class IridiumFactions extends IridiumCore {

    private static IridiumFactions instance;

    private CommandManager commandManager;
    private DatabaseManager databaseManager;
    private UserManager userManager;
    private FactionManager factionManager;

    private Configuration configuration;
    private Messages messages;
    private Commands commands;
    private SQL sql;
    private Inventories inventories;
    private Permissions permissions;
    private BlockValues blockValues;
    private Upgrades upgrades;

    private Map<String, Permission> permissionList;
    private Map<String, Upgrade<?>> upgradesList;
    /*
    TODO LIST
     View Active Relationships
     View and Cancel Relationship requests
     Implement Power properly
     Faction Upgrades (Spawners, ExtraPower, Warps, FactionChest, Experience)
     Faction Bank (Money TNT Experience)
     Faction Missions
     Faction Main Menu
     Faction Claim Permissions (Certain ranks only)
     Faction Strikes
     */

    public IridiumFactions(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        instance = this;
    }

    public IridiumFactions() {
        instance = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        this.commandManager = new CommandManager("IridiumFactions");
        this.databaseManager = new DatabaseManager();
        try {
            databaseManager.init();
        } catch (SQLException exception) {
            // We don't want the plugin to start if the connection fails
            exception.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        this.userManager = new UserManager();
        this.factionManager = new FactionManager();

        // Auto Recalculate Factions
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            ListIterator<Integer> factions = getDatabaseManager().getFactionTableManager().getEntries().stream().map(Faction::getId).collect(Collectors.toList()).listIterator();

            @Override
            public void run() {
                if (!factions.hasNext()) {
                    factions = getDatabaseManager().getFactionTableManager().getEntries().stream().map(Faction::getId).collect(Collectors.toList()).listIterator();
                } else {
                    Faction faction = getFactionManager().getFactionViaId(factions.next());
                    if (faction.getFactionType() == FactionType.PLAYER_FACTION) {
                        getFactionManager().recalculateFactionValue(faction);
                    }
                }
            }

        }, 0, getConfiguration().factionRecalculateInterval * 20L);

        getLogger().info("----------------------------------------");
        getLogger().info("");
        getLogger().info(getDescription().getName() + " Enabled!");
        getLogger().info("Version: " + getDescription().getVersion());
        getLogger().info("");
        getLogger().info("----------------------------------------");
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerBucketListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockExplodeListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityExplodeListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPistonListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChatListener(), this);
    }

    @Override
    public void loadConfigs() {
        this.configuration = getPersist().load(Configuration.class);
        this.messages = getPersist().load(Messages.class);
        this.commands = getPersist().load(Commands.class);
        this.sql = getPersist().load(SQL.class);
        this.inventories = getPersist().load(Inventories.class);
        this.permissions = getPersist().load(Permissions.class);
        this.blockValues = getPersist().load(BlockValues.class);
        this.upgrades = getPersist().load(Upgrades.class);

        for (FactionRank factionRank : FactionRank.values()) {
            configuration.factionRankNames.putIfAbsent(factionRank, factionRank.name());
        }
        for (RelationshipType relationshipType : RelationshipType.values()) {
            configuration.factionRelationshipColors.putIfAbsent(relationshipType, relationshipType.getDefaultColor());
        }

        initializePermissionsList();
        initializeUpgradesList();
    }

    public void initializePermissionsList() {
        this.permissionList = new HashMap<>();
        this.permissionList.put(PermissionType.BLOCK_BREAK.getPermissionKey(), permissions.blockBreak);
        this.permissionList.put(PermissionType.BLOCK_PLACE.getPermissionKey(), permissions.blockPlace);
        this.permissionList.put(PermissionType.BUCKET.getPermissionKey(), permissions.bucket);
        this.permissionList.put(PermissionType.CHANGE_PERMISSIONS.getPermissionKey(), permissions.changePermissions);
        this.permissionList.put(PermissionType.CLAIM.getPermissionKey(), permissions.claim);
        this.permissionList.put(PermissionType.DEMOTE.getPermissionKey(), permissions.demote);
        this.permissionList.put(PermissionType.DESCRIPTION.getPermissionKey(), permissions.description);
        this.permissionList.put(PermissionType.DOORS.getPermissionKey(), permissions.doors);
        this.permissionList.put(PermissionType.INVITE.getPermissionKey(), permissions.invite);
        this.permissionList.put(PermissionType.KICK.getPermissionKey(), permissions.kick);
        this.permissionList.put(PermissionType.KILL_MOBS.getPermissionKey(), permissions.killMobs);
        this.permissionList.put(PermissionType.OPEN_CONTAINERS.getPermissionKey(), permissions.openContainers);
        this.permissionList.put(PermissionType.PROMOTE.getPermissionKey(), permissions.promote);
        this.permissionList.put(PermissionType.REDSTONE.getPermissionKey(), permissions.redstone);
        this.permissionList.put(PermissionType.RENAME.getPermissionKey(), permissions.rename);
        this.permissionList.put(PermissionType.SETHOME.getPermissionKey(), permissions.setHome);
        this.permissionList.put(PermissionType.SPAWNERS.getPermissionKey(), permissions.spawners);
        this.permissionList.put(PermissionType.UNCLAIM.getPermissionKey(), permissions.unclaim);
        this.permissionList.put(PermissionType.MANAGE_WARPS.getPermissionKey(), permissions.manageWarps);
    }

    public void initializeUpgradesList() {
        this.upgradesList = new HashMap<>();
        this.upgradesList.put(UpgradeType.CHEST_UPGRADE.getName(), upgrades.chestUpgrade);
        this.upgradesList.put(UpgradeType.POWER_UPGRADE.getName(), upgrades.powerUpgrade);
        this.upgradesList.put(UpgradeType.SPAWNER_UPGRADE.getName(), upgrades.spawnerUpgrade);
        this.upgradesList.put(UpgradeType.WARPS_UPGRADE.getName(), upgrades.warpsUpgrade);
        this.upgradesList.put(UpgradeType.EXPERIENCE_UPGRADE.getName(), upgrades.experienceUpgrade);
    }

    @Override
    public void saveConfigs() {
        getPersist().save(configuration);
        getPersist().save(messages);
        getPersist().save(commands);
        getPersist().save(sql);
        getPersist().save(inventories);
        getPersist().save(permissions);
        getPersist().save(blockValues);
        getPersist().save(upgrades);
    }

    @Override
    public void saveData() {
        getDatabaseManager().getUserTableManager().save();
        getDatabaseManager().getFactionTableManager().save();
        getDatabaseManager().getFactionRelationshipTableManager().save();
        getDatabaseManager().getFactionClaimTableManager().save();
        getDatabaseManager().getFactionPermissionTableManager().save();
        getDatabaseManager().getFactionBlocksTableManager().save();
        getDatabaseManager().getFactionSpawnersTableManager().save();
        getDatabaseManager().getFactionWarpTableManager().save();
        getDatabaseManager().getFactionUpgradeTableManager().save();
    }

    public static IridiumFactions getInstance() {
        return instance;
    }
}
