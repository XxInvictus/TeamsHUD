package com.xxinvictus.teamshudplus.core;

import net.minecraft.server.level.ServerPlayer;

/**
 * Interface for entities that can be members of a team.
 * Implemented by server players to track team membership.
 */
public interface IHasTeam {

    /**
     * Checks if this entity is currently in a team
     * @return true if the entity is in a team
     */
    boolean hasTeam();

    /**
     * Gets the team this entity belongs to
     * @return The team, or null if not in a team
     */
    ModTeam getTeam();

    /**
     * Sets the team for this entity
     * @param team The team to set, or null to remove from team
     */
    void setTeam(ModTeam team);

    /**
     * Checks if another player is a teammate
     * @param other The other player to check
     * @return true if both players are on the same team
     */
    boolean isTeammate(ServerPlayer other);
}
