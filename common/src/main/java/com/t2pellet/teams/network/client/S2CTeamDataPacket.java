package com.t2pellet.teams.network.client;

import com.t2pellet.teams.client.core.ClientTeamDB;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Packet sent from server to client to update team database state.
 * Handles adding, removing, and online status changes for teams.
 */
public class S2CTeamDataPacket implements S2CModPacket {

    private static final String TEAM_KEY = "teamName";
    private static final String TYPE_KEY = "type";

    /**
     * The type of team data operation
     */
    public enum Type {
        /** Add teams to the database */
        ADD,
        /** Remove teams from the database */
        REMOVE,
        /** Mark teams as online */
        ONLINE,
        /** Mark teams as offline */
        OFFLINE,
        /** Clear all team data */
        CLEAR
    }

    CompoundTag tag = new CompoundTag();

    /**
     * Creates a new team data packet
     * @param type The type of operation to perform
     * @param teams The team names to operate on
     */
    public S2CTeamDataPacket(Type type, String... teams) {
        ListTag nbtList = new ListTag();
        for (var team : teams) {
            nbtList.add(StringTag.valueOf(team));
        }
        tag.put(TEAM_KEY, nbtList);
        tag.putString(TYPE_KEY, type.name());
    }

    /**
     * Decode a team data packet from the network buffer
     * @param byteBuf The buffer to read from
     */
    public S2CTeamDataPacket(FriendlyByteBuf byteBuf) {
        tag = byteBuf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeNbt(tag);
    }

    @Override
    public void handleClient() {
        Type type = Type.valueOf(tag.getString(TYPE_KEY));
        ListTag nbtList = tag.getList(TEAM_KEY, Tag.TAG_STRING);
        for (var elem : nbtList) {
            String team = elem.getAsString();
            switch (type) {
                case ADD -> ClientTeamDB.INSTANCE.addTeam(team);
                case REMOVE -> ClientTeamDB.INSTANCE.removeTeam(team);
                case ONLINE -> ClientTeamDB.INSTANCE.teamOnline(team);
                case OFFLINE -> ClientTeamDB.INSTANCE.teamOffline(team);
                case CLEAR -> ClientTeamDB.INSTANCE.clear();
            }
        }
    }
}
