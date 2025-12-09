package com.t2pellet.teams.network.server;

import com.t2pellet.teams.core.ModTeam;
import com.t2pellet.teams.core.TeamDB;
import com.t2pellet.teams.network.client.S2CTeamRequestedPacket;
import com.t2pellet.teams.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

/**
 * Packet sent from client to server to request joining a team.
 * The server notifies the most senior online team member of the request.
 */
public class C2STeamRequestPacket implements C2SModPacket {


    String name;
    
    /**
     * Creates a new team request packet
     * @param name The name of the team to request joining
     */
    public C2STeamRequestPacket(String name) {
        this.name = name;
    }

    /**
     * Decode a team request packet from the network buffer
     * @param byteBuf The buffer to read from
     */
    public C2STeamRequestPacket(FriendlyByteBuf byteBuf) {
        name = byteBuf.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeUtf(name);
    }

    @Override
    public void handleServer(ServerPlayer player) {
        ModTeam team = TeamDB.getOrMakeDefault(player.server).getTeam(name);
        if (team == null) {
            throw new IllegalArgumentException("Got request to join team " + name + ", but that team doesn't exist");
        } else {
            // Get first online player in list of seniority
            var playerManager = player.server.getPlayerList();
            ServerPlayer seniorPlayer = team.getPlayerUuids()
                    .filter(p -> playerManager.getPlayer(p) != null)
                    .map(playerManager::getPlayer)
                    .findFirst().orElseThrow();
            Services.PLATFORM.sendToClient(new S2CTeamRequestedPacket(name, player.getUUID()), seniorPlayer);
        }
    }
}
