package com.iridium.iridiumfactions.configs;

import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumcore.Background;
import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.fasterxml.annotation.JsonIgnore;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumfactions.configs.inventories.*;

import java.util.Arrays;
import java.util.Collections;

public class Inventories {
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

    public NoItemGUI upgradesGUI = new NoItemGUI(27, "&7Faction Upgrades", background2);

    public SingleItemGUI membersGUI = new SingleItemGUI(0, "&7Faction Members", background1, new Item(XMaterial.PLAYER_HEAD, 0, 1, "&c&l%player_name%", "%player_name%", Arrays.asList(
            "&7Joined: %player_join%",
            "&7Rank: %player_rank%",
            "",
            "&c&l[!] &7Right Click to promote",
            "&c&l[!] &7Left click to demote/kick"
    )));

    public SingleItemGUI warpsGUI = new SingleItemGUI(27, "&7%faction_name%'s Faction Warps", background2, new Item(
            XMaterial.GREEN_STAINED_GLASS_PANE, 1, "&c&l%warp_name%",
            Arrays.asList(
                    "&7%description%",
                    "",
                    "&c&l[!] &cLeft Click to Teleport",
                    "&c&l[!] &cRight Click to Delete"
            )));

    public FactionRanksInventoryConfig factionRanksGUI = new FactionRanksInventoryConfig(27, "&7Faction Permissions", background1,
            new Item(XMaterial.DIAMOND_AXE, 14, 1, "&c&lOwner", Collections.emptyList()),
            new Item(XMaterial.GOLDEN_AXE, 13, 1, "&c&lCo-Owner", Collections.emptyList()),
            new Item(XMaterial.IRON_AXE, 12, 1, "&c&lModerator", Collections.emptyList()),
            new Item(XMaterial.STONE_AXE, 11, 1, "&c&lMember", Collections.emptyList()),
            new Item(XMaterial.WOODEN_AXE, 10, 1, "&c&lTruce", Collections.emptyList()),
            new Item(XMaterial.GOLDEN_APPLE, 15, 1, "&c&lAlly", Collections.emptyList()),
            new Item(XMaterial.BARRIER, 16, 1, "&c&lEnemy", Collections.emptyList())
    );

    public FactionTopInventoryConfig factionTopGUI = new FactionTopInventoryConfig(27, "&7Top Factions", background1, new Item(XMaterial.PLAYER_HEAD, 1, "&c&lFaction Owner: &f%faction_owner% &7(#%faction_rank%)", "%faction_owner%", Arrays.asList(
            "",
            "&c&l * &7Faction Name: &c%faction_name%",
            "&c&l * &7Faction Rank: &c%faction_rank%",
            "&c&l * &7Faction Value: &c%faction_value%",
            "&c&l * &7Faction Reductions: &c-%faction_strike_reduction%%",
            "&c&l * &7Netherite Blocks: &c%NETHERITE_BLOCK_AMOUNT%",
            "&c&l * &7Emerald Blocks: &c%EMERALD_BLOCK_AMOUNT%",
            "&c&l * &7Diamond Blocks: &c%DIAMOND_BLOCK_AMOUNT%",
            "&c&l * &7Gold Blocks: &c%GOLD_BLOCK_AMOUNT%",
            "&c&l * &7Iron Blocks: &c%IRON_BLOCK_AMOUNT%",
            "&c&l * &7Hopper Blocks: &c%HOPPER_AMOUNT%",
            "&c&l * &7Beacon Blocks: &c%BEACON_AMOUNT%"
    )), new Item(XMaterial.BARRIER, 1, " ", Collections.emptyList()));

    public NoItemGUI factionPermissionsGUI = new NoItemGUI(54, "&7Faction Permissions", background1);

    public NoItemGUI boostersGUI = new NoItemGUI(27, "&7Faction Boosters", background2);

    public SingleItemGUI invitesGUI = new SingleItemGUI(0, "&7Faction Invites", background1, new Item(XMaterial.PLAYER_HEAD, 0, 1, "&c&l%player_name%", "%player_name%", Arrays.asList(
            "",
            "&c&l[!] &7Left click to uninvite"
    )));

