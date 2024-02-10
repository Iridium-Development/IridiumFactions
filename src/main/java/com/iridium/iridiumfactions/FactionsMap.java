package com.iridium.iridiumfactions;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import com.iridium.iridiumfactions.utils.PlayerUtils;
import org.bukkit.Chunk;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class FactionsMap {
    private final Chunk centerChunk;
    private final User user;
    private final Player player;

    private final Map<Integer, Character> factionCharacterMap = new HashMap<>();
    private int currentChar = 0;

    public FactionsMap(Player player) {
        this.centerChunk = player.getLocation().getChunk();
        this.user = IridiumFactions.getInstance().getUserManager().getUser(player);
        this.player = player;
    }

    public char getFactionCharacter(Faction faction) {
        char[] mapChars = IridiumFactions.getInstance().getConfiguration().mapChars;
        if (!factionCharacterMap.containsKey(faction.getId())) {
            factionCharacterMap.put(faction.getId(), mapChars[currentChar]);
            if (currentChar + 1 < mapChars.length) {
                currentChar++;
            } else {
                currentChar = 0;
            }
        }
        return factionCharacterMap.get(faction.getId());
    }

    public String getHeader() {
        Faction faction = IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(centerChunk);
        return StringUtils.color(StringUtils.getCenteredMessage(IridiumFactions.getInstance().getConfiguration().mapTitle
                        .replace("%chunk_x%", String.valueOf(centerChunk.getX()))
                        .replace("%chunk_z%", String.valueOf(centerChunk.getZ()))
                        .replace("%faction%", IridiumFactions.getInstance().getFactionManager().getFactionRelationship(user, faction).getColor() + faction.getName())
                , IridiumFactions.getInstance().getConfiguration().mapTitleFiller));
    }

    public void sendMap() {
        BlockFace direction = PlayerUtils.getDirection(player);
        int mapWidth = IridiumFactions.getInstance().getConfiguration().mapWidth;
        int mapHeight = IridiumFactions.getInstance().getConfiguration().mapHeight;

        player.sendMessage(getHeader());

        for (int z = centerChunk.getZ() - (mapHeight / 2); z < centerChunk.getZ() + (mapHeight / 2); z++) {
            boolean buffer = z < centerChunk.getZ() - (mapHeight / 2) + 3;
            StringBuilder stringBuilder = new StringBuilder();
            if (z == centerChunk.getZ() - (mapHeight / 2)) {
                stringBuilder.append(direction == BlockFace.NORTH_WEST ? "&c" : "&e").append("\\");
                stringBuilder.append(direction == BlockFace.NORTH ? "&c" : "&e").append("N");
                stringBuilder.append(direction == BlockFace.NORTH_EAST ? "&c" : "&e").append("/");
            } else if (z == centerChunk.getZ() - (mapHeight / 2) + 1) {
                stringBuilder.append(direction == BlockFace.WEST ? "&c" : "&e").append("W");
                stringBuilder.append("&e+");
                stringBuilder.append(direction == BlockFace.EAST ? "&c" : "&e").append("E");
            } else if (z == centerChunk.getZ() - (mapHeight / 2) + 2) {
                stringBuilder.append(direction == BlockFace.SOUTH_WEST ? "&c" : "&e").append("/");
                stringBuilder.append(direction == BlockFace.SOUTH ? "&c" : "&e").append("S");
                stringBuilder.append(direction == BlockFace.SOUTH_EAST ? "&c" : "&e").append("\\");
            }

            for (int x = centerChunk.getX() - (mapWidth / 2) + (buffer ? 3 : 0); x < centerChunk.getX() + (mapWidth / 2); x++) {
                Faction faction = IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(centerChunk.getWorld(), x, z);
                if (centerChunk.getX() == x && centerChunk.getZ() == z) {
                    stringBuilder.append("&b+");
                } else {
                    if (faction.getFactionType() != FactionType.WILDERNESS) {
                        stringBuilder.append(IridiumFactions.getInstance().getFactionManager().getFactionRelationship(user, faction).getColor()).append(getFactionCharacter(faction));
                    } else {
                        stringBuilder.append("&7-");
                    }
                }
            }

            player.sendMessage(StringUtils.color(stringBuilder.toString()));
        }
    }
}
