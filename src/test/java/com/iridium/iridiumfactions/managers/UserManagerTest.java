package com.iridium.iridiumfactions.managers;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.UserBuilder;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserManagerTest {

    private ServerMock serverMock;

    @BeforeEach
    public void setup() {
        this.serverMock = MockBukkit.mock();
        MockBukkit.load(IridiumFactions.class);
    }

    @AfterEach
    public void tearDown() {
        Bukkit.getScheduler().cancelTasks(IridiumFactions.getInstance());
        MockBukkit.unmock();
    }

    @Test
    public void getUser() {
        PlayerMock player1 = new UserBuilder(serverMock).build();
        PlayerMock player2 = new UserBuilder(serverMock).build();
        // serverMock.addPlayer also calls PlayerJoinListener which adds the user to the DB
        IridiumFactions.getInstance().getDatabaseManager().getUserTableManager().getEntries().clear();

        User user = new User(player2.getUniqueId(), player2.getName());

        IridiumFactions.getInstance().getDatabaseManager().getUserTableManager().addEntry(user);

        assertEquals(player1.getName(), IridiumFactions.getInstance().getUserManager().getUser(player1).getName());
        assertEquals(player1.getUniqueId(), IridiumFactions.getInstance().getUserManager().getUser(player1).getUuid());
        assertEquals(user, IridiumFactions.getInstance().getUserManager().getUser(player2));
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