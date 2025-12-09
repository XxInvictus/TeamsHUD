package com.t2pellet.teams.network.server;

import com.t2pellet.teams.TeamsHUD;
import com.t2pellet.teams.core.IHasTeam;
import com.t2pellet.teams.core.ModTeam;
import com.t2pellet.teams.core.TeamDB;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

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
            TeamsHUD.LOGGER.error("Player {} tried inviting {} but they are not in a team", 
                player.getName().getString(), toPlayer.getName().getString());
        } else {
            try {
                TeamDB.getOrMakeDefault(player.server).invitePlayerToTeam(toPlayer, team);
            } catch (ModTeam.TeamException e) {
                TeamsHUD.LOGGER.error("Failed to invite player {} to team '{}': {}", 
                    toPlayer.getName().getString(), team.getName(), e.getMessage(), e);
            }
        }
    }
}
