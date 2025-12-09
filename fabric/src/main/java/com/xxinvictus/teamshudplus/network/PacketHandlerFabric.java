package com.xxinvictus.teamshudplus.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Function;

import com.xxinvictus.teamshudplus.network.server.C2SModPacket;

/**
 * Fabric-specific packet handler for client-to-server packets.
 */
public class PacketHandlerFabric {

    /**
     * Wraps a C2S packet decoder into a Fabric channel handler.
     * @param decodeFunction Function to decode the packet from buffer
     * @param <MSG> The packet type
     * @return A Fabric play channel handler
     */
    public static <MSG extends C2SModPacket> ServerPlayNetworking.PlayChannelHandler wrapC2S(Function<FriendlyByteBuf, MSG> decodeFunction) {
        return new ServerHandler<>(decodeFunction);
    }

}
