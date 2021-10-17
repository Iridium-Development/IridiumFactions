package com.iridium.iridiumfactions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PermissionType {
    BLOCK_BREAK("blockBreak"),
    BLOCK_PLACE("blockPlace"),
    BUCKET("bucket"),
    CHANGE_PERMISSIONS("changePermissions"),
    CLAIM("claim"),
    DEMOTE("demote"),
    DESCRIPTION("description"),
    DOORS("doors"),
    INVITE("invite"),
    KICK("kick"),
    KILL_MOBS("killMobs"),
    OPEN_CONTAINERS("openContainers"),
    PROMOTE("promote"),
    REDSTONE("redstone"),
    RENAME("rename"),
    SETHOME("sethome"),
    SPAWNERS("spawners"),
    UNCLAIM("unclaim");


    private final String permissionKey;
}
