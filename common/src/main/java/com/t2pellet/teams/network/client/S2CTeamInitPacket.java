package com.t2pellet.teams.network.client;

import com.t2pellet.teams.client.core.ClientTeam;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Packet sent from server to client to initialize the player's team.
 * Contains team name and permission status.
 */
public class S2CTeamInitPacket implements S2CModPacket {

    private final String name;
    private final boolean perms;

    /**
     * Creates a new team init packet
     * @param name The name of the team
     * @param hasPermissions Whether the player has team permissions
     */
    public S2CTeamInitPacket(String name, boolean hasPermissions) {
        this.name = name;
        this.perms = hasPermissions;
    }

    /**
     * Decode a team init packet from the network buffer
     * @param byteBuf The buffer to read from
     */
    public S2CTeamInitPacket(FriendlyByteBuf byteBuf) {
        name = byteBuf.readUtf();
        perms = byteBuf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeUtf(name);
        to.writeBoolean(perms);
    }

    @Override
    public void handleClient() {
        ClientTeam.INSTANCE.init(name,perms);
    }
}
