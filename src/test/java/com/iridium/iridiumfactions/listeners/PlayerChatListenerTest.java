package com.iridium.iridiumfactions.listeners;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.*;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerChatListenerTest {

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
    public void onPlayerChatNoFaction() {
        PlayerMock messageSender = new UserBuilder(serverMock).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(messageSender);
        user.setFactionChatType(FactionChatType.FACTION);

        AsyncPlayerChatEvent asyncPlayerChatEvent = new AsyncPlayerChatEvent(false, messageSender, "test", new HashSet<>(serverMock.getOnlinePlayers()));
        serverMock.getPluginManager().callEvent(asyncPlayerChatEvent);
        assertFalse(asyncPlayerChatEvent.getRecipients().isEmpty());
    }

    @Test
    public void onPlayerChatNoChatType() {
        Faction faction = new FactionBuilder().build();
        PlayerMock messageSender = new UserBuilder(serverMock).withFaction(faction).build();

        AsyncPlayerChatEvent asyncPlayerChatEvent = new AsyncPlayerChatEvent(false, messageSender, "test", new HashSet<>(serverMock.getOnlinePlayers()));
        serverMock.getPluginManager().callEvent(asyncPlayerChatEvent);
        assertFalse(asyncPlayerChatEvent.getRecipients().isEmpty());
    }

    @Test
    public void onPlayerChatFaction() {
        Faction faction = new FactionBuilder().build();
        PlayerMock messageSender = new UserBuilder(serverMock).withFaction(faction).build();
        PlayerMock otherFactionMember = new UserBuilder(serverMock).withFaction(faction).build();
        PlayerMock nonFactionMember = new UserBuilder(serverMock).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(messageSender);
        user.setFactionChatType(FactionChatType.FACTION);

        AsyncPlayerChatEvent asyncPlayerChatEvent = new AsyncPlayerChatEvent(false, messageSender, "test", new HashSet<>(serverMock.getOnlinePlayers()));
        serverMock.getPluginManager().callEvent(asyncPlayerChatEvent);
        assertTrue(asyncPlayerChatEvent.getRecipients().isEmpty());

        messageSender.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionChatFormat
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%relationship_format%", RelationshipType.OWN.getColor())
                .replace("%player%", messageSender.getName())
                .replace("%message%", "test"))
        );
        messageSender.assertNoMoreSaid();

        otherFactionMember.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionChatFormat
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%relationship_format%", RelationshipType.OWN.getColor())
                .replace("%player%", messageSender.getName())
                .replace("%message%", "test"))
        );
        otherFactionMember.assertNoMoreSaid();
        nonFactionMember.assertNoMoreSaid();
    }

    @Test
    public void onPlayerChatAlly() {
        Faction faction = new FactionBuilder().build();
        Faction ally = new FactionBuilder().withRelationship(faction, RelationshipType.ALLY).build();
        PlayerMock messageSender = new UserBuilder(serverMock).withFaction(faction).build();
        PlayerMock allyFactionMember = new UserBuilder(serverMock).withFaction(ally).build();
        PlayerMock nonFactionMember = new UserBuilder(serverMock).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(messageSender);
        user.setFactionChatType(FactionChatType.ALLY);

        AsyncPlayerChatEvent asyncPlayerChatEvent = new AsyncPlayerChatEvent(false, messageSender, "test", new HashSet<>(serverMock.getOnlinePlayers()));
        serverMock.getPluginManager().callEvent(asyncPlayerChatEvent);
        assertTrue(asyncPlayerChatEvent.getRecipients().isEmpty());

        messageSender.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionChatFormat
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%relationship_format%", RelationshipType.OWN.getColor())
                .replace("%player%", messageSender.getName())
                .replace("%message%", "test"))
        );
        messageSender.assertNoMoreSaid();

        allyFactionMember.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionChatFormat
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%relationship_format%", RelationshipType.ALLY.getColor())
                .replace("%player%", messageSender.getName())
                .replace("%message%", "test"))
        );
        allyFactionMember.assertNoMoreSaid();
        nonFactionMember.assertNoMoreSaid();
    }

    @Test
    public void onPlayerChatEnemy() {
        Faction faction = new FactionBuilder().build();
        Faction enemy = new FactionBuilder().withRelationship(faction, RelationshipType.ENEMY).build();
        PlayerMock messageSender = new UserBuilder(serverMock).withFaction(faction).build();
        PlayerMock enemyFactionMember = new UserBuilder(serverMock).withFaction(enemy).build();
        PlayerMock nonFactionMember = new UserBuilder(serverMock).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(messageSender);
        user.setFactionChatType(FactionChatType.ENEMY);

        AsyncPlayerChatEvent asyncPlayerChatEvent = new AsyncPlayerChatEvent(false, messageSender, "test", new HashSet<>(serverMock.getOnlinePlayers()));
        serverMock.getPluginManager().callEvent(asyncPlayerChatEvent);
        assertTrue(asyncPlayerChatEvent.getRecipients().isEmpty());

        messageSender.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionChatFormat
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%relationship_format%", RelationshipType.OWN.getColor())
                .replace("%player%", messageSender.getName())
                .replace("%message%", "test"))
        );
        messageSender.assertNoMoreSaid();

        enemyFactionMember.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().factionChatFormat
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%relationship_format%", RelationshipType.ENEMY.getColor())
                .replace("%player%", messageSender.getName())
                .replace("%message%", "test"))
        );
        enemyFactionMember.assertNoMoreSaid();
        nonFactionMember.assertNoMoreSaid();
    }

}