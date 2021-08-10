package com.iridium.iridiumfactions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

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

    /**
     * The default constructor.
     *
     * @param name The name of the Player
     */
    public Faction(final @NotNull String name) {
        this.name = name;
    }

    /**
     * Constructor Used for Comparators
     * @param id The Faction's id
     */
    public Faction(int id){
        this.id = id;
    }
}
