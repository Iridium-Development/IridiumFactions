package com.iridium.iridiumfactions.configs;

public class Messages extends com.iridium.iridiumteams.configs.Messages {

    public Messages() {
        super("Faction", "f", "IridiumFactions", "&c");

        teamCreated = "%prefix% &7Faction Creation Completed!";
    }
    public String itemsString = "%amount% %item_name%";
    public String noSafeLocation = "%prefix% &7Could not find a safe location to teleport to.";
    public String creatingFaction = "%prefix% &7Creating Faction, please wait...";
}
