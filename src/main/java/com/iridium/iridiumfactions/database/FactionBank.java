package com.iridium.iridiumfactions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "faction_bank")
public class FactionBank extends FactionData {

    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false, uniqueCombo = true)
    private int id;

    @DatabaseField(columnName = "bank_item", uniqueCombo = true)
    private String bankItem;

    @DatabaseField(columnName = "number")
    @Setter
    private double number;

    public FactionBank(@NotNull Faction faction, @NotNull String bankItem, double number) {
        super(faction);
        this.bankItem = bankItem;
        this.number = number;
    }
}
