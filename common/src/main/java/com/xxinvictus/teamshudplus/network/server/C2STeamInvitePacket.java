package com.xxinvictus.teamshudplus.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

import com.xxinvictus.teamshudplus.TeamsHUDPlus;
import com.xxinvictus.teamshudplus.core.IHasTeam;
import com.xxinvictus.teamshudplus.core.ModTeam;
import com.xxinvictus.teamshudplus.core.TeamDB;

/**
 * Packet sent from client to server to invite a player to the sender's team.
 */
public class C2STeamInvitePacket implements C2SModPacket {


    String to;
    
    /**
     * Creates a new team invite packet
     * @param to The username of the player to invite
     */
    public C2STeamInvitePacket(String to) {
        this.to = to;
    }

    /**
     * Decode a team invite packet from the network buffer
     * @param byteBuf The buffer to read from
     */
    public C2STeamInvitePacket(FriendlyByteBuf byteBuf) {
        to = byteBuf.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeUtf(this.to);
    }

    @Override
    public void handleServer(ServerPlayer player) {
        UUID to = player.server.getProfileCache().get(this.to).orElseThrow().getId();

        ServerPlayer toPlayer = player.server.getPlayerList().getPlayer(to);

        ModTeam team = ((IHasTeam) player).getTeam();
        if (team == null) {
            TeamsHUDPlus.LOGGER.error("Player {} tried inviting {} but they are not in a team", 
                player.getName().getString(), toPlayer.getName().getString());
        } else {
            try {
                TeamDB.getOrMakeDefault(player.server).invitePlayerToTeam(toPlayer, team);
            } catch (ModTeam.TeamException e) {
                TeamsHUDPlus.LOGGER.error("Failed to invite player {} to team '{}': {}", 
                    toPlayer.getName().getString(), team.getName(), e.getMessage(), e);
            }
        }
    }
}
