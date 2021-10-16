package com.iridium.iridiumfactions.managers;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import com.iridium.iridiumfactions.utils.PlayerUtils;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class UserManager {
    /**
     * Gets a {@link User}'s info. Creates one if he doesn't exist.
     *
     * @param offlinePlayer The player who's data should be fetched
     * @return The user data
     */
    public @NotNull User getUser(@NotNull OfflinePlayer offlinePlayer) {
        Optional<User> userOptional = getUserByUUID(offlinePlayer.getUniqueId());
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            Optional<String> name = Optional.ofNullable(offlinePlayer.getName());
            User user = new User(offlinePlayer.getUniqueId(), name.orElse(""));
            IridiumFactions.getInstance().getDatabaseManager().getUserTableManager().addEntry(user);
            return user;
        }
    }

    /**
     * Finds an User by his {@link UUID}.
     *
     * @param uuid The uuid of the onlyForPlayers
     * @return the User class of the onlyForPlayers
     */
    public Optional<User> getUserByUUID(@NotNull UUID uuid) {
        return IridiumFactions.getInstance().getDatabaseManager().getUserTableManager().getUser(uuid);
    }

    public void sendFactionMap(Player player) {
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);
        BlockFace direction = PlayerUtils.getDirection(player);
        int mapWidth = 50;
        int mapHeight = 10;
        char[] mapChars = "\\/#$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz?".toCharArray();
        int currentChar = 0;
        HashMap<Integer, Character> factionCharacterMap = new HashMap<>();
        Chunk centerChunk = player.getLocation().getChunk();
        World world = centerChunk.getWorld();
        Optional<Faction> currentFaction = IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(centerChunk);
        String factionName = (currentFaction.isPresent() && user.getFactionID() == currentFaction.get().getId() ? "&a" : "&7") + currentFaction.map(Faction::getName).orElse("&2Wilderness");
        player.sendMessage(StringUtils.color(StringUtils.getCenteredMessage("&8[ &c(" + centerChunk.getX() + ", " + centerChunk.getZ() + ") " + factionName + " &8]", "&8&m ")));
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
                Optional<Faction> faction = IridiumFactions.getInstance().getFactionManager().getFactionViaChunk(world, x, z);
                if (centerChunk.getX() == x && centerChunk.getZ() == z) {
                    stringBuilder.append("&b+");
                } else {
                    if (faction.isPresent()) {
                        if (!factionCharacterMap.containsKey(faction.get().getId())) {
                            char character = mapChars[currentChar];
                            factionCharacterMap.put(faction.get().getId(), character);
                            if (currentChar + 1 < mapChars.length) {
                                currentChar++;
                            } else {
                                currentChar = 0;
                            }
                        }
                        stringBuilder.append(user.getFactionID() == faction.get().getId() ? "&a" : "&7").append(factionCharacterMap.get(faction.get().getId()));
                    } else {
                        stringBuilder.append("&7-");
                    }
                }
            }
            player.sendMessage(StringUtils.color(stringBuilder.toString()));
        }
    }
}
