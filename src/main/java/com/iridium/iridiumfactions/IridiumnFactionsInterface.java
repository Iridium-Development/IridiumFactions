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
import java.util.Map;

public interface IridiumnFactionsInterface {

    public void loadConfigs();

    public void saveConfigs();

    public CommandManager getCommandManager();

    public DatabaseManager getDatabaseManager();

    public UserManager getUserManager();

    public FactionManager getFactionManager();

    public Configuration getConfiguration();

    public Messages getMessages();

    public Commands getCommands();

    public SQL getSql();

    public Inventories getInventories();

    public Permissions getPermissions();

    public Map<String, Permission> getPermissionList();

    public PluginDescriptionFile getDescription();

    public File getDataFolder();

    public NMS getNms();

    public PluginCommand getCommand(String name);
}
