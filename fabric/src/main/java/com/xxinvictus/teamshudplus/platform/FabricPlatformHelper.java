package com.xxinvictus.teamshudplus.platform;

import com.xxinvictus.teamshudplus.TeamsHUDPlusFabric;
import com.xxinvictus.teamshudplus.network.PacketPayload;
import com.xxinvictus.teamshudplus.network.client.S2CModPacket;
import com.xxinvictus.teamshudplus.network.server.C2SModPacket;
import com.xxinvictus.teamshudplus.platform.services.IPlatformHelper;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Fabric platform implementation of IPlatformHelper.
 * Uses the modern payload-based networking API.
 */
public class FabricPlatformHelper implements IPlatformHelper {
    MultiloaderConfig config = TeamsHUDPlusFabric.getConfig();

    @SuppressWarnings("rawtypes")
    private static final Map<Class<?>, CustomPacketPayload.Type> typeRegistry = new HashMap<>();

    @Override
    public Platform getPlatform() {
        return Platform.FABRIC;
    }

    @Override
    public PhysicalSide getPhysicalSide() {
        return switch (FabricLoader.getInstance().getEnvironmentType()) {
            case CLIENT -> PhysicalSide.CLIENT;
            case SERVER -> PhysicalSide.SERVER;
        };
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public MultiloaderConfig getConfig() {
        return config;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void sendToClient(S2CModPacket msg, ServerPlayer player) {
        CustomPacketPayload.Type<PacketPayload<S2CModPacket>> type = typeRegistry.get(msg.getClass());
        ServerPlayNetworking.send(player, new PacketPayload<>(msg, type));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void sendToServer(C2SModPacket msg) {
        CustomPacketPayload.Type<PacketPayload<C2SModPacket>> type = typeRegistry.get(msg.getClass());
        ClientPlayNetworking.send(new PacketPayload<>(msg, type));
    }

    @Override
    public void registerKeyBinding(KeyMapping keyMapping) {
        KeyBindingHelper.registerKeyBinding(keyMapping);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <MSG extends S2CModPacket> void registerClientMessage(Class<MSG> packetClass, Function<FriendlyByteBuf, MSG> reader) {
        var type = PacketPayload.createType(packetClass);
        var codec = PacketPayload.createCodec(type, reader);
        typeRegistry.put(packetClass, type);

        PayloadTypeRegistry.playS2C().register(type, codec);
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
            context.client().execute(() -> ((S2CModPacket) payload.packet()).handleClient());
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <MSG extends C2SModPacket> void registerServerMessage(Class<MSG> packetClass, Function<FriendlyByteBuf, MSG> reader) {
        var type = PacketPayload.createType(packetClass);
        var codec = PacketPayload.createCodec(type, reader);
        typeRegistry.put(packetClass, type);

        PayloadTypeRegistry.playC2S().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
            context.player().server.execute(() -> ((C2SModPacket) payload.packet()).handleServer(context.player()));
        });
    }
}
