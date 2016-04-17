package net.minelink.ctplus.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Thrown whenever a player comes out of combat.
 */
public class CombatUntagEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final UUID playerId;

    private final CombatUntagReason reason;

    public CombatUntagEvent(UUID playerId, CombatUntagReason reason) {
        this.playerId = playerId;
        this.reason = reason;
    }

    /**
     * Gets the player ID that the tag expired for.
     *
     * @return the {@link UUID} player ID.
     */
    public UUID getPlayerId() {
        return playerId;
    }

    /**
     * Gets the reason why the combat has been removed.
     *
     * @return the {@link CombatUntagReason} for this tag removal.
     */
    public CombatUntagReason getReason() {
        return reason;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
