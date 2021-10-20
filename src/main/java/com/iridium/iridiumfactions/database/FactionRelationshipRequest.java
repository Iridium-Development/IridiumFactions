package com.iridium.iridiumfactions.database;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.RelationshipType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "faction_relationships_requests")
public class FactionRelationshipRequest extends FactionData {

    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false)
    private int id;

    @DatabaseField(columnName = "faction2_id", canBeNull = false)
    private int faction2ID;

    @Setter
    @DatabaseField(columnName = "relationship_type", canBeNull = false)
    private RelationshipType relationshipType;

    @DatabaseField(columnName = "user", canBeNull = false)
    private UUID user;

    public FactionRelationshipRequest(@NotNull Faction faction1, @NotNull Faction faction2, RelationshipType relationshipType, User user) {
        super(faction1);
        this.faction2ID = faction2.getId();
        this.relationshipType = relationshipType;
        this.user = user.getUuid();
    }

    public Optional<User> getUser() {
        return IridiumFactions.getInstance().getUserManager().getUserByUUID(user);
    }

    public void accept(User user) {
        Faction faction1 = getFaction().orElse(null);
        Faction faction2 = IridiumFactions.getInstance().getFactionManager().getFactionViaId(faction2ID).orElse(null);
        Optional<User> user1 = getUser();
        if (faction1 == null || faction2 == null) return;

        IridiumFactions.getInstance().getFactionManager().setFactionRelationship(faction1, faction2, relationshipType);
        String message = relationshipType == RelationshipType.ALLY ? IridiumFactions.getInstance().getMessages().factionAllied : IridiumFactions.getInstance().getMessages().factionTruced;

        IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction1).stream().map(User::getPlayer).filter(Objects::nonNull).forEach(p ->
                p.sendMessage(StringUtils.color(message
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%player%", user1.map(User::getName).orElse(""))
                        .replace("%faction%", faction2.getName())
                ))
        );
        IridiumFactions.getInstance().getFactionManager().getFactionMembers(faction2).stream().map(User::getPlayer).filter(Objects::nonNull).forEach(p ->
                p.sendMessage(StringUtils.color(message
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%player%", user.getName())
                        .replace("%faction%", faction1.getName())
                ))
        );
        IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipRequestTableManager().delete(this);
    }
}
