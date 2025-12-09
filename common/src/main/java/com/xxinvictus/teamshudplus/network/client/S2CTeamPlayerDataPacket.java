package com.xxinvictus.teamshudplus.network.client;

import com.mojang.authlib.properties.Property;
import com.xxinvictus.teamshudplus.client.TeamsHUDPlusClient;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

/**
 * Packet sent from server to client to synchronize teammate player data.
 * Contains player information including health, hunger, skin, and UUID.
 */
public class S2CTeamPlayerDataPacket implements S2CModPacket {

    /** NBT key for player UUID */
    public static final String ID_KEY = "playerUuid";
    /** NBT key for player name */
    public static final String NAME_KEY = "playerName";
    /** NBT key for player skin texture */
    public static final String SKIN_KEY = "playerSkin";
    /** NBT key for player skin signature */
    public static final String SKIN_SIG_KEY = "playerSkinSignature";
    /** NBT key for player health */
    public static final String HEALTH_KEY = "playerHealth";
    /** NBT key for player hunger */
    public static final String HUNGER_KEY = "playerHunger";
    /** NBT key for action type */
    public static final String TYPE_KEY = "actionType";

    /**
     * The type of player data operation
     */
    public enum Type {
        /** Add a new teammate */
        ADD,
        /** Update existing teammate data */
        UPDATE,
        /** Remove a teammate */
        REMOVE,
    }

    CompoundTag tag = new CompoundTag();

    /**
     * Creates a new team player data packet from a server player
     * @param player The player whose data to send
     * @param type The type of operation
     */
    public S2CTeamPlayerDataPacket(ServerPlayer player, Type type) {
        var health = player.getHealth();
        var hunger = player.getFoodData().getFoodLevel();
        tag.putUUID(ID_KEY, player.getUUID());
        tag.putString(TYPE_KEY, type.toString());
        switch (type) {
            case ADD -> {
                tag.putString(NAME_KEY, player.getName().getString());
                var properties = player.getGameProfile().getProperties();
                Property skin = null;
                if (properties.containsKey("textures")) {
                    skin = properties.get("textures").iterator().next();
                }
                tag.putString(SKIN_KEY, skin != null ? skin.getValue() : "");
                tag.putString(SKIN_SIG_KEY, skin != null ?
                        skin.getSignature() != null ? skin.getSignature() : ""
                        : "");
                tag.putFloat(HEALTH_KEY, health);
                tag.putInt(HUNGER_KEY, hunger);
            }
            case UPDATE -> {
                tag.putFloat(HEALTH_KEY, health);
                tag.putInt(HUNGER_KEY, hunger);
            }
            case REMOVE -> {
                // No additional data needed for REMOVE
            }
        }
    }

    /**
     * Deserializes the packet from the network buffer.
     * @param byteBuf The buffer to read from
     */
    public S2CTeamPlayerDataPacket(FriendlyByteBuf byteBuf) {
        tag = byteBuf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeNbt(tag);
    }

    @Override
    public void handleClient() {
        TeamsHUDPlusClient.handleTeamPlayerDataPacket(tag);
    }
}
