package com.iridium.iridiumfactions.configs;

import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumfactions.FactionRank;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Configuration {
    public String prefix = "&c&lIridiumFactions &8»";
    public String dateTimeFormat = "EEEE, MMMM dd HH:mm:ss";

    public int mapWidth = 52;
    public int mapHeight = 10;
    public char[] mapChars = "\\/#$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz?".toCharArray();
    public String mapTitle = "&8[ &c(%chunk_x%, %chunk_z%) %faction% &8]";
    public String mapTitleFiller = "&8&m ";

    public Map<FactionRank, String> factionRankNames = ImmutableMap.<FactionRank, String>builder()
            .put(FactionRank.OWNER, "Owner")
            .put(FactionRank.CO_OWNER, "CoOwner")
            .put(FactionRank.MODERATOR, "Moderator")
            .put(FactionRank.MEMBER, "Member")
            .put(FactionRank.TRUCE, "Truce")
            .put(FactionRank.ALLY, "Ally")
            .put(FactionRank.ENEMY, "Enemy")
            .build();

    public String factionInfoTitle = "&8[ %faction% &8]";
    public String factionInfoTitleFiller = "&8&m ";
    public List<String> factionInfo = Arrays.asList(
            "&cDescription: &7%faction_description%",
            "&cLand / Remaining Power / Total Power: &7%faction_land% / %faction_remaining_power% / %faction_total_power%",
            "&cOnline Members(%faction_members_online_count%/%faction_members_count%): &7%faction_members_online%",
            "&cOffline Members(%faction_members_offline_count%/%faction_members_count%): &7%faction_members_offline%"
    );
}
