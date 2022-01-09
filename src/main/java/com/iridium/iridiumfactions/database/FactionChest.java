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

    @DatabaseField(columnName = "page", uniqueCombo = true)
    private int page;

    public FactionChest(Faction faction, Inventory factionChest) {
        this(faction, factionChest, 1);
    }

    public FactionChest(Faction faction, Inventory factionChest, int page) {
        super(faction);
        this.factionChest = factionChest;
        this.page = page;
    }
}
