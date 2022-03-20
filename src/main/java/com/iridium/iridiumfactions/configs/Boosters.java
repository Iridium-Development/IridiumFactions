package com.iridium.iridiumfactions.configs;

import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.fasterxml.annotation.JsonIgnoreProperties;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumcore.dependencies.xseries.XPotion;
import com.iridium.iridiumfactions.Booster;
import com.iridium.iridiumfactions.PotionBooster;

import java.util.Arrays;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Boosters {

    public boolean boostersOnlyEffectFactionMembers = true;
    public boolean boostersOnlyInTerritory = true;

    public Booster flightBooster = new Booster(new Item(XMaterial.FEATHER, 10, 1, "&c&lFlight Booster", Arrays.asList(
            "&7Tired of walking? Buy this",
            "&7booster and Gain access to /f fly.",
            "",
            "&c&lInformation:",
            "&c&l * &7Time Remaining: &c%timeremaining_minutes% minutes and %timeremaining_seconds% seconds",
            "&c&l * &7Booster Cost: $%cost%",
            "",
            "&c&l[!] &cLeft Click to Purchase this Booster."
    )), 10000, 3600, "Flight", true, true);

    public Map<String, PotionBooster> potionBoosters = new ImmutableMap.Builder<String, PotionBooster>()
            .put("haste", new PotionBooster(new Item(XMaterial.DIAMOND_PICKAXE, 12, 1, "&c&lHaste Booster", Arrays.asList(
                    "&7Gain a Haste Potion Effect.",
                    "",
                    "&c&lInformation:",
                    "&c&l * &7Time Remaining: &c%timeremaining_minutes% minutes and %timeremaining_seconds% seconds",
                    "&c&l * &7Booster Cost: $%cost%",
                    "",
                    "&c&l[!] &cLeft Click to Purchase this Booster."
            )), 10000, 3600, "Speed", true, true, 2, XPotion.FAST_DIGGING))

            .put("strength", new PotionBooster(new Item(XMaterial.DIAMOND_SWORD, 14, 1, "&c&lDamage Booster", Arrays.asList(
                    "&7Gain a Strength Potion Effect.",
                    "",
                    "&c&lInformation:",
                    "&c&l * &7Time Remaining: &c%timeremaining_minutes% minutes and %timeremaining_seconds% seconds",
                    "&c&l * &7Booster Cost: $%cost%",
                    "",
                    "&c&l[!] &cLeft Click to Purchase this Booster."
            )), 10000, 3600, "Jump", true, true, 2, XPotion.INCREASE_DAMAGE))

            .put("regeneration", new PotionBooster(new Item(XMaterial.ENCHANTED_GOLDEN_APPLE, 16, 1, "&c&lRegeneration Booster", Arrays.asList(
                    "&7Gain a Regeneration Potion Effect.",
                    "",
                    "&c&lInformation:",
                    "&c&l * &7Time Remaining: &c%timeremaining_minutes% minutes and %timeremaining_seconds% seconds",
                    "&c&l * &7Booster Cost: $%cost%",
                    "",
                    "&c&l[!] &cLeft Click to Purchase this Booster."
            )), 10000, 3600, "Speed", true, true, 2, XPotion.REGENERATION))
            .build();

}
