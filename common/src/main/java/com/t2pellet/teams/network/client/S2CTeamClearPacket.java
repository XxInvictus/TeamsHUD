package com.t2pellet.teams.network.client;

import com.t2pellet.teams.client.core.ClientTeam;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Packet sent from server to client to clear the player's team data.
 * Resets the client team state.
 */
public class S2CTeamClearPacket implements S2CModPacket {

    /**
     * Creates a new team clear packet
     */
    public S2CTeamClearPacket() {
    }

    /**
     * Decode a team clear packet from the network buffer
     * @param byteBuf The buffer to read from (unused as packet has no data)
     */
    public S2CTeamClearPacket(FriendlyByteBuf byteBuf) {
    }

    @Override
    public void write(FriendlyByteBuf to) {

    }

    @Override
    public void handleClient() {
        ClientTeam.INSTANCE.reset();
    }
}
