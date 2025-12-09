package com.t2pellet.teams.network.server;

import com.t2pellet.teams.TeamsHUD;
import com.t2pellet.teams.core.ModTeam;
import com.t2pellet.teams.core.TeamDB;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

/**
 * Packet sent from client to server to leave the current team.
 */
public class C2STeamLeavePacket implements C2SModPacket {


    /**
     * Creates a new team leave packet
     */
    public C2STeamLeavePacket() {}

    /**
     * Decode a team leave packet from the network buffer
     * @param byteBuf The buffer to read from
     */
    public C2STeamLeavePacket( FriendlyByteBuf byteBuf) {

    }

    @Override
    public void write(FriendlyByteBuf to) {

    }

    @Override
    public void handleServer(ServerPlayer player) {
        try {
            TeamDB.getOrMakeDefault(player.server).removePlayerFromTeam(player);
        } catch (ModTeam.TeamException ex) {
            TeamsHUD.LOGGER.error("Failed to remove player {} from team: {}", player.getName().getString(), ex.getMessage(), ex);
        }
    }
}
