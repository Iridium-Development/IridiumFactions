package com.iridium.iridiumfactions;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RelationshipTypeTest {

    private ServerMock serverMock;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @BeforeEach
    public void setup() {
        this.serverMock = MockBukkit.mock();
        MockBukkit.load(IridiumFactions.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void getColor() {
        Map<RelationshipType, String> factionRelationshipColors = IridiumFactions.getInstance().getConfiguration().factionRelationshipColors;
        assertEquals(RelationshipType.OWN.getDefaultColor(), factionRelationshipColors.get(RelationshipType.OWN));
        assertEquals(RelationshipType.ALLY.getDefaultColor(), factionRelationshipColors.get(RelationshipType.ALLY));
        assertEquals(RelationshipType.TRUCE.getDefaultColor(), factionRelationshipColors.get(RelationshipType.TRUCE));
        assertEquals(RelationshipType.ENEMY.getDefaultColor(), factionRelationshipColors.get(RelationshipType.ENEMY));
    }
}