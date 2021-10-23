package com.iridium.iridiumfactions.managers;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserManagerTest {

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
    public void getUser() {
        Player player1 = serverMock.addPlayer("Player 1");
        Player player2 = serverMock.addPlayer("Player 2");
        // serverMock.addPlayer also calls PlayerJoinListener which adds the user to the DB
        IridiumFactions.getInstance().getDatabaseManager().getUserTableManager().getEntries().clear();

        User user = new User(player2.getUniqueId(), "User 2");

        IridiumFactions.getInstance().getDatabaseManager().getUserTableManager().addEntry(user);

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