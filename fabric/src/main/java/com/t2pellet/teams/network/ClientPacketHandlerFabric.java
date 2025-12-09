package com.t2pellet.teams.network;

import com.t2pellet.teams.network.client.S2CModPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Function;

/**
 * Fabric-specific wrapper for client-side packet handlers.
 * Provides utility methods for creating packet handlers on the Fabric platform.
 */
public class ClientPacketHandlerFabric {

    /**
     * Wraps a server-to-client packet decoder function in a Fabric channel handler
     * @param decodeFunction Function to decode the packet from a buffer
     * @param <MSG> The type of packet being handled
     * @return A configured Fabric play channel handler
     */
    public static <MSG extends S2CModPacket> ClientPlayNetworking.PlayChannelHandler wrapS2C(Function<FriendlyByteBuf,MSG> decodeFunction) {
        return new ClientHandler<>(decodeFunction);
    }

}
