package com.iridium.iridiumfactions;

import be.seeseemelk.mockbukkit.ServerMock;
import com.iridium.iridiumfactions.database.Faction;

import java.util.concurrent.atomic.AtomicInteger;

public class FactionBuilder {
    private static final AtomicInteger FACTION_ID = new AtomicInteger(1);
    private final Faction faction;

    public FactionBuilder() {
        int id = FACTION_ID.getAndIncrement();
        this.faction = new Faction("Faction_" + id, id);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
    }

    public FactionBuilder(String name) {
        this.faction = new Faction(name, FACTION_ID.getAndIncrement());
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
    }

    public FactionBuilder(int id) {
        this.faction = new Faction("Faction_" + id, id);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction);
    }

    public FactionBuilder withRelationship(Faction faction, RelationshipType relationshipType) {
        IridiumFactions.getInstance().getFactionManager().setFactionRelationship(faction, this.faction, relationshipType);
        return this;
    }

    public FactionBuilder withMembers(int amount, double power, ServerMock serverMock) {
        for (int i = 0; i < amount; i++) {
            new UserBuilder(serverMock).withFaction(faction).withPower(power).build();
        }
        return this;
    }

    public Faction build() {
        return faction;
    }

}
