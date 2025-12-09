package com.xxinvictus.teamshudplus.network.server;

import com.xxinvictus.teamshudplus.TeamsHUDPlus;
import com.xxinvictus.teamshudplus.core.ModTeam;
import com.xxinvictus.teamshudplus.core.TeamDB;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

/**
 * Packet sent from client to server to join a team
 */
public class C2STeamJoinPacket implements C2SModPacket {

    String team;
    /**
     * Create a team join packet
     * @param team The name of the team to join
     */
    public C2STeamJoinPacket(String team) {
        this.team = team;
    }

    /**
     * Decode a team join packet from the network buffer
     * @param byteBuf The buffer to read from
     */
    public C2STeamJoinPacket(FriendlyByteBuf byteBuf) {
        team = byteBuf.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeUtf(team);
    }
    @Override
    public void handleServer(ServerPlayer player) {
        ModTeam team = TeamDB.getOrMakeDefault(player.server).getTeam(this.team);
        try {
            TeamDB.getOrMakeDefault(player.server).addPlayerToTeam(player, team);
        } catch (ModTeam.TeamException ex) {
            TeamsHUDPlus.LOGGER.error("Failed to add player {} to team '{}': {}", 
                player.getName().getString(), this.team, ex.getMessage(), ex);
        }
    }
}
