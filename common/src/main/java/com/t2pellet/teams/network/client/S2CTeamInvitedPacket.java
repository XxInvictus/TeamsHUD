package com.t2pellet.teams.network.client;

import com.t2pellet.teams.client.TeamsHUDClient;
import com.t2pellet.teams.core.ModTeam;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Packet sent from server to client to notify a player they have been invited to a team.
 */
public class S2CTeamInvitedPacket implements S2CModPacket {

    private final String team;

    /**
     * Creates a new team invited packet
     * @param team The team the player is being invited to
     */
    public S2CTeamInvitedPacket(ModTeam team) {
        this.team = team.getName();
    }

    /**
     * Decode a team invited packet from the network buffer
     * @param byteBuf The buffer to read from
     */
    public S2CTeamInvitedPacket(FriendlyByteBuf byteBuf) {
        team = byteBuf.readUtf();
    }

    @Override
    public void handleClient() {
        TeamsHUDClient.handleTeamInvitedPacket(team);
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeUtf(team);
    }

}
