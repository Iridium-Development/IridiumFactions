package com.iridium.iridiumfactions.database;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.iridium.iridiumfactions.FactionBuilder;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.UserBuilder;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

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
    public void applyPotionEffectsInOwnTerritorySuccess() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        IridiumFactions.getInstance().getFactionManager().getFactionBooster(faction, "regeneration").setTime(LocalDateTime.now().plusSeconds(600));
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, playerMock.getLocation().getChunk()));

        user.applyPotionEffects();

        PotionEffect potionEffect = playerMock.getPotionEffect(PotionEffectType.REGENERATION);
        assertNotNull(potionEffect);
        assertEquals(1, potionEffect.getAmplifier());
    }

    @Test
    public void applyPotionEffectsNotInOwnTerritoryFailure() {
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        IridiumFactions.getInstance().getFactionManager().getFactionBooster(faction, "regeneration").setTime(LocalDateTime.now().plusSeconds(600));

        user.applyPotionEffects();

        PotionEffect potionEffect = playerMock.getPotionEffect(PotionEffectType.REGENERATION);
        assertNull(potionEffect);
    }

    @Test
    public void applyPotionEffectsInOtherLandTerritorySuccess() {
        IridiumFactions.getInstance().getBoosters().boostersOnlyEffectFactionMembers = false;
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        IridiumFactions.getInstance().getFactionManager().getFactionBooster(faction, "regeneration").setTime(LocalDateTime.now().plusSeconds(600));
        IridiumFactions.getInstance().getDatabaseManager().getFactionClaimTableManager().addEntry(new FactionClaim(faction, playerMock.getLocation().getChunk()));

        user.applyPotionEffects();

        PotionEffect potionEffect = playerMock.getPotionEffect(PotionEffectType.REGENERATION);
        assertNotNull(potionEffect);
        assertEquals(1, potionEffect.getAmplifier());
    }

    @Test
    public void applyPotionEffectsNotInOwnTerritorySuccess() {
        IridiumFactions.getInstance().getBoosters().boostersOnlyInTerritory = false;
        Faction faction = new FactionBuilder().build();
        PlayerMock playerMock = new UserBuilder(serverMock).withFaction(faction).build();
        User user = IridiumFactions.getInstance().getUserManager().getUser(playerMock);
        IridiumFactions.getInstance().getFactionManager().getFactionBooster(faction, "regeneration").setTime(LocalDateTime.now().plusSeconds(600));

        user.applyPotionEffects();

        PotionEffect potionEffect = playerMock.getPotionEffect(PotionEffectType.REGENERATION);
        assertNotNull(potionEffect);
        assertEquals(1, potionEffect.getAmplifier());
    }

}