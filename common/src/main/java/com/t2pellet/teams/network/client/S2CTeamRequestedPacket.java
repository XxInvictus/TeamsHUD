package com.t2pellet.teams.network.client;

import com.t2pellet.teams.client.TeamsHUDClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

/**
 * Packet sent from server to client to notify that a player has requested to join the team.
 */
public class S2CTeamRequestedPacket implements S2CModPacket {


    CompoundTag tag = new CompoundTag();
    
    /**
     * Creates a new team requested packet
     * @param name The name of the team being requested
     * @param id The UUID of the player making the request
     */
    public S2CTeamRequestedPacket(String name, UUID id) {
        tag.putString(S2CTeamPlayerDataPacket.NAME_KEY, name);
        tag.putUUID(S2CTeamPlayerDataPacket.ID_KEY, id);
    }

    /**
     * Decode a team requested packet from the network buffer
     * @param byteBuf The buffer to read from
     */
    public S2CTeamRequestedPacket(FriendlyByteBuf byteBuf) {
        tag = byteBuf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeNbt(tag);
    }


    @Override
    public void handleClient() {
        String name = tag.getString(S2CTeamPlayerDataPacket.NAME_KEY);
        UUID id = tag.getUUID(S2CTeamPlayerDataPacket.ID_KEY);
        TeamsHUDClient.handleTeamRequestedPacket(name,id);
    }
}
