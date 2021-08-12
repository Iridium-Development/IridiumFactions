package com.iridium.iridiumfactions.configs;

import com.iridium.iridiumfactions.commands.CreateCommand;
import com.iridium.iridiumfactions.commands.HelpCommand;
import com.iridium.iridiumfactions.commands.ReloadCommand;

public class Commands {
    public CreateCommand createCommand = new CreateCommand();
    public HelpCommand helpCommand = new HelpCommand();
    public ReloadCommand reloadCommand = new ReloadCommand();
}
