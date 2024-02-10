package com.iridium.iridiumfactions.utils;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class PlayerUtils {

    public static BlockFace getDirection(Player player) {
        return radial[Math.round(player.getLocation().getYaw() / 45f) & 0x7].getOppositeFace();
    }

    private static final BlockFace[] radial = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};
}
