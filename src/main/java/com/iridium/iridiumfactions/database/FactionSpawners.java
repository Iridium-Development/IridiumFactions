package com.iridium.iridiumfactions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "faction_spawners")
public final class FactionSpawners extends FactionData {

    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false, uniqueCombo = true)
    private int id;

    @DatabaseField(columnName = "spawner_type", canBeNull = false, uniqueCombo = true)
    private @NotNull EntityType spawnerType;

    @DatabaseField(columnName = "amount", canBeNull = false)
    @Setter
    private int amount;

    /**
     * The default constructor.
     *
     * @param faction     The Faction which has this valuable block
     * @param spawnerType The type of the spawner
     */
    public FactionSpawners(@NotNull Faction faction, @NotNull EntityType spawnerType) {
        super(faction);
        this.spawnerType = spawnerType;
    }

}
