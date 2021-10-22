package com.iridium.iridiumfactions.managers;

import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.managers.tablemanagers.FactionTableManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FactionManagerTest {

    private MockedStatic<IridiumFactions> iridiumFactionsMockedStatic;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @BeforeEach
    public void setup() throws SQLException {
        DatabaseManager databaseManager = mock(DatabaseManager.class);
        when(databaseManager.getFactionTableManager()).thenReturn(new FactionTableManager(null, false));

        IridiumFactions iridiumFactions = mock(IridiumFactions.class);
        when(iridiumFactions.getFactionManager()).thenReturn(new FactionManager());
        when(iridiumFactions.getDatabaseManager()).thenReturn(databaseManager);

        this.iridiumFactionsMockedStatic = mockStatic(IridiumFactions.class);
        iridiumFactionsMockedStatic.when(IridiumFactions::getInstance).thenReturn(iridiumFactions);
    }

    @AfterEach
    public void tearDown() {
        this.iridiumFactionsMockedStatic.close();
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

    @Test
    public void getFactionViaName() {
        Faction faction1 = new Faction("Faction 1", 1);
        Faction faction2 = new Faction("Faction 2", 2);

        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction1);
        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().addEntry(faction2);

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaName("Faction 1").orElse(null), faction1);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaName("Faction 2").orElse(null), faction2);

        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaName("faction 1").orElse(null), faction1);
        assertEquals(IridiumFactions.getInstance().getFactionManager().getFactionViaName("FACTION 2").orElse(null), faction2);
    }

}