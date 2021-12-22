package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionBuilder;
import com.iridium.iridiumfactions.FactionChatType;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.UserBuilder;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ChatCommandTest {

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
    public void executeChatCommandNoFaction() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();

        serverMock.dispatchCommand(playerMock, "f claim");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().dontHaveFaction.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeChatCommandBadSyntax() {
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(new FactionBuilder().build()).build();

        serverMock.dispatchCommand(playerMock, "f chat");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getCommands().chatCommand.syntax.replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)));
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeChatCommandSuccess() {
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(new FactionBuilder().build()).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        for (FactionChatType factionChatType : FactionChatType.values()) {
            for (String alias : factionChatType.getAliases()) {
                user.setFactionChatType(null);
                assertNull(user.getFactionChatType());
                serverMock.dispatchCommand(playerMock, "f chat " + alias);
                assertEquals(factionChatType, user.getFactionChatType());
                playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().setFactionChatType
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%type%", factionChatType.name()))
                );
                playerMock.assertNoMoreSaid();
            }
        }
    }

    @Test
    public void executeChatCommandError() {
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(new FactionBuilder().build()).build();

        serverMock.dispatchCommand(playerMock, "f chat invalid");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().unknownFactionChatType
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%type%", "invalid"))
        );
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void tabCompleteChatCommand() {
        assertEquals(Arrays.asList("a", "ally", "e", "enemy", "f", "faction", "n", "none"), IridiumFactions.getInstance().getCommands().chatCommand.onTabComplete(null, null, null, new String[]{"", ""}));
        assertEquals(Arrays.asList("f", "faction"), IridiumFactions.getInstance().getCommands().chatCommand.onTabComplete(null, null, null, new String[]{"", "f"}));
        assertEquals(Arrays.asList("a", "ally"), IridiumFactions.getInstance().getCommands().chatCommand.onTabComplete(null, null, null, new String[]{"", "a"}));
        assertEquals(Arrays.asList("e", "enemy"), IridiumFactions.getInstance().getCommands().chatCommand.onTabComplete(null, null, null, new String[]{"", "e"}));
        assertEquals(Arrays.asList("n", "none"), IridiumFactions.getInstance().getCommands().chatCommand.onTabComplete(null, null, null, new String[]{"", "n"}));
    }

}