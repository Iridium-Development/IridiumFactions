package com.iridium.iridiumfactions;

import com.iridium.iridiumcore.nms.NMS;
import com.iridium.iridiumfactions.commands.CommandManager;
import com.iridium.iridiumfactions.configs.*;
import com.iridium.iridiumfactions.managers.DatabaseManager;
import com.iridium.iridiumfactions.managers.FactionManager;
import com.iridium.iridiumfactions.managers.UserManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.sql.SQLException;
import java.util.Map;

public class MockIridiumFactions implements IridiumnFactionsInterface{

    private DatabaseManager databaseManager;
    private UserManager userManager;
    private FactionManager factionManager;

    public void init(){
        this.databaseManager = new DatabaseManager();
        try {
            this.databaseManager.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.userManager = new UserManager();
        this.factionManager = new FactionManager();
    }


    @Override
    public void loadConfigs() {

    }

    @Override
    public void saveConfigs() {

    }

    public CommandManager getCommandManager() {
        return null;
    }

    public DatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    public UserManager getUserManager() {
        return this.userManager;
    }

    public FactionManager getFactionManager() {
        return this.factionManager;
    }

    public Configuration getConfiguration() {
        return null;
    }

    public Messages getMessages() {
        return null;
    }

    public Commands getCommands() {
        return null;
    }

    public SQL getSql() {
        return new SQL();
    }

    public Inventories getInventories() {
        return null;
    }

    public Permissions getPermissions() {
        return null;
    }

    public Map<String, Permission> getPermissionList() {
        return null;
    }

    @Override
    public PluginDescriptionFile getDescription() {
        return null;
    }

    @Override
    public File getDataFolder() {
        return null;
    }

    @Override
    public NMS getNms() {
        return null;
    }

    @Override
    public PluginCommand getCommand(String name) {
        return null;
    }
}
