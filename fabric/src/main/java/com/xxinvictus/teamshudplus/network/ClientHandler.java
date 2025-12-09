package com.xxinvictus.teamshudplus.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Function;

import com.xxinvictus.teamshudplus.network.client.S2CModPacket;

/**
 * Fabric client-side network packet handler.
 * Receives and processes server-to-client packets on the Fabric platform.
 * @param <MSG> The type of packet being handled
 */
public class ClientHandler<MSG extends S2CModPacket> implements ClientPlayNetworking.PlayChannelHandler {

    private final Function<FriendlyByteBuf, MSG> decodeFunction;

    /**
     * Creates a new client handler
     * @param decodeFunction Function to decode the packet from a buffer
     */
    public ClientHandler(Function<FriendlyByteBuf, MSG> decodeFunction) {
        this.decodeFunction = decodeFunction;
    }

    /**
     * Receives and processes an incoming packet from the server
     * @param client The Minecraft client instance
     * @param handler The client packet listener
     * @param buf The buffer containing the packet data
     * @param responseSender Sender for response packets
     */
    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        MSG decode = decodeFunction.apply(buf);
        client.execute(decode::handleClient);
    }
}
