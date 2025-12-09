package com.xxinvictus.teamshudplus.network;

import net.minecraft.network.FriendlyByteBuf;

/**
 * Base interface for all network packets in the mod.
 */
public interface ModPacket {
    /**
     * Writes the packet data to a network buffer
     * @param to The buffer to write to
     */
    void write(FriendlyByteBuf to);

}
