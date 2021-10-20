package com.iridium.iridiumfactions.database;

import com.iridium.iridiumfactions.RelationshipType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "faction_relationships")
public class FactionRelationship extends FactionData{

    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false)
    private int id;

    @DatabaseField(columnName = "faction2_id", canBeNull = false)
    private int faction2ID;

    @Setter
    @DatabaseField(columnName = "relationship_type", canBeNull = false)
    private RelationshipType relationshipType;

    public FactionRelationship(@NotNull Faction faction1, @NotNull Faction faction2) {
        super(faction1);
        this.faction2ID = faction2.getId();
        this.relationshipType = RelationshipType.TRUCE;
    }

    public FactionRelationship(@NotNull Faction faction1, @NotNull Faction faction2, RelationshipType relationshipType) {
        super(faction1);
        this.faction2ID = faction2.getId();
        this.relationshipType = relationshipType;
    }
}
