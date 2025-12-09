package com.xxinvictus.teamshudplus.network.server;

import com.xxinvictus.teamshudplus.network.ModPacket;
import net.minecraft.server.level.ServerPlayer;

/**
 * Interface for client-to-server mod packets
 */
public interface C2SModPacket extends ModPacket {

    /**
     * Handle this packet on the server side
     * @param player The player who sent the packet
     */
    void handleServer(ServerPlayer player);

}
