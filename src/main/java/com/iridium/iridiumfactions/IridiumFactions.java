package com.iridium.iridiumfactions;

import com.iridium.iridiumfactions.configs.*;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import com.iridium.iridiumfactions.listeners.EntityDamageListener;
import com.iridium.iridiumfactions.listeners.PlayerPortalListener;
import com.iridium.iridiumfactions.managers.CommandManager;
import com.iridium.iridiumfactions.managers.DatabaseManager;
import com.iridium.iridiumfactions.managers.FactionManager;
import com.iridium.iridiumfactions.managers.UserManager;
import com.iridium.iridiumfactions.placeholders.FactionPlaceholderBuilder;
import com.iridium.iridiumfactions.placeholders.TeamChatPlaceholderBuilder;
import com.iridium.iridiumfactions.placeholders.UserPlaceholderBuilder;
import com.iridium.iridiumteams.IridiumTeams;
import com.iridium.iridiumteams.managers.MissionManager;
import com.iridium.iridiumteams.managers.ShopManager;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.sql.SQLException;

@Getter
public class IridiumFactions extends IridiumTeams<Faction, User> {
    @Getter
    private static IridiumFactions instance;

    private Configuration configuration;
    private Messages messages;
    private Permissions permissions;
    private Inventories inventories;
    private Commands commands;
    private BankItems bankItems;
    private Enhancements enhancements;
    private BlockValues blockValues;
    private Top top;
    private SQL sql;
    private Missions missions;
    private Shop shop;
    private Settings settings;

    private FactionPlaceholderBuilder teamsPlaceholderBuilder;
    private UserPlaceholderBuilder userPlaceholderBuilder;
    private TeamChatPlaceholderBuilder teamChatPlaceholderBuilder;

    private FactionManager teamManager;
    private UserManager userManager;
    private CommandManager commandManager;
    private DatabaseManager databaseManager;
    private MissionManager<Faction, User> missionManager;
    private ShopManager<Faction, User> shopManager;

    private Economy economy;

    public IridiumFactions(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        instance = this;
    }

    public IridiumFactions() {
        instance = this;
    }

    @Override
    public void onEnable() {
        instance = this;

        this.teamManager = new FactionManager();

        this.userManager = new UserManager();
        this.commandManager = new CommandManager("iridiumfactions");
        this.databaseManager = new DatabaseManager();
        this.missionManager = new MissionManager<>(this);
        this.shopManager = new ShopManager<>(this);
        try {
            databaseManager.init();
        } catch (SQLException exception) {
            // We don't want the plugin to start if the connection fails
            exception.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.teamsPlaceholderBuilder = new FactionPlaceholderBuilder();
        this.userPlaceholderBuilder = new UserPlaceholderBuilder();
        this.teamChatPlaceholderBuilder = new TeamChatPlaceholderBuilder();

        Bukkit.getScheduler().runTask(this, () -> this.economy = setupEconomy());

        addBstats(6282);
        //TODO
//        startUpdateChecker();
        super.onEnable();
    }

    private Economy setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) {
            getLogger().warning("You do not have an economy plugin installed (like Essentials)");
            return null;
        }
        return economyProvider.getProvider();
    }

    @Override
    public void registerListeners() {
        super.registerListeners();
        Bukkit.getPluginManager().registerEvents(new PlayerPortalListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), this);
    }

    @Override
    public void loadConfigs() {
        this.configuration = getPersist().load(Configuration.class);
        this.messages = getPersist().load(Messages.class);
        this.commands = getPersist().load(Commands.class);
        this.sql = getPersist().load(SQL.class);
        this.inventories = getPersist().load(Inventories.class);
        this.permissions = getPersist().load(Permissions.class);
        this.bankItems = getPersist().load(BankItems.class);
        this.enhancements = getPersist().load(Enhancements.class);
        this.blockValues = getPersist().load(BlockValues.class);
        this.top = getPersist().load(Top.class);
        this.missions = getPersist().load(Missions.class);
        this.shop = getPersist().load(Shop.class);
        this.settings = getPersist().load(Settings.class);
        super.loadConfigs();
    }

    @Override
    public void saveConfigs() {
        super.saveConfigs();
        getPersist().save(configuration);
        getPersist().save(messages);
        getPersist().save(commands);
        getPersist().save(sql);
        getPersist().save(inventories);
        getPersist().save(permissions);
        getPersist().save(bankItems);
        getPersist().save(enhancements);
        getPersist().save(blockValues);
        getPersist().save(top);
        getPersist().save(missions);
        getPersist().save(shop);
        getPersist().save(settings);
    }

    @Override
    public void saveData() {
        getDatabaseManager().getUserTableManager().save();
        getDatabaseManager().getFactionTableManager().save();
        getDatabaseManager().getInvitesTableManager().save();
        getDatabaseManager().getTrustTableManager().save();
        getDatabaseManager().getPermissionsTableManager().save();
        getDatabaseManager().getBankTableManager().save();
        getDatabaseManager().getEnhancementTableManager().save();
        getDatabaseManager().getTeamBlockTableManager().save();
        getDatabaseManager().getTeamSpawnerTableManager().save();
        getDatabaseManager().getTeamWarpTableManager().save();
        getDatabaseManager().getTeamMissionTableManager().save();
        getDatabaseManager().getTeamMissionDataTableManager().save();
        getDatabaseManager().getTeamRewardsTableManager().save();
        getDatabaseManager().getTeamSettingsTableManager().save();
        getDatabaseManager().getFactionClaimsTableManager().save();
    }

    @Override
    public void initializeBankItem() {
        super.initializeBankItem();
        addBankItem(getBankItems().crystalsBankItem);
    }

    public FactionManager getFactionManager() {
        return teamManager;
    }

}
