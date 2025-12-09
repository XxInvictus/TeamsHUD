package com.t2pellet.teams.network;

import com.t2pellet.teams.network.client.S2CTeamClearPacket;
import com.t2pellet.teams.network.client.S2CTeamDataPacket;
import com.t2pellet.teams.network.client.S2CTeamInitPacket;
import com.t2pellet.teams.network.client.S2CTeamInviteSentPacket;
import com.t2pellet.teams.network.client.S2CTeamInvitedPacket;
import com.t2pellet.teams.network.client.S2CTeamPlayerDataPacket;
import com.t2pellet.teams.network.client.S2CTeamRequestedPacket;
import com.t2pellet.teams.network.client.S2CTeamUpdatePacket;
import com.t2pellet.teams.network.server.C2STeamCreatePacket;
import com.t2pellet.teams.network.server.C2STeamInvitePacket;
import com.t2pellet.teams.network.server.C2STeamJoinPacket;
import com.t2pellet.teams.network.server.C2STeamKickPacket;
import com.t2pellet.teams.network.server.C2STeamLeavePacket;
import com.t2pellet.teams.network.server.C2STeamRequestPacket;
import com.t2pellet.teams.platform.PhysicalSide;
import com.t2pellet.teams.platform.Platform;
import com.t2pellet.teams.platform.Services;

/**
 * Central registry for all network packets in the mod.
 * Handles registration of both client-to-server and server-to-client packets.
 */
public class CommonPacketHandler {

    /**
     * Registers all network packets for the mod.
     * Registers server-bound packets and conditionally registers client-bound packets.
     */
    public static void registerPackets() {
        Services.PLATFORM.registerServerMessage(C2STeamCreatePacket.class, C2STeamCreatePacket::new);
        Services.PLATFORM.registerServerMessage(C2STeamRequestPacket.class, C2STeamRequestPacket::new);
        Services.PLATFORM.registerServerMessage(C2STeamKickPacket.class, C2STeamKickPacket::new);
        Services.PLATFORM.registerServerMessage(C2STeamLeavePacket.class, C2STeamLeavePacket::new);
        Services.PLATFORM.registerServerMessage(C2STeamInvitePacket.class, C2STeamInvitePacket::new);
        Services.PLATFORM.registerServerMessage(C2STeamJoinPacket.class, C2STeamJoinPacket::new);
        if (Services.PLATFORM.getPlatform() == Platform.FORGE || Services.PLATFORM.getPhysicalSide() == PhysicalSide.CLIENT) {
            registerClientPackets();
        }
    }

    /**
     * Registers all client-bound network packets.
     * Only called on the client side or on Forge (which registers both sides).
     */
    public static void registerClientPackets() {
        Services.PLATFORM.registerClientMessage(S2CTeamPlayerDataPacket.class, S2CTeamPlayerDataPacket::new);
        Services.PLATFORM.registerClientMessage(S2CTeamDataPacket.class, S2CTeamDataPacket::new);
        Services.PLATFORM.registerClientMessage(S2CTeamRequestedPacket.class, S2CTeamRequestedPacket::new);
        Services.PLATFORM.registerClientMessage(S2CTeamClearPacket.class, S2CTeamClearPacket::new);
        Services.PLATFORM.registerClientMessage(S2CTeamInvitedPacket.class, S2CTeamInvitedPacket::new);
        Services.PLATFORM.registerClientMessage(S2CTeamInviteSentPacket.class, S2CTeamInviteSentPacket::new);
        Services.PLATFORM.registerClientMessage(S2CTeamUpdatePacket.class, S2CTeamUpdatePacket::new);
        Services.PLATFORM.registerClientMessage(S2CTeamInitPacket.class, S2CTeamInitPacket::new);
    }
}
