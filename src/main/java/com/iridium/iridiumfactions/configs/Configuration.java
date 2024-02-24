package com.iridium.iridiumfactions.configs;

import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumcore.dependencies.xseries.XSound;
import com.iridium.iridiumfactions.FactionConfiguration;
import com.iridium.iridiumfactions.RelationshipType;
import com.iridium.iridiumteams.Reward;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Configuration extends com.iridium.iridiumteams.configs.Configuration {

    public Configuration() {
        super("&c", "Faction", "IridiumFactions");
        this.createRequiresName = true;
        this.preventTntGriefing = false;

        this.levelRewards = ImmutableMap.<Integer, Reward>builder()
                .put(1, new Reward(new Item(XMaterial.EXPERIENCE_BOTTLE, 1, "&c&lLevel %faction_level% Reward", Arrays.asList(
                        "&7Faction Level %faction_level% Rewards:",
                        "&c&l* &c1000 Money",
                        "&c&l* &c5 Faction Crystals",
                        "",
                        "&c&l[!] &cLeft click to redeem"
                )), Collections.emptyList(), 0, new ImmutableMap.Builder<String, Double>().put("Crystals", 5.00).build(), 200, 0, XSound.ENTITY_PLAYER_LEVELUP))

                .put(5, new Reward(new Item(XMaterial.EXPERIENCE_BOTTLE, 1, "&c&lLevel %faction_level% Reward", Arrays.asList(
                        "&7Faction Level %faction_level% Rewards:",
                        "&c&l* &c10000 Money",
                        "&c&l* &c10 Faction Crystals",
                        "",
                        "&c&l[!] &cLeft click to redeem"
                )), Collections.emptyList(), 0, new ImmutableMap.Builder<String, Double>().put("Crystals", 10.00).build(), 2000, 0, XSound.ENTITY_PLAYER_LEVELUP))
                .build();

        this.teamInfo = Arrays.asList(
                "&cDescription: &7%faction_description%",
                "&cLand / Remaining Power / Total Power: &7%faction_land% / %faction_remaining_power% / %faction_total_power%",
                "&cLevel: &7%faction_level% (#%faction_experience_rank%)",
                "&cValue: &7%faction_value% (#%faction_value_rank%)",
                "&cOnline Members(%faction_members_online_count%/%faction_members_count%): &7%faction_members_online%",
                "&cOffline Members(%faction_members_offline_count%/%faction_members_count%): &7%faction_members_offline%"
        );
    }

    public String factionCreateTitle = "&c&lFaction Created";
    public String factionCreateSubTitle = "&7IridiumFactions by Peaches_MLG";
    public String defaultDescription = "Default faction description :c";
    public String factionTitleTop = "%faction_relationship_color%%faction_name%";
    public String factionTitleBottom = "&7%faction_description%";

    public int mapWidth = 52;
    public int mapHeight = 10;
    public double maxPower = 10;
    public double minPower = -10;
    public double startingPower = 10;
    public double powerLossPerDeath = 3;
    public int powerRecoveryDelayInSeconds = 600;
    public double powerRecoveryAmount = 1;

    public boolean disablePortals = true;

    public char[] mapChars = "\\/#$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz?".toCharArray();
    public String mapTitle = "&8[ &c(%chunk_x%, %chunk_z%) %faction% &8]";
    public String mapTitleFiller = "&8&m ";
    public FactionConfiguration wildernessFaction = new FactionConfiguration("Wilderness", "");
    public FactionConfiguration warzoneFaction = new FactionConfiguration("Warzone", "");
    public FactionConfiguration safezoneFaction = new FactionConfiguration("Safezone", "");


    public Map<RelationshipType, String> factionRelationshipColors = new HashMap<>(ImmutableMap.<RelationshipType, String>builder()
            .put(RelationshipType.OWN, "&a")
            .put(RelationshipType.ALLY, "&d")
            .put(RelationshipType.TRUCE, "&7")
            .put(RelationshipType.ENEMY, "&c")
            .put(RelationshipType.WILDERNESS, "&2")
            .put(RelationshipType.WARZONE, "&c")
            .put(RelationshipType.SAFEZONE, "&e")
            .build());
    public Map<RelationshipType, Integer> factionRelationshipLimits = new HashMap<>(ImmutableMap.<RelationshipType, Integer>builder()
            .put(RelationshipType.ALLY, 1)
            .build());

    public Item factionCrystal = new Item(XMaterial.NETHER_STAR, 1, "&c*** &c&lFaction Crystal &c***", Arrays.asList(
            "",
            "&c%amount% Faction Crystals",
            "&c&l[!] &cRight-Click to Redeem"
    ));

}
