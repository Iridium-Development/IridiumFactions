package com.iridium.iridiumfactions.api;

import com.iridium.iridiumfactions.database.User;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter

public class FactionCreateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    @Nullable
    private String factionName;
    @NotNull
    private final User user;

    public FactionCreateEvent(@NotNull User user, @Nullable String factionName) {
        this.factionName = factionName;
        this.user = user;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * The name of the Faction.<br>
     * null indicates that the name of the Player is used as the Faction name
     * because it hasn't been set.
     *
     * @return the name of the Faction or null
     */
    @Nullable
    public String getFactionName() {
        return factionName;
    }

    /**
     *
     * Sets the name of the Faction.<br>
     * set it to null to default to the player's name
     *
     * @param factionName The name of the Faction
     */
    public void setFactionName(@Nullable String factionName) {
        this.factionName = factionName;
    }
}
