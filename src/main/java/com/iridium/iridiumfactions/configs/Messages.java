package com.iridium.iridiumfactions.configs;

public class Messages extends com.iridium.iridiumteams.configs.Messages {

    public Messages() {
        super("Faction", "f", "IridiumFactions", "&c");

        teamCreated = "%prefix% &7Faction Creation Completed!";
    }
    public String noSafeLocation = "%prefix% &7Could not find a safe location to teleport to.";
    public String creatingFaction = "%prefix% &7Creating Faction, please wait...";

    public String factionClaimedLand = "%prefix% &7%player% has claimed land at (%x%,%z%).";
    public String landAlreadyClaimed = "%prefix% &7This land has already been claimed by %faction%.";
    public String factionLandNotClaim = "%prefix% &7This land has not been claimed by %faction%.";
    public String factionUnClaimedLand = "%prefix% &7%player% has un-claimed land at (%x%,%z%).";
    public String factionUnClaimedAllLand = "%prefix% &7%player% has un-claimed all land for %faction%.";
    public String factionDescriptionChanged = "%prefix% &7%player% has changed your faction description to %description%.";
    public String factionNameChanged = "%prefix% &7%player% has changed your faction name to %name%.";
    public String notEnoughPowerToClaim = "%prefix% &7You do not have enough power to claim this land.";
    public String cannotClaimLand = "%prefix% &7You cannot claim land in this Faction.";
}
