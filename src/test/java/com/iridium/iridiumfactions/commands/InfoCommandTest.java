package com.iridium.iridiumfactions.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.*;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InfoCommandTest {

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
    public void executeInfoCommandNoFaction() {
        PlayerMock playerMock = new UserBuilder(serverMock).build();

        serverMock.dispatchCommand(playerMock, "f info");
        Faction faction = new Faction(FactionType.WILDERNESS);
        playerMock.assertSaid(StringUtils.color(StringUtils.getCenteredMessage(IridiumFactions.getInstance().getConfiguration().factionInfoTitle.replace("%faction%", RelationshipType.WILDERNESS.getColor() + faction.getName()), IridiumFactions.getInstance().getConfiguration().factionInfoTitleFiller)));
        for (String line : IridiumFactions.getInstance().getConfiguration().factionInfo) {
            playerMock.assertSaid(StringUtils.color(line
                    .replace("%faction_description%", faction.getDescription())
                    .replace("%faction_total_power%", String.valueOf(faction.getTotalPower()))
                    .replace("%faction_remaining_power%", String.valueOf(faction.getRemainingPower()))
                    .replace("%faction_land%", String.valueOf(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction).size()))
                    .replace("%faction_members_online%", "")
                    .replace("%faction_members_offline%", "")
                    .replace("%faction_members_online_count%", "0")
                    .replace("%faction_members_offline_count%", "0")
                    .replace("%faction_members_count%", "0")
                    .replace("%faction_value%", "0.0")
                    .replace("%faction_rank%", String.valueOf(faction.getRank()))
            ));
        }
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeInfoCommandWithFaction() {
        Faction faction = new FactionBuilder("Faction").build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();


        serverMock.dispatchCommand(playerMock, "f info");
        playerMock.assertSaid(StringUtils.color(StringUtils.getCenteredMessage(IridiumFactions.getInstance().getConfiguration().factionInfoTitle.replace("%faction%", RelationshipType.OWN.getColor() + faction.getName()), IridiumFactions.getInstance().getConfiguration().factionInfoTitleFiller)));
        for (String line : IridiumFactions.getInstance().getConfiguration().factionInfo) {
            playerMock.assertSaid(StringUtils.color(line
                    .replace("%faction_description%", faction.getDescription())
                    .replace("%faction_total_power%", String.valueOf(faction.getTotalPower()))
                    .replace("%faction_remaining_power%", String.valueOf(faction.getRemainingPower()))
                    .replace("%faction_land%", String.valueOf(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction).size()))
                    .replace("%faction_members_online%", playerMock.getName())
                    .replace("%faction_members_offline%", "")
                    .replace("%faction_members_online_count%", "1")
                    .replace("%faction_members_offline_count%", "0")
                    .replace("%faction_members_count%", "1")
                    .replace("%faction_value%", "0.0")
                    .replace("%faction_rank%", String.valueOf(faction.getRank()))
            ));
        }
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeInfoCommandWithFactionArgs() {
        Faction faction = new FactionBuilder("Faction").build();
        PlayerMock playerMock = new UserBuilder(serverMock).build();


        serverMock.dispatchCommand(playerMock, "f info Faction");
        playerMock.assertSaid(StringUtils.color(StringUtils.getCenteredMessage(IridiumFactions.getInstance().getConfiguration().factionInfoTitle.replace("%faction%", RelationshipType.TRUCE.getColor() + faction.getName()), IridiumFactions.getInstance().getConfiguration().factionInfoTitleFiller)));
        for (String line : IridiumFactions.getInstance().getConfiguration().factionInfo) {
            playerMock.assertSaid(StringUtils.color(line
                    .replace("%faction_description%", faction.getDescription())
                    .replace("%faction_total_power%", String.valueOf(faction.getTotalPower()))
                    .replace("%faction_remaining_power%", String.valueOf(faction.getRemainingPower()))
                    .replace("%faction_land%", String.valueOf(IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().getEntries(faction).size()))
                    .replace("%faction_members_online%", "")
                    .replace("%faction_members_offline%", "")
                    .replace("%faction_members_online_count%", "0")
                    .replace("%faction_members_offline_count%", "0")
                    .replace("%faction_members_count%", "0")
                    .replace("%faction_value%", "0.0")
                    .replace("%faction_rank%", String.valueOf(faction.getRank()))
            ));
        }
        playerMock.assertNoMoreSaid();
    }

    @Test
    public void executeLeaveCommandSuccessful() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        PlayerMock otherPlayer = new UserBuilder(serverMock).withFaction(faction).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);

        serverMock.dispatchCommand(playerMock, "f leave");
        playerMock.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().leftFaction
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%name%", faction.getName())
        ));
        playerMock.assertNoMoreSaid();

        otherPlayer.assertSaid(StringUtils.color(IridiumFactions.getInstance().getMessages().userLeftFaction
                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                .replace("%name%", faction.getName())
                .replace("%player%", playerMock.getName())
        ));
        otherPlayer.assertNoMoreSaid();

        assertEquals(user.getFactionID(), 0);
        assertEquals(user.getFactionRank(), FactionRank.TRUCE);
    }

}