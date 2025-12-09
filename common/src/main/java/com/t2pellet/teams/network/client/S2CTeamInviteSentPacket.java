package com.t2pellet.teams.network.client;

import com.t2pellet.teams.client.TeamsHUDClient;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Packet sent from server to client to notify that a team invitation was sent successfully.
 */
public class S2CTeamInviteSentPacket implements S2CModPacket {

    String team;
    String player;

    /**
     * Creates a new team invite sent packet
     * @param team The name of the team
     * @param player The name of the player who was invited
     */
    public S2CTeamInviteSentPacket(String team, String player) {
        this.team = team;
        this.player = player;
    }

    /**
     * Decode a team invite sent packet from the network buffer
     * @param byteBuf The buffer to read from
     */
    public S2CTeamInviteSentPacket(FriendlyByteBuf byteBuf) {
        team = byteBuf.readUtf();
        player = byteBuf.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeUtf(team);
        to.writeUtf(player);
    }

    @Override
    public void handleClient() {
        TeamsHUDClient.handleTeamInviteSentPacket(team,player);
    }
}
