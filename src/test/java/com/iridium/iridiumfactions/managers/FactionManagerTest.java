package com.iridium.iridiumfactions.managers;

import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FactionManagerTest {

    @Test
    public void getFactionViaId() {
        Faction expected = new Faction("Test");
        expected.setId(1);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(expected);

        Optional<Faction> actual = IridiumFactions.getInstance().getFactionManager().getFactionViaId(1);
        assertEquals(actual.orElse(null), expected);
    }

    @Test
    public void testTest() {
        assertEquals(1, 1);
    }
}