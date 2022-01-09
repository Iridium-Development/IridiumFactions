package com.iridium.iridiumfactions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.Inventory;

@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "faction_chest")
public class FactionChest extends FactionData {

    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false)
    private int id;

    @DatabaseField(columnName = "chest", canBeNull = false)
    @Setter
    private Inventory factionChest;

    public FactionChest(Faction faction, Inventory factionChest) {
        super(faction);
        this.factionChest = factionChest;
    }
}
