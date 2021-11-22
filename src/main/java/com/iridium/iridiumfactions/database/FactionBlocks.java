package com.iridium.iridiumfactions.database;

import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "faction_blocks")
public final class FactionBlocks extends FactionData {

    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false, uniqueCombo = true)
    private int id;

    @DatabaseField(columnName = "block", canBeNull = false, uniqueCombo = true)
    private @NotNull XMaterial material;

    @DatabaseField(columnName = "amount", canBeNull = false)
    @Setter
    private int amount;

    /**
     * The default constructor.
     *
     * @param faction  The Faction which has this valuable block
     * @param material The material of this valuable block
     */
    public FactionBlocks(@NotNull Faction faction, @NotNull XMaterial material) {
        super(faction);
        this.material = material;
    }

}
