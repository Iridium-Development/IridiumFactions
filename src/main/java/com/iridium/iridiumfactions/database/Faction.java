package com.iridium.iridiumfactions.database;

import com.iridium.iridiumfactions.IridiumFactions;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Represents a Faction of IridiumFactions.
 */
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "factions")
public final class Faction {

    @DatabaseField(columnName = "id", canBeNull = false, generatedId = true)
    private int id;

    @DatabaseField(columnName = "name", canBeNull = false)
    private @NotNull String name;

    @DatabaseField(columnName = "description", canBeNull = false)
    private @NotNull String description;

    @DatabaseField(columnName = "create_time")
    private long time;

    /**
     * The default constructor.
     *
     * @param name The name of the Player
     */
    public Faction(final @NotNull String name) {
        this.name = name;
        this.description = "Default Faction Description";
        this.time = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * The factions remaining power
     *
     * @return The factions remaining power
     */
    public double getRemainingPower() {
        int land = IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(this).size();
        return getTotalPower() - land;
    }

    /**
     * The factions total power
     *
     * @return The factions total power
     */
    public double getTotalPower() {
        return IridiumFactions.getInstance().getFactionManager().getFactionMembers(this).stream().map(User::getPower).reduce(0.00, Double::sum);
    }

    /**
     * The date this faction was created.
     *
     * @return A LocalDateTime when this faction was created
     */
    public LocalDateTime getCreateTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(getTime()), ZoneId.systemDefault());
    }

    /**
     * Constructor Used for Comparators
     *
     * @param id The Faction's id
     */
    public Faction(int id) {
        this.id = id;
    }
}
