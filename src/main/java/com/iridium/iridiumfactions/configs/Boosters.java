package com.iridium.iridiumfactions.configs;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.fasterxml.annotation.JsonIgnoreProperties;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumfactions.Booster;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Boosters {

    public Booster flightBooster = new Booster(new Item(XMaterial.FEATHER, 16, 1, "&c&lFlight Booster", Arrays.asList(
            "&7Tired of walking? Buy this",
            "&7booster and Gain access to /f fly.",
            "",
            "&c&lInformation:",
            "&c&l * &7Time Remaining: &c%timeremaining_minutes% minutes and %timeremaining_seconds% seconds",
            "&c&l * &7Booster Cost: $%cost%",
            "",
            "&c&l[!] &cLeft Click to Purchase this Booster."
    )), 10000, 3600, "Flight", true, true);

}
