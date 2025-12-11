package com.xxinvictus.teamshudplus.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Nullable;

import com.xxinvictus.teamshudplus.TeamsHUDPlus;
import com.xxinvictus.teamshudplus.network.client.S2CTeamDataPacket;
import com.xxinvictus.teamshudplus.network.client.S2CTeamInvitedPacket;
import com.xxinvictus.teamshudplus.platform.Services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Database for managing all teams on the server.
 * Extends {@link SavedData} to persist team data across server restarts.
 * Handles team creation, deletion, player membership, and NBT serialization.
 */
public class TeamDB extends SavedData {

    private static final String TEAMS_KEY = "teams";

    private Map<String, ModTeam> teams = new HashMap<>();
    ServerLevel serverLevel;
    Scoreboard scoreboard;

    private TeamDB(ServerLevel serverLevel) {
        this.serverLevel = serverLevel;
        scoreboard = serverLevel.getScoreboard();
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        toNBT(compoundTag);
        return compoundTag;
    }

    /**
     * Gets a stream of all teams in the database.
     * @return Stream of all ModTeam instances
     */
    public Stream<ModTeam> getTeams() {
        return teams.values().stream();
    }

    /**
     * Adds an existing team to the database and syncs to all clients.
     * @param team The team to add
     * @throws ModTeam.TeamException if a team with the same name already exists
     */
    public void addTeam(ModTeam team) throws ModTeam.TeamException {
        if (teams.containsKey(team.getName())) {
            throw new ModTeam.TeamException(ModComponents.DUPLICATE_TEAM);
        }
        teams.put(team.getName(), team);
        List<ServerPlayer> players = serverLevel.getServer().getPlayerList().getPlayers();
        Services.PLATFORM.sendToClients(new S2CTeamDataPacket(S2CTeamDataPacket.Type.ADD, team.name), players);
        setDirty();
    }

    /**
     * Creates a new team with the given name and optional creator.
     * If a creator is provided, they are automatically added to the team.
     * @param name The name of the team to create
     * @param creator The player creating the team, or null if created by command/system
     * @return The newly created ModTeam
     * @throws ModTeam.TeamException if the name is invalid or creator is already in a team
     */
    public ModTeam addTeam(String name, @Nullable ServerPlayer creator) throws ModTeam.TeamException {
        if (name == null || name.trim().isEmpty()) {
            throw new ModTeam.TeamException(ModComponents.translatable("teamshudplus.error.invalidname"));
        }
        if (creator != null && ((IHasTeam) creator).hasTeam()) {
            throw new ModTeam.TeamException(ModComponents.translatable("teamshudplus.error.alreadyinteam", creator.getName().getString()));
        }
        ModTeam team = new ModTeam.Builder(name).complete(this);
        addTeam(team);
        if (creator != null) {
            team.addPlayer(creator);
        }
        List<ServerPlayer> players = (creator != null) ? creator.getServer().getPlayerList().getPlayers() : serverLevel.getServer().getPlayerList().getPlayers();
        Services.PLATFORM.sendToClients(new S2CTeamDataPacket(S2CTeamDataPacket.Type.ONLINE, team.name), players);
        setDirty();
        return team;
    }

    /**
     * Removes a team from the database, clears all players, and syncs to clients.
     * @param team The team to remove
     */
    public void removeTeam(ModTeam team) {
        teams.remove(team.getName());
        MinecraftServer server = serverLevel.getServer();
        scoreboard.removePlayerTeam(scoreboard.getPlayerTeam(team.getName()));
        team.clear();
        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        Services.PLATFORM.sendToClients(new S2CTeamDataPacket(S2CTeamDataPacket.Type.REMOVE, team.name), players);
        setDirty();
    }

    /**
     * Checks if the database has no teams.
     * @return true if there are no teams
     */
    public boolean isEmpty() {
        return teams.isEmpty();
    }

    /**
     * Checks if a team with the given name exists.
     * @param team The team name to check
     * @return true if the team exists
     */
    public boolean hasTeam(String team) {
        return teams.containsKey(team);
    }

    /**
     * Gets the team that a player is currently in.
     * @param player The player to check
     * @return The player's team, or null if not in a team
     */
    public ModTeam getTeam(ServerPlayer player) {
        return ((IHasTeam) player).getTeam();
    }

