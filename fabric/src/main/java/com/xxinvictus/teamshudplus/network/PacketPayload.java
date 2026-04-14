package com.xxinvictus.teamshudplus.network;

import com.xxinvictus.teamshudplus.TeamsHUDPlus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Locale;
import java.util.function.Function;

/**
 * Generic wrapper that adapts ModPacket instances to the CustomPacketPayload system.
 * @param <T> The wrapped ModPacket type
 */
public record PacketPayload<T extends ModPacket>(
    T packet,
    CustomPacketPayload.Type<PacketPayload<T>> payloadType
) implements CustomPacketPayload {

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return payloadType;
    }

    /**
     * Creates a CustomPacketPayload.Type for the given packet class.
     */
    public static <T extends ModPacket> CustomPacketPayload.Type<PacketPayload<T>> createType(Class<T> clazz) {
        return new CustomPacketPayload.Type<>(TeamsHUDPlus.id(clazz.getName().toLowerCase(Locale.ROOT)));
    }

    /**
     * Creates a StreamCodec for the given packet type.
     */
    public static <T extends ModPacket> StreamCodec<FriendlyByteBuf, PacketPayload<T>> createCodec(
            CustomPacketPayload.Type<PacketPayload<T>> type,
            Function<FriendlyByteBuf, T> decoder) {
        return StreamCodec.of(
            (buf, payload) -> payload.packet().write(buf),
            buf -> new PacketPayload<>(decoder.apply(buf), type)
        );
    }
}
