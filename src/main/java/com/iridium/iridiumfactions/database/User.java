package com.iridium.iridiumfactions.database;

import com.iridium.iridiumfactions.FactionRank;
import com.iridium.iridiumfactions.IridiumFactions;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Represents a User of IridiumFactions.
 */
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "users")
public final class User {

    @DatabaseField(columnName = "uuid", canBeNull = false, id = true)
    private @NotNull UUID uuid;

    @DatabaseField(columnName = "name", canBeNull = false)
    private @NotNull String name;

    @Setter(AccessLevel.PRIVATE)
    @DatabaseField(columnName = "faction_id", canBeNull = false)
    private int factionID;

    @DatabaseField(columnName = "join_time")
    private long joinTime;

    @DatabaseField(columnName = "power", canBeNull = false)
    private double power;

    @DatabaseField(columnName = "faction_rank", canBeNull = false)
    private @NotNull FactionRank factionRank;

    private boolean bypassing = false;

    /**
     * The default constructor.
     *
     * @param uuid The UUID of the {@link Player}
     * @param name The name of the Player
     */
    public User(final @NotNull UUID uuid, final @NotNull String name) {
        this.uuid = uuid;
        this.name = name;
        this.joinTime = 0L;
        this.factionRank = FactionRank.TRUCE;
        this.power = 10;
    }

    public Faction getFaction() {
        return IridiumFactions.getInstance().getFactionManager().getFactionViaId(factionID);
    }

    public void setFaction(Faction faction) {
        this.factionID = faction == null ? 0 : faction.getId();
        setJoinTime(LocalDateTime.now());
    }

    public LocalDateTime getJoinTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(joinTime), ZoneId.systemDefault());
    }

    public void setJoinTime(LocalDateTime joinTime) {
        this.joinTime = ZonedDateTime.of(joinTime, ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
