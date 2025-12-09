package com.t2pellet.teams.platform.services;

import com.t2pellet.teams.network.client.S2CModPacket;
import com.t2pellet.teams.network.server.C2SModPacket;
import com.t2pellet.teams.platform.MultiloaderConfig;
import com.t2pellet.teams.platform.PhysicalSide;
import com.t2pellet.teams.platform.Platform;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.function.Function;

/**
 * Platform abstraction interface for multiloader support.
 * Provides platform-specific implementations for Forge and Fabric.
 */
public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    Platform getPlatform();
    
    /**
     * Gets the physical side the code is running on
     * @return The physical side (CLIENT or SERVER)
     */
    PhysicalSide getPhysicalSide();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }

    /**
     * Gets the configuration instance for this platform
     * @return The multiloader configuration
     */
    MultiloaderConfig getConfig();

    /**
     * Sends a packet to a specific client player
     * @param msg The packet to send
     * @param player The player to send the packet to
     */
    void sendToClient(S2CModPacket msg, ServerPlayer player);

    /**
     * Sends a packet to multiple client players
     * @param msg The packet to send
     * @param playerList The collection of players to send the packet to
     */
    default void sendToClients(S2CModPacket msg, Collection<ServerPlayer> playerList) {
        playerList.forEach(player -> sendToClient(msg,player));
    }
    
    /**
     * Sends a packet to the server
     * @param msg The packet to send
     */
    void sendToServer(C2SModPacket msg);

    /**
     * Registers a key binding with the platform
     * @param keyMapping The key mapping to register
     */
    void registerKeyBinding(KeyMapping keyMapping);

    /**
     * Registers a client-bound packet type
     * @param packetClass The packet class
     * @param reader Function to decode the packet from a buffer
     * @param <MSG> The packet type
     */
    <MSG extends S2CModPacket> void registerClientMessage(Class<MSG> packetClass, Function<FriendlyByteBuf,MSG> reader);

    /**
     * Registers a server-bound packet type
     * @param packetClass The packet class
     * @param reader Function to decode the packet from a buffer
     * @param <MSG> The packet type
     */
    <MSG extends C2SModPacket> void registerServerMessage(Class<MSG> packetClass, Function<FriendlyByteBuf,MSG> reader);

}