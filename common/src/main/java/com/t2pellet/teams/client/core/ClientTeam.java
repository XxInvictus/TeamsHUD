package com.t2pellet.teams.client.core;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.UUID;

/**
 * Client-side representation of a team with teammate information
 */
public interface ClientTeam {

    /** Singleton instance of the client team */
    ClientTeam INSTANCE = new ClientTeamImpl();

    /**
     * Initialize the team with a name and permissions
     * @param name The team name
     * @param hasPermissions Whether the local player has team management permissions
     */
    void init(String name, boolean hasPermissions);

    /**
     * Get the team name
     * @return The team name
     */
    String getName();

    /**
     * Check if the local player has team management permissions
     * @return true if player has permissions
     */
    boolean hasPermissions();

    /**
     * Check if the local player is in a team
     * @return true if in a team
     */
    boolean isInTeam();

    /**
     * Check if the team is empty or only contains the local player
     * @return true if team is effectively empty
     */
    boolean isTeamEmpty();

    /**
     * Get the list of all teammates
     * @return List of teammates
     */
    List<Teammate> getTeammates();

    /**
     * Check if a specific player is in the team
     * @param player The player UUID to check
     * @return true if player is in team
     */
    boolean hasPlayer(UUID player);

    /**
     * Add a player to the team
     * @param player Player UUID
     * @param name Player name
     * @param skin Player skin resource location
     * @param health Player health
     * @param hunger Player hunger level
     */
    void addPlayer(UUID player, String name, ResourceLocation skin, float health, int hunger);

    /**
     * Update a player's health and hunger
     * @param player Player UUID
     * @param health Updated health value
     * @param hunger Updated hunger level
     */
    void updatePlayer(UUID player, float health, int hunger);

    /**
     * Remove a player from the team
     * @param player Player UUID to remove
     */
    void removePlayer(UUID player);

    /**
     * Get the list of favorite teammates
     * @return List of favorite teammates
     */
    List<Teammate> getFavourites();

    /**
     * Check if a teammate is marked as favorite
     * @param player The teammate to check
     * @return true if teammate is a favorite
     */
    boolean isFavourite(Teammate player);

    /**
     * Add a teammate to favorites
     * @param player The teammate to add
     */
    void addFavourite(Teammate player);

    /**
     * Remove a teammate from favorites
     * @param player The teammate to remove
     */
    void removeFavourite(Teammate player);

    /**
     * Reset the team state (clear all data)
     */
    void reset();

    /**
     * Represents a teammate with their information
     */
    class Teammate {
        /** The teammate's UUID */
        public final UUID id;
        /** The teammate's display name */
        public final String name;
        /** The teammate's skin texture location */
        public final ResourceLocation skin;
        /** The teammate's current health */
        float health;
        /** The teammate's current hunger level */
        int hunger;
        /** The last known distance to this teammate (-1.0 if unknown) */
        double lastKnownDistance = -1.0; // -1.0 means not yet calculated

        Teammate(UUID id, String name, ResourceLocation skin, float health, int hunger) {
            this.id = id;
            this.name = name;
            this.skin = skin;
            this.health = health;
            this.hunger = hunger;
        }

        /**
         * Get the teammate's current health
         * @return Health value
         */
        public float getHealth() {
            return health;
        }

        /**
         * Get the teammate's current hunger level
         * @return Hunger level
         */
        public int getHunger() {
            return hunger;
        }

        /**
         * Get the last known distance to this teammate
         * @return Distance in blocks, or -1.0 if unknown/not loaded
         */
        public double getDistance() {
            return lastKnownDistance;
        }

        /**
         * Set the distance to this teammate
         * @param distance Distance in blocks, or -1.0 to mark as unknown
         */
        public void setDistance(double distance) {
            this.lastKnownDistance = distance;
        }
    }

}