    /**
     * Gets a team by its name.
     * @param name The team name
     * @return The team, or null if not found
     */
    public ModTeam getTeam(String name) {
        return teams.get(name);
    }

    /**
     * Sends a team invitation to a player.
     * @param player The player to invite
     * @param team The team to invite them to
     * @throws ModTeam.TeamException if the player is already in a team
     */
    public void invitePlayerToTeam(ServerPlayer player, ModTeam team) throws ModTeam.TeamException {
        if (((IHasTeam) player).hasTeam()) {
            throw new ModTeam.TeamException(ModComponents.translatable("teamshudplus.error.alreadyinteam", player.getName().getString()));
        }
        Services.PLATFORM.sendToClient(new S2CTeamInvitedPacket(team), player);
    }

    /**
     * Adds a player to a team.
     * @param player The player to add
     * @param team The team to add them to
     * @throws ModTeam.TeamException if the player is already in a team
     */
    public void addPlayerToTeam(ServerPlayer player, ModTeam team) throws ModTeam.TeamException {
        if (((IHasTeam) player).hasTeam()) {
            throw new ModTeam.TeamException(ModComponents.translatable("teamshudplus.error.alreadyinteam", player.getName()));
        }
        team.addPlayer(player);
    }

    /**
     * Removes a player from their current team. If the team becomes empty, it is deleted.
     * @param player The player to remove
     * @throws ModTeam.TeamException if the player is not in a team
     */
    public void removePlayerFromTeam(ServerPlayer player) throws ModTeam.TeamException {
        ModTeam playerTeam = ((IHasTeam) player).getTeam();
        if (playerTeam == null) {
            throw new ModTeam.TeamException(ModComponents.translatable("teamshudplus.error.notinteam", player.getName().getString()));
        }
        playerTeam.removePlayer(player);
        if (playerTeam.isEmpty()) {
            removeTeam(playerTeam);
        }
    }

    /**
     * Deserializes team data from NBT.
     * @param compound The NBT tag containing team data
     */
    public void fromNBT(CompoundTag compound) {
        teams.clear();
        ListTag list = compound.getList(TEAMS_KEY, Tag.TAG_COMPOUND);
        for (var tag : list) {
            try {
                addTeam(ModTeam.fromNBT((CompoundTag) tag,this));
            } catch (Exception ex) {
                TeamsHUDPlus.LOGGER.error("Failed to load team from NBT: {}", ex.getMessage(), ex);
            }
        }
    }

    /**
     * Serializes all teams to NBT.
     * @param compound The NBT tag to write to
     */
    public void toNBT(CompoundTag compound) {
        ListTag list = new ListTag();
        for (var team : teams.values()) {
            list.add(team.toNBT());
        }
        compound.put(TEAMS_KEY, list);
    }

    /**
     * Gets the TeamDB for a server level if it exists.
     * @param serverLevel The server level
     * @return The TeamDB, or null if not found
     */
    static TeamDB get(ServerLevel serverLevel) {
        return serverLevel.getDataStorage()
                .get(compoundTag -> loadStatic(compoundTag, serverLevel),TEAMS_KEY);
    }

    /**
     * Gets or creates the TeamDB for a server level.
     * @param serverLevel The server level
     * @return The TeamDB instance
     */
    static TeamDB getOrMake(ServerLevel serverLevel) {
        return serverLevel.getDataStorage()
                .computeIfAbsent(compoundTag -> loadStatic(compoundTag,serverLevel), () -> new TeamDB(serverLevel), TEAMS_KEY);
    }

    /**
     * Gets or creates the default TeamDB for the server (overworld).
     * @param server The Minecraft server
     * @return The default TeamDB instance
     */
    public static TeamDB getOrMakeDefault(MinecraftServer server) {
        return getOrMake(server.overworld());
    }

    /**
     * Loads a TeamDB from NBT data.
     * @param compoundTag The NBT data
     * @param level The server level
     * @return The loaded TeamDB instance
     */
    public static TeamDB loadStatic(CompoundTag compoundTag,ServerLevel level) {
        TeamDB id = new TeamDB(level);
        id.fromNBT(compoundTag);
        return id;
    }

}
