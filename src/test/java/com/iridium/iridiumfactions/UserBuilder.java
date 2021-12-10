package com.iridium.iridiumfactions;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.User;

public class UserBuilder {
    private final PlayerMock playerMock;
    private final User user;

    public UserBuilder(ServerMock serverMock) {
        this.playerMock = serverMock.addPlayer();
        this.user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
    }

    public UserBuilder(ServerMock serverMock, String playerName) {
        this.playerMock = serverMock.addPlayer(playerName);
        this.user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
    }

    public UserBuilder withFaction(Faction faction) {
        user.setFaction(faction);
        return this;
    }

    public UserBuilder withFactionRank(FactionRank factionRank) {
        user.setFactionRank(factionRank);
        return this;
    }

    public UserBuilder setBypassing(){
        user.setBypassing(true);
        return this;
    }

    public PlayerMock build() {
        return playerMock;
    }

}
