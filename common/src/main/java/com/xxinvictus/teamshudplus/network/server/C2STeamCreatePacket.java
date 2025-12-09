package com.xxinvictus.teamshudplus.network.server;

import com.xxinvictus.teamshudplus.TeamsHUDPlus;
import com.xxinvictus.teamshudplus.core.TeamDB;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

/**
 * Packet sent from client to server to create a new team
 */
public class C2STeamCreatePacket implements C2SModPacket {

    String team;

    /**
     * Create a team creation packet
     * @param team The name of the team to create
     */
    public C2STeamCreatePacket(String team) {
        this.team = team;
    }

    /**
     * Decode a team creation packet from the network buffer
     * @param byteBuf The buffer to read from
     */
    public C2STeamCreatePacket(FriendlyByteBuf byteBuf) {
        team = byteBuf.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeUtf(team);
    }

    @Override
    public void handleServer(ServerPlayer player) {
        try {
            TeamDB.getOrMakeDefault(player.server).addTeam(team, player);
        } catch (Exception e) {
            TeamsHUDPlus.LOGGER.error("Failed to create team '{}' for player {}: {}", team, player.getName().getString(), e.getMessage(), e);
        }
    }
}
