package com.iridium.iridiumfactions.database;

import com.iridium.iridiumfactions.FactionRank;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "faction_permissions")
public final class FactionPermission extends FactionData {

    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false, uniqueCombo = true)
    private int id;

    @DatabaseField(columnName = "permission", canBeNull = false, uniqueCombo = true)
    private @NotNull String permission;

    @DatabaseField(columnName = "rank", canBeNull = false)
    private @NotNull FactionRank rank;

    @DatabaseField(columnName = "allowed", canBeNull = false)
    @Setter
    private boolean allowed;

    /**
     * The default constructor.
     *
     * @param faction    The Faction that has this permission
     * @param permission The permission that is represented in the database
     * @param rank       The rank which may or may not have this permission
     * @param allowed    Whether or not this permission has been granted for this Faction rank.
     */
    public FactionPermission(@NotNull Faction faction, @NotNull String permission, @NotNull FactionRank rank, boolean allowed) {
        super(faction);
        this.permission = permission;
        this.rank = rank;
        this.allowed = allowed;
    }

}
