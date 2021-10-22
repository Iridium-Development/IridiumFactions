package com.iridium.iridiumfactions.managers;

import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.User;
import com.iridium.iridiumfactions.managers.tablemanagers.UserTableManager;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserManagerTest {

    private MockedStatic<IridiumFactions> iridiumFactionsMockedStatic;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @BeforeEach
    public void setup() {
        DatabaseManager databaseManager = mock(DatabaseManager.class);
        when(databaseManager.getUserTableManager()).thenReturn(new UserTableManager());

        IridiumFactions iridiumFactions = mock(IridiumFactions.class);
        when(iridiumFactions.getUserManager()).thenReturn(new UserManager());
        when(iridiumFactions.getDatabaseManager()).thenReturn(databaseManager);

        this.iridiumFactionsMockedStatic = mockStatic(IridiumFactions.class);
        iridiumFactionsMockedStatic.when(IridiumFactions::getInstance).thenReturn(iridiumFactions);
    }

    @AfterEach
    public void tearDown() {
        this.iridiumFactionsMockedStatic.close();
    }

    @Test
    public void getUser() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);

        when(player1.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player1.getName()).thenReturn("Player 1");

        User user = new User(UUID.randomUUID(), "User 2");

        IridiumFactions.getInstance().getDatabaseManager().getUserTableManager().addEntry(user);

        when(player2.getUniqueId()).thenReturn(user.getUuid());

        assertEquals(IridiumFactions.getInstance().getUserManager().getUser(player1).getName(), "Player 1");
        assertEquals(IridiumFactions.getInstance().getUserManager().getUser(player2), user);
    }

    @Test
    public void getUserByUUID() {
        User user1 = new User(UUID.randomUUID(), "User 1");
        User user2 = new User(UUID.randomUUID(), "User 2");

        IridiumFactions.getInstance().getDatabaseManager().getUserTableManager().addEntry(user1);
        IridiumFactions.getInstance().getDatabaseManager().getUserTableManager().addEntry(user2);

        assertEquals(IridiumFactions.getInstance().getUserManager().getUserByUUID(user1.getUuid()).orElse(null), user1);
        assertEquals(IridiumFactions.getInstance().getUserManager().getUserByUUID(user2.getUuid()).orElse(null), user2);
    }
}