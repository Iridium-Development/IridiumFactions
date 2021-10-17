package com.iridium.iridiumfactions.configs;

import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumcore.Background;
import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.fasterxml.annotation.JsonIgnore;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumfactions.configs.inventories.ConfirmationInventoryConfig;
import com.iridium.iridiumfactions.configs.inventories.FactionRanksInventoryConfig;
import com.iridium.iridiumfactions.configs.inventories.NoItemGUI;
import com.iridium.iridiumfactions.configs.inventories.SingleItemGUI;

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

    public SingleItemGUI membersGUI = new SingleItemGUI(27, "&7Faction Members", background1, new Item(XMaterial.PLAYER_HEAD, 0, 1, "&b&l%player_name%", "%player_name%", Arrays.asList(
            "&7Joined: %player_join%",
            "&7Rank: %player_rank%",
            "",
            "&b&l[!] &7Right Click to promote",
            "&b&l[!] &7Left click to demote/kick"
    )));

    public FactionRanksInventoryConfig factionRanksGUI = new FactionRanksInventoryConfig(27, "&7Faction Permissions", background1,
            new Item(XMaterial.DIAMOND_AXE, 14, 1, "&b&lOwner", Collections.emptyList()),
            new Item(XMaterial.GOLDEN_AXE, 13, 1, "&b&lCo-Owner", Collections.emptyList()),
            new Item(XMaterial.IRON_AXE, 12, 1, "&b&lModerator", Collections.emptyList()),
            new Item(XMaterial.STONE_AXE, 11, 1, "&b&lMember", Collections.emptyList()),
            new Item(XMaterial.WOODEN_AXE, 10, 1, "&b&lTruce", Collections.emptyList()),
            new Item(XMaterial.GOLDEN_APPLE, 15, 1, "&b&lAlly", Collections.emptyList()),
            new Item(XMaterial.BARRIER, 16, 1, "&b&lEnemy", Collections.emptyList())
    );

    public NoItemGUI factionPermissionsGUI = new NoItemGUI(54, "&7Faction Permissions", background1);

    public SingleItemGUI invitesGUI = new SingleItemGUI(27, "&7Faction Invites", background1, new Item(XMaterial.PLAYER_HEAD, 0, 1, "&b&l%player_name%", "%player_name%", Arrays.asList(
            "",
            "&b&l[!] &7Left click to uninvite"
    )));

    public ConfirmationInventoryConfig confirmationGUI = new ConfirmationInventoryConfig(27, "&7Are you sure?", background2, new Item(XMaterial.GREEN_STAINED_GLASS_PANE, 15, 1, "&a&lYes", Collections.emptyList()), new Item(XMaterial.RED_STAINED_GLASS_PANE, 11, 1, "&c&lNo", Collections.emptyList()));

    public Item nextPage = new Item(XMaterial.LIME_STAINED_GLASS_PANE, 1, "&a&lNext Page", Collections.emptyList());
    public Item previousPage = new Item(XMaterial.RED_STAINED_GLASS_PANE, 1, "&c&lPrevious Page", Collections.emptyList());

}
