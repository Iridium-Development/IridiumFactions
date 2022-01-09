package com.iridium.iridiumfactions.configs;

import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.fasterxml.annotation.JsonIgnoreProperties;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumfactions.Upgrade;
import com.iridium.iridiumfactions.upgrades.*;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Upgrades {
    public Upgrade<SpawnerUpgrade> spawnerUpgrade = new Upgrade<>(true, "Spawner",
            new Item(XMaterial.SPAWNER, 11, 1, "&c&lFaction Spawners", Arrays.asList(
                    "&7Spawners too slow? Buy this",
                    "&7upgrade to increase your spawner speeds.",
                    "",
                    "&c&lInformation:",
                    "&c&l * &7Current Level: &c%level%",
                    "&c&l * &7Current Modifier: &c%modifier%x Increase",
                    "&c&l * &7Upgrade Cost: &c$%upgrade_cost%",
                    "&c&lLevels:",
                    "&c&l * &7Level 1: &c1x Increase",
                    "&c&l * &7Level 2: &c1.5x Increase",
                    "&c&l * &7Level 3: &c2x Increase",
                    "",
                    "&c&l[!] &cLeft Click to Purchase this Upgrade"
            )), ImmutableMap.<Integer, SpawnerUpgrade>builder()
            .put(1, new SpawnerUpgrade(1000, 1))
            .put(2, new SpawnerUpgrade(1000, 1.5))
            .put(3, new SpawnerUpgrade(1000, 2))
            .build());

    public Upgrade<PowerUpgrade> powerUpgrade = new Upgrade<>(true, "Power",
            new Item(XMaterial.BEACON, 12, 1, "&c&lFaction Power", Arrays.asList(
                    "&7Not enough power? Buy this",
                    "&7upgrade to increase your Faction Power.",
                    "",
                    "&c&lInformation:",
                    "&c&l * &7Current Level: &c%level%",
                    "&c&l * &7Current Increase: &c%extraPower% Extra Power",
                    "&c&l * &7Upgrade Cost: &c$%upgrade_cost%",
                    "&c&lLevels:",
                    "&c&l * &7Level 1: &c0 extraPower",
                    "&c&l * &7Level 2: &c50 extraPower",
                    "&c&l * &7Level 3: &c100 extraPower",
                    "",
                    "&c&l[!] &cLeft Click to Purchase this Upgrade"
            )), ImmutableMap.<Integer, PowerUpgrade>builder()
            .put(1, new PowerUpgrade(1000, 0))
            .put(2, new PowerUpgrade(1000, 50))
            .put(3, new PowerUpgrade(1000, 100))
            .build());

    public Upgrade<WarpsUpgrade> warpsUpgrade = new Upgrade<>(true, "Warps",
            new Item(XMaterial.END_PORTAL_FRAME, 13, 1, "&c&lFaction Warps", Arrays.asList(
                    "&7Need more faction warps? Buy this",
                    "&7upgrade to increase your faction warps.",
                    "",
                    "&c&lInformation:",
                    "&c&l * &7Current Level: &c%level%",
                    "&c&l * &7Current Warps: &c%warps% Warps",
                    "&c&l * &7Upgrade Cost: &c$%upgrade_cost%",
                    "&c&lLevels:",
                    "&c&l * &7Level 1: &c1 Warp",
                    "&c&l * &7Level 2: &c2 Warp",
                    "&c&l * &7Level 3: &c3 Warp",
                    "&c&l * &7Level 4: &c4 Warp",
                    "&c&l * &7Level 5: &c5 Warp",
                    "",
                    "&c&l[!] &cLeft Click to Purchase this Upgrade"
            )), ImmutableMap.<Integer, WarpsUpgrade>builder()
            .put(1, new WarpsUpgrade(1000, 1))
            .put(2, new WarpsUpgrade(1000, 2))
            .put(3, new WarpsUpgrade(1000, 3))
            .put(4, new WarpsUpgrade(1000, 4))
            .put(5, new WarpsUpgrade(1000, 5))
            .build());

    public Upgrade<ChestUpgrade> chestUpgrade = new Upgrade<>(true, "Chest",
            new Item(XMaterial.CHEST, 14, 1, "&c&lFaction Chest", Arrays.asList(
                    "&7Need more slots in your Faction Chest? Buy this",
                    "&7upgrade to increase your Faction Chest size.",
                    "",
                    "&c&lInformation:",
                    "&c&l * &7Current Level: &c%level%",
                    "&c&l * &7Current Size: &c%slots% Slots",
                    "&c&l * &7Upgrade Cost: &c$%upgrade_cost%",
                    "&c&lLevels:",
                    "&c&l * &7Level 1: &c9 Slots",
                    "&c&l * &7Level 2: &c18 Slots",
                    "&c&l * &7Level 3: &c27 Slots",
                    "&c&l * &7Level 4: &c36 Slots",
                    "&c&l * &7Level 5: &c45 Slots",
                    "",
                    "&c&l[!] &cLeft Click to Purchase this Upgrade"
            )), ImmutableMap.<Integer, ChestUpgrade>builder()
            .put(1, new ChestUpgrade(1000, 1))
            .put(2, new ChestUpgrade(1000, 6))
            .put(3, new ChestUpgrade(1000, 12))
            .put(4, new ChestUpgrade(1000, 18))
            .put(5, new ChestUpgrade(1000, 24))
            .build());

    public Upgrade<ExperienceUpgrade> experienceUpgrade = new Upgrade<>(true, "Experience",
            new Item(XMaterial.EXPERIENCE_BOTTLE, 15, 1, "&c&lFaction Experience", Arrays.asList(
                    "&7Gaining Experience too slow? Buy this",
                    "&7upgrade to increase your experience gain.",
                    "",
                    "&c&lInformation:",
                    "&c&l * &7Current Level: &c%level%",
                    "&c&l * &7Current Modifier: &c%modifier%x Increase",
                    "&c&l * &7Upgrade Cost: &c$%upgrade_cost%",
                    "&c&lLevels:",
                    "&c&l * &7Level 1: &c1x Increase",
                    "&c&l * &7Level 2: &c1.5x Increase",
                    "&c&l * &7Level 3: &c2x Increase",
                    "&c&l * &7Level 4: &c3x Increase",
                    "",
                    "&c&l[!] &cLeft Click to Purchase this Upgrade"
            )), ImmutableMap.<Integer, ExperienceUpgrade>builder()
            .put(1, new ExperienceUpgrade(1000, 1))
            .put(2, new ExperienceUpgrade(1000, 1.5))
            .put(3, new ExperienceUpgrade(1000, 2))
            .put(4, new ExperienceUpgrade(1000, 3))
            .build());
}
