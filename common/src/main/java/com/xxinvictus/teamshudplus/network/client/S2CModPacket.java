package com.xxinvictus.teamshudplus.network.client;

import com.xxinvictus.teamshudplus.network.ModPacket;

/**
 * Base interface for all server-to-client network packets.
 * Extends ModPacket and adds client-side handling capability.
 */
public interface S2CModPacket extends ModPacket {

    /**
     * Handles this packet on the client side.
     * Called when the packet is received by the client.
     */
    void handleClient();

}
