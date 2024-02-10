package com.iridium.iridiumfactions.configs;

import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumcore.dependencies.xseries.XSound;
import com.iridium.iridiumteams.Reward;
import com.iridium.iridiumteams.missions.Mission;
import com.iridium.iridiumteams.missions.MissionData;
import com.iridium.iridiumteams.missions.MissionType;

import java.util.Arrays;
import java.util.Collections;

public class Missions extends com.iridium.iridiumteams.configs.Missions {

    public Missions() {
        super("&c");
        this.missions = ImmutableMap.<String, Mission>builder()
                .put("farmer", new Mission(ImmutableMap.<Integer, MissionData>builder()
                        .put(1, new MissionData(new Item(XMaterial.SUGAR_CANE, 1, "&c&lFarmer",
                                Arrays.asList(
                                        "&7Complete Faction Missions to gain rewards",
                                        "&7Which can be used to purchase Faction Upgrades",
                                        "",
                                        "&c&lObjectives:",
                                        "&c&l* &7Grow 10 Sugarcane: %progress_1%/10",
                                        "&c&l* &7Grow 10 Wheat: %progress_2%/10",
                                        "&c&l* &7Grow 10 Carrots: %progress_3%/10",
                                        "",
                                        "&c&lRewards",
                                        "&c&l* &75 Faction Crystals",
                                        "&c&l* &7$1000",
                                        "",
                                        "&c&l * &7Time Remaining: " + "&c%timeremaining_hours% hours %timeremaining_minutes% minutes and %timeremaining_seconds% seconds"
                                )
                        ), Arrays.asList("GROW:SUGAR_CANE:10", "GROW:WHEAT:10", "GROW:CARROTS:10"), new Reward(new Item(XMaterial.DIAMOND, 1, "&c&lFarmer Reward",
                                Arrays.asList(
                                        "&c&l Rewards",
                                        "&c&l* &75 Faction Crystals",
                                        "&c&l* &7$1000"
                                )), Collections.emptyList(), 1000, new ImmutableMap.Builder<String, Double>().put("Crystals", 5.00).build(), 0, 10, XSound.ENTITY_PLAYER_LEVELUP),
                                "%prefix% &7Farmer mission Completed!\n" +
                                        "&c&l* &7+3 Faction Experience\n" +
                                        "&c&l* &7+5 Faction Crystals\n" +
                                        "&c&l* &7+1000 Money\n" +
                                        "&7/is rewards to redeem rewards"
                        )).build(), MissionType.DAILY))

                .put("hunter", new Mission(ImmutableMap.<Integer, MissionData>builder()
                        .put(1, new MissionData(new Item(XMaterial.BONE, 1, "&c&lHunter",
                                Arrays.asList(
                                        "&7Complete Faction Missions to gain rewards",
                                        "&7Which can be used to purchase Faction Upgrades",
                                        "",
                                        "&c&lObjectives:",
                                        "&c&l* &7Kill 10 Zombies: %progress_1%/10",
                                        "&c&l* &7Kill 10 Skeletons: %progress_2%/10",
                                        "&c&l* &7Kill 10 Creepers: %progress_3%/10",
                                        "",
                                        "&c&lRewards",
                                        "&c&l* &75 Faction Crystals",
                                        "&c&l* &7$1000",
                                        "",
                                        "&c&l * &7Time Remaining: " + "&c%timeremaining_hours% hours %timeremaining_minutes% minutes and %timeremaining_seconds% seconds"
                                )
                        ), Arrays.asList("KILL:ZOMBIE:10", "KILL:SKELETON:10", "KILL:CREEPER:10"), new Reward(new Item(XMaterial.DIAMOND, 1, "&c&lHunter Reward",
                                Arrays.asList(
                                        "&c&l Rewards",
                                        "&c&l* &75 Faction Crystals",
                                        "&c&l* &7$1000"
                                )), Collections.emptyList(), 1000, new ImmutableMap.Builder<String, Double>().put("Crystals", 5.00).build(), 0, 10, XSound.ENTITY_PLAYER_LEVELUP),
                                "%prefix% &7Hunter mission Completed!\n" +
                                        "&c&l* &7+3 Faction Experience\n" +
                                        "&c&l* &7+5 Faction Crystals\n" +
                                        "&c&l* &7+1000 Money\n" +
                                        "&7/is rewards to redeem rewards"
                        )).build(), MissionType.DAILY))

                .put("baker", new Mission(ImmutableMap.<Integer, MissionData>builder()
                        .put(1, new MissionData(new Item(XMaterial.BREAD, 1, "&c&lBaker",
                                Arrays.asList(
                                        "&7Complete Faction Missions to gain rewards",
                                        "&7Which can be used to purchase Faction Upgrades",
                                        "",
                                        "&c&lObjectives:",
                                        "&c&l* &7Bake 64 Bread: %progress_1%/64",
                                        "",
                                        "&c&lRewards",
                                        "&c&l* &75 Faction Crystals",
                                        "&c&l* &7$1000",
                                        "",
                                        "&c&l * &7Time Remaining: " + "&c%timeremaining_hours% hours %timeremaining_minutes% minutes and %timeremaining_seconds% seconds"
                                )
                        ), Collections.singletonList("CRAFT:BREAD:64"), new Reward(new Item(XMaterial.DIAMOND, 1, "&c&lBaker Reward",
                                Arrays.asList(
                                        "&c&l Rewards",
                                        "&c&l* &75 Faction Crystals",
                                        "&c&l* &7$1000"
                                )), Collections.emptyList(), 1000, new ImmutableMap.Builder<String, Double>().put("Crystals", 5.00).build(), 0, 10, XSound.ENTITY_PLAYER_LEVELUP),
                                "%prefix% &7Baker mission Completed!\n" +
                                        "&c&l* &7+3 Faction Experience\n" +
                                        "&c&l* &7+5 Faction Crystals\n" +
                                        "&c&l* &7+1000 Money\n" +
                                        "&7/is rewards to redeem rewards"
                        )).build(), MissionType.DAILY))

                .put("miner", new Mission(ImmutableMap.<Integer, MissionData>builder()
                        .put(1, new MissionData(new Item(XMaterial.GOLD_ORE, 1, "&c&lMiner",
                                Arrays.asList(
                                        "&7Complete Faction Missions to gain rewards",
                                        "&7Which can be used to purchase Faction Upgrades",
                                        "",
                                        "&c&lObjectives:",
                                        "&c&l* &7Mine 15 Iron Ores: %progress_1%/15",
                                        "&c&l* &7Mine 30 Coal Ores: %progress_2%/30",
                                        "&c&l* &7Mine 1 Diamond Ore: %progress_3%/1",
                                        "",
                                        "&c&lRewards",
                                        "&c&l* &75 Faction Crystals",
                                        "&c&l* &7$1000",
                                        "",
                                        "&c&l * &7Time Remaining: " + "&c%timeremaining_hours% hours %timeremaining_minutes% minutes and %timeremaining_seconds% seconds"
                                )
                        ), Arrays.asList("MINE:IRON_ORE:15", "MINE:COAL_ORE:30", "MINE:DIAMOND_ORE:1"), new Reward(new Item(XMaterial.DIAMOND, 1, "&c&lMiner Reward",
                                Arrays.asList(
                                        "&c&l Rewards",
                                        "&c&l* &75 Faction Crystals",
                                        "&c&l* &7$1000"
                                )), Collections.emptyList(), 1000, new ImmutableMap.Builder<String, Double>().put("Crystals", 5.00).build(), 0, 10, XSound.ENTITY_PLAYER_LEVELUP),
                                "%prefix% &7Miner mission Completed!\n" +
                                        "&c&l* &7+3 Faction Experience\n" +
                                        "&c&l* &7+5 Faction Crystals\n" +
                                        "&c&l* &7+1000 Money\n" +
                                        "&7/is rewards to redeem rewards"
                        )).build(), MissionType.DAILY))

                .put("fisherman", new Mission(ImmutableMap.<Integer, MissionData>builder()
                        .put(1, new MissionData(new Item(XMaterial.FISHING_ROD, 1, "&c&lFisherman",
                                Arrays.asList(
                                        "&7Complete Faction Missions to gain rewards",
                                        "&7Which can be used to purchase Faction Upgrades",
                                        "",
                                        "&c&lObjectives:",
                                        "&c&l* &7Catch 10 Fish: %progress_1%/10",
                                        "",
                                        "&c&lRewards",
                                        "&c&l* &75 Faction Crystals",
                                        "&c&l* &7$1000",
                                        "",
                                        "&c&l * &7Time Remaining: " + "&c%timeremaining_hours% hours %timeremaining_minutes% minutes and %timeremaining_seconds% seconds"
                                )
                        ), Collections.singletonList("FISH:ANY:10"), new Reward(new Item(XMaterial.DIAMOND, 1, "&c&lFisherman Reward",
                                Arrays.asList(
                                        "&c&l Rewards",
                                        "&c&l* &75 Faction Crystals",
                                        "&c&l* &7$1000"
                                )), Collections.emptyList(), 1000, new ImmutableMap.Builder<String, Double>().put("Crystals", 5.00).build(), 0, 10, XSound.ENTITY_PLAYER_LEVELUP),
                                "%prefix% &7Fisherman mission Completed!\n" +
                                        "&c&l* &7+3 Faction Experience\n" +
                                        "&c&l* &7+5 Faction Crystals\n" +
                                        "&c&l* &7+1000 Money\n" +
                                        "&7/is rewards to redeem rewards"
                        )).build(), MissionType.DAILY))

                .put("blacksmith", new Mission(ImmutableMap.<Integer, MissionData>builder()
                        .put(1, new MissionData(new Item(XMaterial.IRON_INGOT, 1, "&c&lBlacksmith",
                                Arrays.asList(
                                        "&7Complete Faction Missions to gain rewards",
                                        "&7Which can be used to purchase Faction Upgrades",
                                        "",
                                        "&c&lObjectives:",
                                        "&c&l* &7Smelt 30 Iron Ores: %progress_1%/30",
                                        "&c&l* &7Smelt 15 Gold Ores: %progress_2%/15",
                                        "",
                                        "&c&lRewards",
                                        "&c&l* &75 Faction Crystals",
                                        "&c&l* &7$1000",
                                        "",
                                        "&c&l * &7Time Remaining: " + "&c%timeremaining_hours% hours %timeremaining_minutes% minutes and %timeremaining_seconds% seconds"
                                )), Arrays.asList("SMELT:" + (XMaterial.supports(17) ? XMaterial.RAW_IRON.name() : XMaterial.IRON_ORE.name()) + ":30", "SMELT:" + (XMaterial.supports(17) ? XMaterial.RAW_GOLD.name() : XMaterial.GOLD_ORE.name()) + ":15"), new Reward(new Item(XMaterial.DIAMOND, 1, "&c&lBlacksmith Reward",
                                Arrays.asList(
                                        "&c&l Rewards",
                                        "&c&l* &75 Faction Crystals",
                                        "&c&l* &7$1000"
                                )), Collections.emptyList(), 1000, new ImmutableMap.Builder<String, Double>().put("Crystals", 5.00).build(), 0, 10, XSound.ENTITY_PLAYER_LEVELUP),
                                "%prefix% &7Blacksmith mission Completed!\n" +
                                        "&c&l* &7+3 Faction Experience\n" +
                                        "&c&l* &7+5 Faction Crystals\n" +
                                        "&c&l* &7+1000 Money\n" +
                                        "&7/is rewards to redeem rewards"
                        )).build(), MissionType.DAILY))

                .put("brewer", new Mission(ImmutableMap.<Integer, MissionData>builder()
                        .put(1, new MissionData(new Item(XMaterial.POTION, 1, "&c&lPotion Brewer",
                                Arrays.asList(
                                        "&7Complete Faction Missions to gain rewards",
                                        "&7Which can be used to purchase Faction Upgrades",
                                        "",
                                        "&c&lObjectives:",
                                        "&c&l* &7Brew 3 Speed II Potions: %progress_1%/3",
                                        "&c&l* &7Brew 3 Strength II Potions: %progress_2%/3",
                                        "",
                                        "&c&lRewards",
                                        "&c&l* &75 Faction Crystals",
                                        "&c&l* &7$1000",
                                        "",
                                        "&c&l * &7Time Remaining: " + "&c%timeremaining_hours% hours %timeremaining_minutes% minutes and %timeremaining_seconds% seconds"
                                )
                        ), Arrays.asList("BREW:SPEED:2:3", "BREW:STRENGTH:2:3"), new Reward(new Item(XMaterial.DIAMOND, 1, "&c&lPotionBrewer Reward",
                                Arrays.asList(
                                        "&c&l Rewards",
                                        "&c&l* &75 Faction Crystals",
                                        "&c&l* &7$1000"
                                )), Collections.emptyList(), 1000, new ImmutableMap.Builder<String, Double>().put("Crystals", 5.00).build(), 0, 10, XSound.ENTITY_PLAYER_LEVELUP),
                                "%prefix% &7Potion Brewer mission Completed!\n" +
                                        "&c&l* &7+3 Faction Experience\n" +
                                        "&c&l* &7+5 Faction Crystals\n" +
                                        "&c&l* &7+1000 Money\n" +
                                        "&7/is rewards to redeem rewards"
                        )).build(), MissionType.DAILY))

                .put("mine_oak", new Mission(ImmutableMap.<Integer, MissionData>builder()
                        .put(1, new MissionData(new Item(XMaterial.OAK_LOG, 0, 1, "&c&lMine 10 Logs",
                                Arrays.asList(
                                        "&7Complete Faction Missions to gain rewards",
                                        "&7Which can be used to purchase Faction Upgrades",
                                        "",
                                        "&c&lObjectives:",
                                        "&c&l* &7Mine 10 logs: %progress_1%/10",
                                        "",
                                        "&c&lRewards",
                                        "&c&l* &75 Faction Crystals",
                                        "&c&l* &7$1000"
                                )
                        ), Collections.singletonList("MINE:LOGS:10"), new Reward(new Item(XMaterial.DIAMOND, 1, "&c&lPotionBrewer Reward",
                                Arrays.asList(
                                        "&c&l Rewards",
                                        "&c&l* &75 Faction Crystals",
                                        "&c&l* &7$1000"
                                )), Collections.emptyList(), 1000, new ImmutableMap.Builder<String, Double>().put("Crystals", 5.00).build(), 0, 10, XSound.ENTITY_PLAYER_LEVELUP),
                                "%prefix% &7Mission Completed!\n" +
                                        "&c&l* &7+3 Faction Experience\n" +
                                        "&c&l* &7+5 Faction Crystals\n" +
                                        "&c&l* &7+1000 Money\n" +
                                        "&7/is rewards to redeem rewards"
                        ))
                        .put(2, new MissionData(new Item(XMaterial.OAK_LOG, 0, 1, "&c&lMine 100 Logs",
                                Arrays.asList(
                                        "&7Complete Faction Missions to gain rewards",
                                        "&7Which can be used to purchase Faction Upgrades",
                                        "",
                                        "&c&lObjectives:",
                                        "&c&l* &7Mine 100 logs: %progress_1%/100",
                                        "",
                                        "&c&lRewards",
                                        "&c&l* &75 Faction Crystals",
                                        "&c&l* &7$1000"
                                )
                        ), Collections.singletonList("MINE:LOGS:100"), new Reward(new Item(XMaterial.DIAMOND, 1, "&c&lPotionBrewer Reward",
                                Arrays.asList(
                                        "&c&l Rewards",
                                        "&c&l* &75 Faction Crystals",
                                        "&c&l* &7$1000"
                                )), Collections.emptyList(), 1000, new ImmutableMap.Builder<String, Double>().put("Crystals", 5.00).build(), 0, 10, XSound.ENTITY_PLAYER_LEVELUP),
                                "%prefix% &7Mission Completed!\n" +
                                        "&c&l* &7+3 Faction Experience\n" +
                                        "&c&l* &7+5 Faction Crystals\n" +
                                        "&c&l* &7+1000 Money\n" +
                                        "&7/is rewards to redeem rewards"
                        ))
                        .put(3, new MissionData(new Item(XMaterial.OAK_LOG, 0, 1, "&c&lMine 1000 Logs",
                                Arrays.asList(
                                        "&7Complete Faction Missions to gain rewards",
                                        "&7Which can be used to purchase Faction Upgrades",
                                        "",
                                        "&c&lObjectives:",
                                        "&c&l* &7Mine 1000 logs: %progress_1%/1000",
                                        "",
                                        "&c&lRewards",
                                        "&c&l* &75 Faction Crystals",
                                        "&c&l* &7$1000"
                                )
                        ), Collections.singletonList("MINE:LOGS:1000"), new Reward(new Item(XMaterial.DIAMOND, 1, "&c&lPotionBrewer Reward",
                                Arrays.asList(
                                        "&c&l Rewards",
                                        "&c&l* &75 Faction Crystals",
                                        "&c&l* &7$1000"
                                )), Collections.emptyList(), 1000, new ImmutableMap.Builder<String, Double>().put("Crystals", 5.00).build(), 0, 10, XSound.ENTITY_PLAYER_LEVELUP),
                                "%prefix% &7Mission Completed!\n" +
                                        "&c&l* &7+3 Faction Experience\n" +
                                        "&c&l* &7+5 Faction Crystals\n" +
                                        "&c&l* &7+1000 Money\n" +
                                        "&7/is rewards to redeem rewards"
                        ))
                        .build(), MissionType.ONCE))
                .build();
    }

}
