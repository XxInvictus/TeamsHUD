package com.t2pellet.teams.network.server;

import com.t2pellet.teams.TeamsHUD;
import com.t2pellet.teams.core.ModTeam;
import com.t2pellet.teams.core.TeamDB;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

/**
 * Packet sent from client to server to kick a player from a team.
 * The sender must have permissions to kick players from the team.
 */
public class C2STeamKickPacket implements C2SModPacket {

    String name;
    UUID toKick;

    /**
     * Creates a new team kick packet
     * @param team The name of the team
     * @param playerToKick The UUID of the player to kick
     */
    public C2STeamKickPacket(String team, UUID playerToKick) {
        name = team;
        toKick = playerToKick;
    }

    /**
     * Decode a team kick packet from the network buffer
     * @param byteBuf The buffer to read from
     */
    public C2STeamKickPacket(FriendlyByteBuf byteBuf) {
        name = byteBuf.readUtf();
        toKick = byteBuf.readUUID();
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeUtf(name);
        to.writeUUID(toKick);
    }

    @Override
    public void handleServer(ServerPlayer player) {
        ModTeam team = TeamDB.getOrMakeDefault(player.server).getTeam(name);
        if (player != null && team.playerHasPermissions(player)) {
            ServerPlayer kicked = player.server.getPlayerList().getPlayer(toKick);
            try {
                TeamDB.getOrMakeDefault(player.server).removePlayerFromTeam(kicked);
            } catch (ModTeam.TeamException ex) {
                TeamsHUD.LOGGER.error("Failed to kick player {} from team '{}': {}", toKick, name, ex.getMessage(), ex);
            }
        } else {
            TeamsHUD.LOGGER.error("Player {} attempted to kick {} from team '{}' without permissions", 
                player != null ? player.getName().getString() : "null", toKick, name);
        }
    }
}
