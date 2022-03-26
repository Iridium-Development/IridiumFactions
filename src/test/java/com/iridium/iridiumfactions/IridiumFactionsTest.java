package com.iridium.iridiumfactions;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.iridium.iridiumfactions.managers.DatabaseManager;
import com.iridium.iridiumfactions.managers.tablemanagers.TableManager;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IridiumFactionsTest {

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
    public void saveDataTest() {
        IridiumFactions.getInstance().saveData();
        for (Field field : DatabaseManager.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object object = field.get(IridiumFactions.getInstance().getDatabaseManager());
                if (object instanceof TableManager tableManager) {
                    assertTrue(tableManager.isSaved(), () -> "TableManager " + tableManager.getClass().getSimpleName() + " has not been saved");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}