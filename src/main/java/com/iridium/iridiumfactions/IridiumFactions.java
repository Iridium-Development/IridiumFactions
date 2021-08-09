package com.iridium.iridiumfactions;

import com.iridium.iridiumcore.IridiumCore;
import com.iridium.iridiumfactions.commands.CommandManager;
import com.iridium.iridiumfactions.configs.Commands;
import com.iridium.iridiumfactions.configs.Configuration;
import com.iridium.iridiumfactions.configs.Messages;
import com.iridium.iridiumfactions.configs.SQL;
import lombok.Getter;

@Getter
public class IridiumFactions extends IridiumCore {

    private static IridiumFactions instance;

    private CommandManager commandManager;

    private Configuration configuration;
    private Messages messages;
    private Commands commands;
    private SQL sql;

    @Override
    public void onEnable() {
        instance = this;
        super.onEnable();
        this.commandManager = new CommandManager("IridiumFactions");
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
        super.registerListeners();
    }

    @Override
    public void loadConfigs() {
        this.configuration = getPersist().load(Configuration.class);
        this.messages = getPersist().load(Messages.class);
        this.commands = getPersist().load(Commands.class);
        this.sql = getPersist().load(SQL.class);
    }

    @Override
    public void saveConfigs() {
        getPersist().save(configuration);
        getPersist().save(messages);
        getPersist().save(commands);
        getPersist().save(sql);
    }

    @Override
    public void saveData() {
    }

    public static IridiumFactions getInstance() {
        return instance;
    }
}
