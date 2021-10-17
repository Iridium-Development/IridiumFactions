package com.iridium.iridiumfactions.configs;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.fasterxml.annotation.JsonIgnoreProperties;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.Permission;

import java.util.Arrays;

/**
 * The Factions permission configuration used by IridiumSkyblock (permissions.yml).
 * Is deserialized automatically on plugin startup and reload.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Permissions {

    public String allowed = "&a&lALLOWED";
    public String denied = "&c&lDENIED";
    public Permission blockBreak = new Permission(new Item(XMaterial.DIAMOND_PICKAXE, 10, 1, "&bBreak Blocks", Arrays.asList("&7Grant the ability to break any blocks in your Faction.", "", "&b&lPermission", "%permission%")), 1, FactionRank.MEMBER);
    public Permission blockPlace = new Permission(new Item(XMaterial.COBBLESTONE, 11, 1, "&bPlace Blocks", Arrays.asList("&7Grant the ability to place any blocks in your Faction.", "", "&b&lPermission", "%permission%")), 1, FactionRank.MEMBER);
    public Permission bucket = new Permission(new Item(XMaterial.BUCKET, 12, 1, "&bUse Buckets", Arrays.asList("&7Grant the ability to fill and empty buckets in your Faction.", "", "&b&lPermission", "%permission%")), 1, FactionRank.MEMBER);
    public Permission changePermissions = new Permission(new Item(XMaterial.SUNFLOWER, 13, 1, "&bChange Permissions", Arrays.asList("&7Grant the ability to edit Faction permissions.", "", "&b&lPermission", "%permission%")), 1, FactionRank.CO_OWNER);
    public Permission claim = new Permission(new Item(XMaterial.IRON_AXE, 14, 1, "&bClaim Land", Arrays.asList("&7Grant the ability to claim land for your faction.", "", "&b&lPermission", "%permission%")), 1, FactionRank.MODERATOR);
    public Permission demote = new Permission(new Item(XMaterial.PLAYER_HEAD, 15, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmU5YWU3YTRiZTY1ZmNiYWVlNjUxODEzODlhMmY3ZDQ3ZTJlMzI2ZGI1OWVhM2ViNzg5YTkyYzg1ZWE0NiJ9fX0=", 1, "&bDemote Users", Arrays.asList("&7Grant the ability to demote users in your Faction.", "", "&b&lPermission", "%permission%")), 1, FactionRank.CO_OWNER);
    public Permission description = new Permission(new Item(XMaterial.WRITABLE_BOOK, 16, 1, "&bChange Description", Arrays.asList("&7Grant the ability to change your Faction description.", "", "&b&lPermission", "%permission%")), 1, FactionRank.CO_OWNER);
    public Permission doors = new Permission(new Item(XMaterial.OAK_DOOR, 19, 1, "&bUse Doors", Arrays.asList("&7Grant the ability to use doors or trapdoors in your Faction.", "", "&b&lPermission", "%permission%")), 1, FactionRank.MEMBER);
    public Permission invite = new Permission(new Item(XMaterial.DIAMOND, 20, 1, "&bInvite Users", Arrays.asList("&7Grant the ability to invite Faction members.", "", "&b&lPermission", "%permission%")), 1, FactionRank.MODERATOR);
    public Permission kick = new Permission(new Item(XMaterial.IRON_BOOTS, 21, 1, "&bKick Users", Arrays.asList("&7Grant the ability to kick Faction members.", "", "&b&lPermission", "%permission%")), 1, FactionRank.MODERATOR);
    public Permission killMobs = new Permission(new Item(XMaterial.DIAMOND_SWORD, 22, 1, "&bKill Mobs", Arrays.asList("&7Grant the ability to kill mobs in your Faction.", "", "&b&lPermission", "%permission%")), 1, FactionRank.MEMBER);
    public Permission openContainers = new Permission(new Item(XMaterial.CHEST, 23, 1, "&bOpen Containers", Arrays.asList("&7Grant the ability to open containers in your Faction.", "", "&b&lPermission", "%permission%")), 1, FactionRank.MEMBER);
    public Permission promote = new Permission(new Item(XMaterial.PLAYER_HEAD, 24, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2Y0NmFiYWQ5MjRiMjIzNzJiYzk2NmE2ZDUxN2QyZjFiOGI1N2ZkZDI2MmI0ZTA0ZjQ4MzUyZTY4M2ZmZjkyIn19fQ==", 1, "&bPromote Users", Arrays.asList("&7Grant the ability to promote users in your Faction.", "", "&b&lPermission", "%permission%")), 1, FactionRank.CO_OWNER);
    public Permission redstone = new Permission(new Item(XMaterial.REDSTONE, 25, 1, "&bUse Redstone", Arrays.asList("&7Grant the ability to use buttons, levels, or pressure plates in your Faction.", "", "&b&lPermission", "%permission%")), 1, FactionRank.MEMBER);
    public Permission rename = new Permission(new Item(XMaterial.PAPER, 28, 1, "&bRename Faction", Arrays.asList("&7Grant the ability to rename your Faction.", "", "&b&lPermission", "%permission%")), 1, FactionRank.CO_OWNER);
    public Permission setHome = new Permission(new Item(XMaterial.WHITE_BED, 29, 1, "&bFaction Home", Arrays.asList("&7Grant the ability to change your Faction home.", "", "&b&lPermission", "%permission%")), 1, FactionRank.MODERATOR);
    public Permission spawners = new Permission(new Item(XMaterial.SPAWNER, 30, 1, "&bBreak Spawners", Arrays.asList("&7Grant the ability to mine spawners in your Faction.", "", "&b&lPermission", "%permission%")), 1, FactionRank.MEMBER);
    public Permission unclaim = new Permission(new Item(XMaterial.GRASS_BLOCK, 31, 1, "&bUn-Claim Land", Arrays.asList("&7Grant the ability to unclaim your Faction land.", "", "&b&lPermission", "%permission%")), 1, FactionRank.MODERATOR);
}
