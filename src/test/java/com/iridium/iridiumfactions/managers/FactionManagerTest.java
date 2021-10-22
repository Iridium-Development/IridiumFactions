package com.iridium.iridiumfactions.managers;

import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.MockIridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FactionManagerTest {

    @BeforeEach
    public void setup() {
        MockIridiumFactions mockIridiumFactions = new MockIridiumFactions();
        IridiumFactions.setInstance(mockIridiumFactions);
        mockIridiumFactions.init();
    }

    @Test
    public void getFactionViaId() {
        Faction faction1 = new Faction("Faction 1", 1);
        Faction faction2 = new Faction("Faction 2", 2);

        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction1);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction2);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaId(1).orElse(null), faction1);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaId(2).orElse(null), faction2);
    }

}