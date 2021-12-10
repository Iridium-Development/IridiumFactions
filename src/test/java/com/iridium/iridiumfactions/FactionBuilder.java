package com.iridium.iridiumfactions;

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

    public Faction build() {
        return faction;
    }

}
