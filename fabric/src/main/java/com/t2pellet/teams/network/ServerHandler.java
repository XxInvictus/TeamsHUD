package com.t2pellet.teams.network;

import com.t2pellet.teams.network.server.C2SModPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.function.Function;

/**
 * Fabric server-side packet handler for processing client-to-server packets.
 * @param <MSG> The packet type extending C2SModPacket
 */
public class ServerHandler<MSG extends C2SModPacket> implements ServerPlayNetworking.PlayChannelHandler {

    private final Function<FriendlyByteBuf, MSG> packetDecoder;

    /**
     * Creates a server handler with the given packet decoder.
     * @param packetDecoder Function to decode packets from buffer
     */
    public ServerHandler(Function<FriendlyByteBuf,MSG> packetDecoder) {
        this.packetDecoder = packetDecoder;
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        MSG decode = packetDecoder.apply(buf);
        server.execute(() -> decode.handleServer(player));
    }
}
