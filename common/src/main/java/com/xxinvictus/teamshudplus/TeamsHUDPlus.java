package com.xxinvictus.teamshudplus;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxinvictus.teamshudplus.core.ModTeam;
import com.xxinvictus.teamshudplus.core.TeamDB;
import com.xxinvictus.teamshudplus.network.CommonPacketHandler;
import com.xxinvictus.teamshudplus.network.client.S2CTeamDataPacket;
import com.xxinvictus.teamshudplus.network.client.S2CTeamPlayerDataPacket;
import com.xxinvictus.teamshudplus.platform.Services;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Main mod initialization class for TeamsHUDPlus.
 * Handles common logic for both Forge and Fabric platforms including:
 * - Packet registration
 * - Player connection/disconnection events
 * - Advancement synchronization
 * - Health/hunger updates
 */
public class TeamsHUDPlus {

    /** The mod ID used for registration and resource locations */
    public static final String MODID = "teamshudplus";
    /** The mod display name */
    public static final String MOD_NAME = "TeamsHUDPlus";
    /** Logger for mod messages */
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    /**
     * Initializes the mod by registering network packets.
     * Called by platform-specific entry points.
     */
    public static void init() {
        CommonPacketHandler.registerPackets();
    }

    /**
     * Handles player advancement events. If advancement syncing is enabled,
     * adds the advancement to the player's team.
     * @param player The player who earned the advancement
     * @param advancement The advancement earned
     */
    public static void onAdvancement(ServerPlayer player, Advancement advancement) {
        if (!Services.PLATFORM.getConfig().syncAdvancements()) {
            return;
        }
        TeamDB teamDB = TeamDB.getOrMakeDefault(player.server);
        ModTeam team = teamDB.getTeam(player);
        if (team != null) {
            team.addAdvancement(advancement);
        }
    }

    /**
     * Handles player connection. Marks player as online in their team and
     * sends team data packets to sync the client.
     * @param player The player connecting
     */
    public static void playerConnect(ServerPlayer player) {
        TeamDB teamDB = TeamDB.getOrMakeDefault(player.server);
        ModTeam team = teamDB.getTeam(player);
        if (team != null) {
            team.playerOnline(player, true);
        }
        // Send packets
        var teams = teamDB.getTeams().map(t -> t.name).toArray(String[]::new);
        var onlineTeams = teamDB.getTeams().filter(t -> t.getOnlinePlayers().stream().findAny().isPresent()).map(t -> t.name).toArray(String[]::new);
        Services.PLATFORM.sendToClient(new S2CTeamDataPacket(S2CTeamDataPacket.Type.ADD, teams), player);
        Services.PLATFORM.sendToClient(new S2CTeamDataPacket(S2CTeamDataPacket.Type.ONLINE, onlineTeams), player);
    }

    /**
     * Handles player disconnection. Marks player as offline in their team.
     * @param player The player disconnecting
     */
    public static void playerDisconnect(ServerPlayer player) {
        TeamDB teamDB = TeamDB.getOrMakeDefault(player.server);
        ModTeam team = teamDB.getTeam(player);
        if (team != null) {
            team.playerOffline(player, true);
        }
        teamDB.setDirty();
    }

    /**
     * Handles player cloning (death/respawn, dimension change). Transfers
     * team membership from old to new player entity.
     * @param oldPlayer The old player entity
     * @param newPlayer The new player entity
     * @param alive Whether the player is still alive (dimension change vs death)
     */
    public static void playerClone(ServerPlayer oldPlayer,ServerPlayer newPlayer,boolean alive) {
        TeamDB teamDB = TeamDB.getOrMakeDefault(oldPlayer.server);
        ModTeam team = teamDB.getTeam(oldPlayer);
        if (team != null) {
            team.playerOffline(oldPlayer, false);
            team.playerOnline(newPlayer, false);
        }
    }

    /**
     * Handles player health/hunger updates. Syncs the updated stats to all
     * other teammates.
     * @param player The player whose stats changed
     * @param health The new health value
     * @param hunger The new hunger value
     */
    public static void onPlayerHealthUpdate(ServerPlayer player, float health, int hunger) {
        ModTeam team = TeamDB.getOrMakeDefault(player.server).getTeam(player);
        if (team != null) {
            List<ServerPlayer> players = team.getOnlinePlayers().stream().filter(other -> !other.equals(player)).collect(Collectors.toList());
            Services.PLATFORM.sendToClients(new S2CTeamPlayerDataPacket(player, S2CTeamPlayerDataPacket.Type.UPDATE), players);
        }
    }

    /**
     * Creates a ResourceLocation with the mod's namespace.
     * @param path The resource path
     * @return A ResourceLocation for this mod
     */
    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID,path);
    }

    /**
     * Called when the server stops.
     * @param server The stopping server
     */
    public static void onServerStopped(MinecraftServer server) {

    }

    /**
     * Called when the server starts.
     * @param server The starting server
     */
    public static void onServerStarted(MinecraftServer server) {

    }
}