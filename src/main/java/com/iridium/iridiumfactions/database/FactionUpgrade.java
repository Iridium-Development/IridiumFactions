package com.iridium.iridiumfactions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "faction_upgrades")
public final class FactionUpgrade extends FactionData {

    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false, uniqueCombo = true)
    private int id;

    @DatabaseField(columnName = "level", canBeNull = false)
    @Setter
    private int level;

    @DatabaseField(columnName = "upgrade", canBeNull = false, uniqueCombo = true)
    @Setter
    private String upgrade;

    public FactionUpgrade(Faction faction, String upgrade) {
        this(faction, upgrade, 1);
    }

    public FactionUpgrade(Faction faction, String upgrade, int level) {
        super(faction);
        this.upgrade = upgrade;
        this.level = level;
    }

}
