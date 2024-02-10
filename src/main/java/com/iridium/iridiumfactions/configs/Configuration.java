package com.iridium.iridiumfactions.configs;

import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumcore.dependencies.xseries.XSound;
import com.iridium.iridiumteams.Reward;

import java.util.Arrays;
import java.util.Collections;

public class Configuration extends com.iridium.iridiumteams.configs.Configuration {
    public Configuration() {
        super("&c", "Faction", "IridiumFactions");
        this.createRequiresName = true;

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
    }

    public String factionCreateTitle = "&c&lFaction Created";
    public String factionCreateSubTitle = "&7IridiumFactions by Peaches_MLG";
    public String defaultDescription = "Default faction description :c";
    public String factionTitleTop = "&c%faction_name%";
    public String factionTitleBottom = "&7%faction_description%";

    public Item factionCrystal = new Item(XMaterial.NETHER_STAR, 1, "&c*** &c&lFaction Crystal &c***", Arrays.asList(
            "",
            "&c%amount% Faction Crystals",
            "&c&l[!] &cRight-Click to Redeem"
    ));

}
