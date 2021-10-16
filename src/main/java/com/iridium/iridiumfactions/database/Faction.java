package com.iridium.iridiumfactions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

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

    /**
     * The default constructor.
     *
     * @param name The name of the Player
     */
    public Faction(final @NotNull String name) {
        this.name = name;
        this.description = "Default Faction Description";
    }

    /**
     * Constructor Used for Comparators
     * @param id The Faction's id
     */
    public Faction(int id){
        this.id = id;
    }
}
