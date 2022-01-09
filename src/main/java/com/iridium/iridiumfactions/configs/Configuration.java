package com.iridium.iridiumfactions.configs;

import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumcore.utils.NumberFormatter;
import com.iridium.iridiumfactions.FactionConfiguration;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.RelationshipType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {
    public String prefix = "&c&lIridiumFactions &8»";
    public String dateTimeFormat = "EEEE, MMMM dd HH:mm:ss";

    public int mapWidth = 52;
    public int mapHeight = 10;
    public int factionRecalculateInterval = 5;
    public int minFactionNameLength = 3;
    public int maxFactionNameLength = 20;

    public char[] mapChars = "\\/#$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz?".toCharArray();
    public String mapTitle = "&8[ &c(%chunk_x%, %chunk_z%) %faction% &8]";
    public String mapTitleFiller = "&8&m ";

    public FactionConfiguration playerFaction = new FactionConfiguration("Default Faction Description");
    public FactionConfiguration wildernessFaction = new FactionConfiguration("Wilderness", "");
    public FactionConfiguration warzoneFaction = new FactionConfiguration("Warzone", "");
    public FactionConfiguration safezoneFaction = new FactionConfiguration("Safezone", "");

    public NumberFormatter numberFormatter = new NumberFormatter();

    public List<Integer> factionWarpSlots = Arrays.asList(9, 11, 13, 15, 17);

    public Map<FactionRank, String> factionRankNames = new HashMap<>(ImmutableMap.<FactionRank, String>builder()
            .put(FactionRank.OWNER, "Owner")
            .put(FactionRank.CO_OWNER, "CoOwner")
            .put(FactionRank.MODERATOR, "Moderator")
            .put(FactionRank.MEMBER, "Member")
            .put(FactionRank.TRUCE, "Truce")
            .put(FactionRank.ALLY, "Ally")
            .put(FactionRank.ENEMY, "Enemy")
            .build());
    public Map<RelationshipType, String> factionRelationshipColors = new HashMap<>(ImmutableMap.<RelationshipType, String>builder()
            .put(RelationshipType.OWN, "&a")
            .put(RelationshipType.ALLY, "&d")
            .put(RelationshipType.TRUCE, "&7")
            .put(RelationshipType.ENEMY, "&c")
            .put(RelationshipType.WILDERNESS, "&2")
            .put(RelationshipType.WARZONE, "&c")
            .put(RelationshipType.SAFEZONE, "&e")
            .build());

    public String factionInfoTitle = "&8[ %faction% &8]";
    public String factionInfoTitleFiller = "&8&m ";
    public List<String> factionInfo = Arrays.asList(
            "&cDescription: &7%faction_description%",
            "&cLand / Remaining Power / Total Power: &7%faction_land% / %faction_remaining_power% / %faction_total_power%",
            "&cRank: &7#%faction_rank%",
            "&cValue: &7%faction_value%",
            "&cOnline Members(%faction_members_online_count%/%faction_members_count%): &7%faction_members_online%",
            "&cOffline Members(%faction_members_offline_count%/%faction_members_count%): &7%faction_members_offline%"
    );

    public Map<Integer, Integer> factionTopSlots = ImmutableMap.<Integer, Integer>builder()
            .put(1, 4)
            .put(2, 12)
            .put(3, 14)
            .put(4, 19)
            .put(5, 20)
            .put(6, 21)
            .put(7, 22)
            .put(8, 23)
            .put(9, 24)
            .put(10, 25)
            .build();
}