    public SingleItemGUI relationshipsGUI = new SingleItemGUI(0, "&7Faction Relationships", background1, new Item(XMaterial.PLAYER_HEAD, 0, 1, "&c&l%faction_name%", "%faction_owner%", Arrays.asList(
            "&7Faction Name: %faction_name%",
            "&7Relationship Type: %relationship_type%",
            "",
            "&c&l[!] &7Left click to set relationship to truce"
    )));

    public SingleItemGUI strikesGUI = new SingleItemGUI(0, "&7Faction Strikes", background1, new Item(XMaterial.PLAYER_HEAD, 0, 1, "&c&l%reason%", "%player_name%", Arrays.asList(
            "&7Punished By: %player_name%",
            "&7Reason: %reason%"
    )));
    public NoItemGUI bankGUI = new NoItemGUI(27, "&7Faction Bank", background2);

    public ConfirmationInventoryConfig confirmationGUI = new ConfirmationInventoryConfig(27, "&7Are you sure?", background2, new Item(XMaterial.GREEN_STAINED_GLASS_PANE, 15, 1, "&a&lYes", Collections.emptyList()), new Item(XMaterial.RED_STAINED_GLASS_PANE, 11, 1, "&c&lNo", Collections.emptyList()));

    public InventoryConfig factionMenu = new InventoryConfig(45, "&7Faction Menu", background1, ImmutableMap.<String, Item>builder()
            .put("f chest", new Item(XMaterial.CHEST, 12, 1, "&c&lFaction Chest", Collections.singletonList("&7Open your faction's chest")))
            .put("f boosters", new Item(XMaterial.EXPERIENCE_BOTTLE, 23, 1, "&c&lFaction Boosters", Collections.singletonList("&7View your faction boosters")))
            .put("f strike", new Item(XMaterial.RED_DYE, 24, 1, "&c&lFaction Strikes", Collections.singletonList("&7View your faction strikes")))
            .put("f home", new Item(XMaterial.WHITE_BED, 13, 1, "&c&lFaction Home", Collections.singletonList("&7Teleport to your faction home")))
            .put("f members", new Item(XMaterial.PLAYER_HEAD, 14, 1, "&c&lFaction Members", "Peaches_MLG", Collections.singletonList("&7View your faction members")))
            .put("f warps", new Item(XMaterial.END_PORTAL_FRAME, 20, 1, "&c&lFaction Warps", Collections.singletonList("&7View your faction warps")))
            .put("f upgrade", new Item(XMaterial.DIAMOND, 21, 1, "&c&lFaction Upgrades", Collections.singletonList("&7View your faction upgrades")))
            .put("f missions", new Item(XMaterial.IRON_SWORD, 22, 1, "&c&lFaction Missions", Collections.singletonList("&7View your faction missions")))
            .put("f bank", new Item(XMaterial.PLAYER_HEAD, 30, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM4MWM1MjlkNTJlMDNjZDc0YzNiZjM4YmI2YmEzZmRlMTMzN2FlOWJmNTAzMzJmYWE4ODllMGEyOGU4MDgxZiJ9fX0", 1, "&c&lFaction Bank", Collections.singletonList("&7View your faction bank")))
            .put("f permissions", new Item(XMaterial.WRITABLE_BOOK, 31, 1, "&c&lFaction Permissions", Collections.singletonList("&7View your faction permissions")))
            .put("f delete", new Item(XMaterial.BARRIER, 44, 1, "&c&lDelete Faction", Collections.singletonList("&7Delete your faction")))
            .put("f invites", new Item(XMaterial.PAPER, 32, 1, "&c&lFaction Invites", Collections.singletonList("&7View your faction invites")))
            .build()
    );

    public Item nextPage = new Item(XMaterial.LIME_STAINED_GLASS_PANE, 1, "&a&lNext Page", Collections.emptyList());
    public Item previousPage = new Item(XMaterial.RED_STAINED_GLASS_PANE, 1, "&c&lPrevious Page", Collections.emptyList());

}
