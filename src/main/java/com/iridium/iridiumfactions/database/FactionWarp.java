package com.iridium.iridiumfactions.database;

import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumfactions.IridiumFactions;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "faction_warps")
public final class FactionWarp extends FactionData {

    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false, uniqueCombo = true)
    private int id;

    @DatabaseField(columnName = "location")
    private @NotNull Location location;

    @DatabaseField(columnName = "name", uniqueCombo = true)
    private @NotNull String name;

    @DatabaseField(columnName = "password")
    private String password;

    @DatabaseField(columnName = "description")
    @Setter
    private String description;

    @DatabaseField(columnName = "icon")
    @Setter
    private XMaterial icon;

    public FactionWarp(Faction faction, Location location, String name) {
        super(faction);
        this.location = location;
        this.name = name;
        this.icon = IridiumFactions.getInstance().getInventories().warpsGUI.item.material;
    }
}
