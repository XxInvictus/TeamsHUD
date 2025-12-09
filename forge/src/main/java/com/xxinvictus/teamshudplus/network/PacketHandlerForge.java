package com.xxinvictus.teamshudplus.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.xxinvictus.teamshudplus.TeamsHUDPlus;
import com.xxinvictus.teamshudplus.network.client.S2CModPacket;
import com.xxinvictus.teamshudplus.network.server.C2SModPacket;

/**
 * Forge-specific network packet handler.
 * Provides packet wrapping and sending functionality for the Forge platform.
 */
public class PacketHandlerForge {

    /** The Forge network channel for this mod */
    public static SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(TeamsHUDPlus.id(TeamsHUDPlus.MODID), () -> "1.0", s -> true, s -> true);;

    /**
     * Wraps a server-to-client packet handler for Forge
     * @param <MSG> The packet type
     * @return BiConsumer that handles the packet on the network thread
     */
    public static <MSG extends S2CModPacket> BiConsumer<MSG, Supplier<NetworkEvent.Context>> wrapS2C() {
        return ((msg, contextSupplier) -> {
            contextSupplier.get().enqueueWork(msg::handleClient);
            contextSupplier.get().setPacketHandled(true);
        });
    }

    /**
     * Wraps a client-to-server packet handler for Forge
     * @param <MSG> The packet type
     * @return BiConsumer that handles the packet on the network thread
     */
    public static <MSG extends C2SModPacket> BiConsumer<MSG, Supplier<NetworkEvent.Context>> wrapC2S() {
        return ((msg, contextSupplier) -> {
            ServerPlayer player = contextSupplier.get().getSender();
            contextSupplier.get().enqueueWork(() -> msg.handleServer(player));
            contextSupplier.get().setPacketHandled(true);
        });
    }

    /**
     * Sends a packet to a client player
     * @param packet The packet to send
     * @param player The target player
     * @param <MSG> The packet type
     */
    public static <MSG> void sendToClient(MSG packet, ServerPlayer player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    /**
     * Sends a packet to the server
     * @param packet The packet to send
     * @param <MSG> The packet type
     */
    public static <MSG> void sendToServer(MSG packet) {
        INSTANCE.sendToServer(packet);
    }
}
