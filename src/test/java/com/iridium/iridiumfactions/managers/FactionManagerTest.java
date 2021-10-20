package com.iridium.iridiumfactions.managers;

import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import org.junit.jupiter.api.Assertions;

import java.util.Optional;

class FactionManagerTest {

    @org.junit.jupiter.api.Test
    public void getFactionViaId() {
        Faction expected = new Faction("Test");
        expected.setId(1);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(expected);

        Optional<Faction> actual = IridiumFactions.getInstance().getFactionManager().getFactionViaId(1);
        Assertions.assertEquals(actual.orElse(null), expected);
    }
}