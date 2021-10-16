package com.iridium.iridiumfactions;

import com.iridium.iridiumcore.IridiumCore;
import com.iridium.iridiumfactions.commands.CommandManager;
import com.iridium.iridiumfactions.configs.*;
import com.iridium.iridiumfactions.listeners.InventoryClickListener;
import com.iridium.iridiumfactions.listeners.PlayerJoinListener;
import com.iridium.iridiumfactions.listeners.PlayerMoveListener;
import com.iridium.iridiumfactions.managers.DatabaseManager;
import com.iridium.iridiumfactions.managers.FactionManager;
import com.iridium.iridiumfactions.managers.UserManager;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.sql.SQLException;

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

    @Override
    public void onEnable() {
        instance = this;
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
    }

    @Override
    public void loadConfigs() {
        this.configuration = getPersist().load(Configuration.class);
        this.messages = getPersist().load(Messages.class);
        this.commands = getPersist().load(Commands.class);
        this.sql = getPersist().load(SQL.class);
        this.inventories = getPersist().load(Inventories.class);
    }

    @Override
    public void saveConfigs() {
        getPersist().save(configuration);
        getPersist().save(messages);
        getPersist().save(commands);
        getPersist().save(sql);
        getPersist().save(inventories);
    }

    @Override
    public void saveData() {
        getDatabaseManager().getUserTableManager().save();
        getDatabaseManager().getFactionTableManager().save();
    }

    public static IridiumFactions getInstance() {
        return instance;
    }
}
