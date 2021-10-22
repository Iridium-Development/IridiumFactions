package com.iridium.iridiumfactions;

import com.iridium.iridiumfactions.configs.Configuration;
import com.iridium.iridiumfactions.configs.Messages;
import com.iridium.iridiumfactions.configs.SQL;
import com.iridium.iridiumfactions.database.*;
import com.iridium.iridiumfactions.managers.DatabaseManager;
import com.iridium.iridiumfactions.managers.FactionManager;
import com.iridium.iridiumfactions.managers.UserManager;
import com.iridium.iridiumfactions.managers.tablemanagers.FactionTableManager;
import com.iridium.iridiumfactions.managers.tablemanagers.ForeignFactionTableManager;
import com.iridium.iridiumfactions.managers.tablemanagers.UserTableManager;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestingUtils {

    public static DatabaseManager createDatabaseManagerMock() {
        DatabaseManager databaseManager = mock(DatabaseManager.class);

        when(databaseManager.registerFaction(any(Faction.class))).thenReturn(CompletableFuture.runAsync(() -> {}));
        when(databaseManager.getUserTableManager()).thenReturn(new UserTableManager());
        when(databaseManager.getFactionTableManager()).thenReturn(new FactionTableManager());
        when(databaseManager.getFactionClaimTableManager()).thenReturn(new ForeignFactionTableManager<>(FactionClaim.class, Comparator.comparing(FactionClaim::getWorld).thenComparing(FactionClaim::getX).thenComparing(FactionClaim::getZ)));
        when(databaseManager.getFactionPermissionTableManager()).thenReturn(new ForeignFactionTableManager<>(FactionPermission.class, Comparator.comparing(FactionPermission::getFactionID).thenComparing(FactionPermission::getRank).thenComparing(FactionPermission::getPermission)));
        when(databaseManager.getFactionRelationshipTableManager()).thenReturn(new ForeignFactionTableManager<>(FactionRelationship.class, Comparator.comparing(FactionRelationship::getFactionID).thenComparing(FactionRelationship::getFaction2ID)));
        when(databaseManager.getFactionRelationshipRequestTableManager()).thenReturn(new ForeignFactionTableManager<>(FactionRelationshipRequest.class, Comparator.comparing(FactionRelationshipRequest::getFactionID).thenComparing(FactionRelationshipRequest::getFaction2ID).thenComparing(FactionRelationshipRequest::getRelationshipType)));

        return databaseManager;
    }

    public static IridiumFactions createIridiumFactionsMock() {
        DatabaseManager databaseManager = TestingUtils.createDatabaseManagerMock();
        IridiumFactions iridiumFactions = mock(IridiumFactions.class);

        when(iridiumFactions.getSql()).thenReturn(new SQL());
        when(iridiumFactions.getMessages()).thenReturn(new Messages());
        when(iridiumFactions.getConfiguration()).thenReturn(new Configuration());
        when(iridiumFactions.getUserManager()).thenReturn(new UserManager());
        when(iridiumFactions.getFactionManager()).thenReturn(new FactionManager());
        when(iridiumFactions.getDatabaseManager()).thenReturn(databaseManager);

        return iridiumFactions;
    }

}
