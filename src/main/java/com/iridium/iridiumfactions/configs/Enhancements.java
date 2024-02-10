package com.iridium.iridiumfactions.configs;

import com.google.common.collect.ImmutableMap;

public class Enhancements extends com.iridium.iridiumteams.configs.Enhancements {

    public Enhancements() {
        super("&c");
        this.membersEnhancement.item.slot = 10;
        this.warpsEnhancement.item.slot = 12;
        this.potionEnhancements.get("haste").item.slot = 14;
        this.potionEnhancements.get("speed").item.slot = 15;
        this.potionEnhancements.get("jump").item.slot = 16;

        this.farmingEnhancement.levels.forEach((integer, farmingEnhancementData) -> farmingEnhancementData.bankCosts = new ImmutableMap.Builder<String, Double>().put("Crystals", 5.00).build());
        this.spawnerEnhancement.levels.forEach((integer, spawnerEnhancementData) -> spawnerEnhancementData.bankCosts = new ImmutableMap.Builder<String, Double>().put("Crystals", 5.00).build());
        this.experienceEnhancement.levels.forEach((integer, experienceEnhancementData) -> experienceEnhancementData.bankCosts = new ImmutableMap.Builder<String, Double>().put("Crystals", 5.00).build());
        this.flightEnhancement.levels.forEach((integer, flightEnhancementData) -> flightEnhancementData.bankCosts = new ImmutableMap.Builder<String, Double>().put("Crystals", 5.00).build());
        this.membersEnhancement.levels.forEach((integer, membersEnhancementData) -> membersEnhancementData.bankCosts = new ImmutableMap.Builder<String, Double>().put("Crystals", 5.00).build());
        this.warpsEnhancement.levels.forEach((integer, warpsEnhancementData) -> warpsEnhancementData.bankCosts = new ImmutableMap.Builder<String, Double>().put("Crystals", 5.00).build());
        this.potionEnhancements.get("haste").levels.forEach((integer, potionEnhancementData) -> potionEnhancementData.bankCosts = new ImmutableMap.Builder<String, Double>().put("Crystals", 5.00).build());
        this.potionEnhancements.get("speed").levels.forEach((integer, potionEnhancementData) -> potionEnhancementData.bankCosts = new ImmutableMap.Builder<String, Double>().put("Crystals", 5.00).build());
        this.potionEnhancements.get("jump").levels.forEach((integer, potionEnhancementData) -> potionEnhancementData.bankCosts = new ImmutableMap.Builder<String, Double>().put("Crystals", 5.00).build());
    }
}
