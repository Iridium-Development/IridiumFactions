package com.iridium.iridiumfactions.configs;

import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumcore.Background;
import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.fasterxml.annotation.JsonIgnore;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumteams.configs.inventories.InventoryConfig;

import java.util.Collections;

public class Inventories extends com.iridium.iridiumteams.configs.Inventories {

    @JsonIgnore
    private final Background background1 = new Background(ImmutableMap.<Integer, Item>builder().build());
    @JsonIgnore
    private final Background background2 = new Background(ImmutableMap.<Integer, Item>builder()
            .put(9, new Item(XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE, 1, " ", Collections.emptyList()))
            .put(10, new Item(XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE, 1, " ", Collections.emptyList()))
            .put(11, new Item(XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE, 1, " ", Collections.emptyList()))
            .put(12, new Item(XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE, 1, " ", Collections.emptyList()))
            .put(13, new Item(XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE, 1, " ", Collections.emptyList()))
            .put(14, new Item(XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE, 1, " ", Collections.emptyList()))
            .put(15, new Item(XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE, 1, " ", Collections.emptyList()))
            .put(16, new Item(XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE, 1, " ", Collections.emptyList()))
            .put(17, new Item(XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE, 1, " ", Collections.emptyList()))
            .build());

    public InventoryConfig factionMenu = new InventoryConfig(45, "&7Faction Menu", background1, ImmutableMap.<String, Item>builder()
            .put("f boosters", new Item(XMaterial.EXPERIENCE_BOTTLE, 23, 1, "&c&lFaction Boosters", Collections.singletonList("&7View your faction boosters")))
            .put("f home", new Item(XMaterial.WHITE_BED, 13, 1, "&c&lFaction Home", Collections.singletonList("&7Teleport to your faction home")))
            .put("f members", new Item(XMaterial.PLAYER_HEAD, 14, 1, "&c&lFaction Members", "Peaches_MLG", Collections.singletonList("&7View your faction members")))
            .put("f warps", new Item(XMaterial.END_PORTAL_FRAME, 20, 1, "&c&lFaction Warps", Collections.singletonList("&7View your faction warps")))
            .put("f upgrades", new Item(XMaterial.DIAMOND, 21, 1, "&c&lFaction Upgrades", Collections.singletonList("&7View your faction upgrades")))
            .put("f missions", new Item(XMaterial.IRON_SWORD, 22, 1, "&c&lFaction Missions", Collections.singletonList("&7View your faction missions")))
            .put("f bank", new Item(XMaterial.PLAYER_HEAD, 30, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM4MWM1MjlkNTJlMDNjZDc0YzNiZjM4YmI2YmEzZmRlMTMzN2FlOWJmNTAzMzJmYWE4ODllMGEyOGU4MDgxZiJ9fX0", 1, "&c&lFaction Bank", Collections.singletonList("&7View your faction bank")))
            .put("f permissions", new Item(XMaterial.WRITABLE_BOOK, 31, 1, "&c&lFaction Permissions", Collections.singletonList("&7View your faction permissions")))
            .put("f invites", new Item(XMaterial.NAME_TAG, 32, 1, "&c&lFaction Invites", Collections.singletonList("&7View your faction invites")))
            .put("f delete", new Item(XMaterial.BARRIER, 44, 1, "&c&lDelete Faction", Collections.singletonList("&7Delete your faction")))
            .build()
    );

    public Inventories() {
        super("Faction", "&c");
        missionTypeSelectorGUI.weekly.enabled = false;
    }
}
