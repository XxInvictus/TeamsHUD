package com.xxinvictus.teamshudplus.network.client;

import com.xxinvictus.teamshudplus.client.TeamsHUDPlusClient;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Packet sent from server to client to notify about team member changes.
 * Contains information about players joining or leaving a team.
 */
public class S2CTeamUpdatePacket implements S2CModPacket {

    /**
     * The type of team update action
     */
    public enum Action {
        /** A player joined the team */
        JOINED,
        /** A player left the team */
        LEFT
    }

    private static final String TEAM_KEY = "teamName";
    private static final String PLAYER_KEY = "playerName";
    private static final String ACTION_KEY = "action";
    private static final String LOCAL_KEY = "local";

    CompoundTag tag = new CompoundTag();

    /**
     * Creates a new team update packet
     * @param team The name of the team
     * @param player The name of the player who joined/left
     * @param action The action that occurred (JOINED or LEFT)
     * @param isLocal Whether this update affects the local player
     */
    public S2CTeamUpdatePacket(String team, String player, Action action, boolean isLocal) {
        tag.putString(TEAM_KEY, team);
        tag.putString(PLAYER_KEY, player);
        tag.putString(ACTION_KEY, action.name());
        tag.putBoolean(LOCAL_KEY, isLocal);
    }

    /**
     * Decode a team update packet from the network buffer
     * @param byteBuf The buffer to read from
     */
    public S2CTeamUpdatePacket(FriendlyByteBuf byteBuf) {
        tag = byteBuf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeNbt(tag);
    }


    @Override
    public void handleClient() {
        String team = tag.getString(TEAM_KEY);
        String player = tag.getString(PLAYER_KEY);
        S2CTeamUpdatePacket.Action action = S2CTeamUpdatePacket.Action.valueOf(tag.getString(ACTION_KEY));
        boolean isLocal = tag.getBoolean(LOCAL_KEY);
        TeamsHUDPlusClient.handleTeamUpdatePacket(team,player,action,isLocal);
    }
}
