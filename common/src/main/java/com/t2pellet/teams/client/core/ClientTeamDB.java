package com.t2pellet.teams.client.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Client-side database for tracking known teams and their online status.
 * Maintains a registry of all teams and which ones have online members.
 */
public class ClientTeamDB {

    /** Singleton instance of the client team database */
    public static ClientTeamDB INSTANCE = new ClientTeamDB();

    private Set<String> teams;
    private Set<String> onlineTeams;

    private ClientTeamDB() {
        teams = new HashSet<>();
        onlineTeams = new HashSet<>();
    }

    /**
     * Gets all known teams
     * @return List of team names
     */
    public List<String> getTeams() {
        return teams.stream().toList();
    }

    /**
     * Gets all teams that have at least one online member
     * @return List of online team names
     */
    public List<String> getOnlineTeams() {
        return onlineTeams.stream().toList();
    }

    /**
     * Adds a team to the database
     * @param team The team name to add
     */
    public void addTeam(String team) {
        teams.add(team);
    }

    /**
     * Removes a team from the database and marks it offline
     * @param team The team name to remove
     */
    public void removeTeam(String team) {
        teams.remove(team);
        teamOffline(team);
    }

    /**
     * Checks if a team exists in the database
     * @param team The team name to check
     * @return true if the team exists
     */
    public boolean containsTeam(String team) {
        return teams.contains(team);
    }

    /**
     * Marks a team as having online members
     * @param team The team name to mark online
     */
    public void teamOnline(String team) {
        onlineTeams.add(team);
    }

    /**
     * Marks a team as having no online members
     * @param team The team name to mark offline
     */
    public void teamOffline(String team) {
        onlineTeams.remove(team);
    }

    /**
     * Checks if a team has online members
     * @param team The team name to check
     * @return true if the team has online members
     */
    public boolean containsOnlineTeam(String team) {
        return onlineTeams.contains(team);
    }

    /**
     * Clears all team data from the database
     */
    public void clear() {
        teams.clear();
        onlineTeams.clear();
    }

}
