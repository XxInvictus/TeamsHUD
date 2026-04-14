package com.xxinvictus.teamshudplus.core;

import com.mojang.authlib.GameProfile;
import com.xxinvictus.teamshudplus.TeamsHUDPlus;
import com.xxinvictus.teamshudplus.mixin.AdvancementAccessor;
import com.xxinvictus.teamshudplus.network.client.S2CTeamClearPacket;
import com.xxinvictus.teamshudplus.network.client.S2CTeamDataPacket;
import com.xxinvictus.teamshudplus.network.client.S2CTeamInitPacket;
import com.xxinvictus.teamshudplus.network.client.S2CTeamPlayerDataPacket;
import com.xxinvictus.teamshudplus.network.client.S2CTeamUpdatePacket;
import com.xxinvictus.teamshudplus.platform.Services;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Represents a custom team in the mod, extending Minecraft's Team class.
 * Manages team members, online status, shared advancements, and scoreboard integration.
 */
public class ModTeam extends net.minecraft.world.scores.Team {

    /** The name of the team */
    public final String name;
    private final TeamDB teamDB;
    private final Set<UUID> players;
    private final Map<UUID, ServerPlayer> onlinePlayers;
    private final Set<AdvancementHolder> advancements = new LinkedHashSet<>();
    private PlayerTeam scoreboardTeam;

    /**
     * Creates a new team
     * @param scoreboard The server scoreboard
     * @param name The team name
     * @param teamDB The team database
     */
    ModTeam(Scoreboard scoreboard, String name, TeamDB teamDB) {
        this.name = name;
        this.teamDB = teamDB;
        players = new HashSet<>();
        onlinePlayers = new HashMap<>();
        scoreboardTeam = scoreboard.getPlayerTeam(name);
        if (scoreboardTeam == null) {
            scoreboardTeam = scoreboard.addPlayerTeam(name);
        }
    }

    /**
     * Gets the UUID of the team owner (first player who joined)
     * @return The owner's UUID, or null if team is empty
     */
    public UUID getOwner() {
        return players.stream().findFirst().orElse(null);
    }

    /**
     * Checks if a player has permissions to manage the team
     * @param player The player to check
     * @return true if player is the owner or has operator permissions
     */
    public boolean playerHasPermissions(ServerPlayer player) {
        UUID owner = getOwner();
        return (owner != null && owner.equals(player.getUUID())) || player.hasPermissions(2);
    }
    
    /**
     * Gets all currently online team members
     * @return Collection of online server players
     */
    public Collection<ServerPlayer> getOnlinePlayers() {
        return onlinePlayers.values();
    }

    /**
     * Checks if the team has no members
     * @return true if team is empty
     */
    public boolean isEmpty() {
        return players.isEmpty();
    }

    /**
     * Checks if a player is a member of this team
     * @param player The player to check
     * @return true if player is a member
     */
    public boolean hasPlayer(ServerPlayer player) {
        return hasPlayer(player.getUUID());
    }

    /**
     * Checks if a player UUID is a member of this team
     * @param player The player UUID to check
     * @return true if player is a member
     */
    public boolean hasPlayer(UUID player) {
        return players.contains(player);
    }

    /**
     * Adds a player to the team
     * @param player The player to add
     */
    public void addPlayer(ServerPlayer player) {
        addPlayer(player.getUUID());
    }

    /**
     * Removes a player from the team
     * @param player The player to remove
     */
    public void removePlayer(ServerPlayer player) {
        removePlayer(player.getUUID());
    }

    /**
     * Clears all members from the team
     */
    public void clear() {
        var playersCopy = new ArrayList<>(players);
        playersCopy.forEach(player -> removePlayer(player));
        advancements.clear();
    }

    /**
     * Adds a shared advancement to the team
     * @param advancement The advancement to add
     */
    public void addAdvancement(AdvancementHolder advancement) {
        advancements.add(advancement);
    }

    /**
     * Gets all shared advancements for the team
     * @return Set of team advancements
     */
    public Set<AdvancementHolder> getAdvancements() {
        return advancements;
    }

    /**
     * Handles a player coming online
     * @param player The player who came online
     * @param sendPackets Whether to send network packets
     */
    public void playerOnline(ServerPlayer player, boolean sendPackets) {
        onlinePlayers.put(player.getUUID(), player);
        ((IHasTeam) player).setTeam(this);
        // Packets
        if (sendPackets) {
            Services.PLATFORM.sendToClient(new S2CTeamInitPacket(name, playerHasPermissions(player)), player);
            if (onlinePlayers.size() == 1) {
                var players = teamDB.serverLevel.getServer().getPlayerList().getPlayers();
                Services.PLATFORM.sendToClients(new S2CTeamDataPacket(S2CTeamDataPacket.Type.ONLINE, name), players);
            }
            var players = getOnlinePlayers();
            Services.PLATFORM.sendToClients(new S2CTeamPlayerDataPacket(player, S2CTeamPlayerDataPacket.Type.ADD), players);
            for (var teammate : players) {
                Services.PLATFORM.sendToClient(new S2CTeamPlayerDataPacket(teammate, S2CTeamPlayerDataPacket.Type.ADD), player);
            }
        }
        // Advancement Sync
        if (Services.PLATFORM.getConfig().syncAdvancements()) {
            for (AdvancementHolder advancement : getAdvancements()) {
                AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
                for (String criterion : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancement, criterion);
                }
            }
        }
    }

    /**
     * Gets a stream of all player UUIDs in the team
     * @return Stream of player UUIDs
     */
    public Stream<UUID> getPlayerUuids() {
        return players.stream();
    }

    /**
     * Handles a player going offline
     * @param player The player who went offline
     * @param sendPackets Whether to send network packets
     */
    public void playerOffline(ServerPlayer player, boolean sendPackets) {
        onlinePlayers.remove(player.getUUID());
        // Packets
        if (sendPackets) {
            if (isEmpty()) {
                var players = teamDB.serverLevel.getServer().getPlayerList().getPlayers();
                Services.PLATFORM.sendToClients(new S2CTeamDataPacket(S2CTeamDataPacket.Type.OFFLINE, name), players);
            }
            var players = getOnlinePlayers();
            Services.PLATFORM.sendToClients(new S2CTeamPlayerDataPacket(player, S2CTeamPlayerDataPacket.Type.REMOVE), players);
        }
    }

    private void addPlayer(UUID player) {
        players.add(player);
        var playerEntity = teamDB.serverLevel.getServer().getPlayerList().getPlayer(player);
        String playerName = getNameFromUUID(player);
        // Scoreboard
        var playerScoreboardTeam = teamDB.scoreboard.getPlayersTeam(playerName);
        if (playerScoreboardTeam == null || !playerScoreboardTeam.isAlliedTo(scoreboardTeam)) {
            teamDB.scoreboard.addPlayerToTeam(playerName, scoreboardTeam);
        }
        if (playerEntity != null) {
            // Packets
            Services.PLATFORM.sendToClient(new S2CTeamUpdatePacket(name, playerName, S2CTeamUpdatePacket.Action.JOINED, true), playerEntity);
            Services.PLATFORM.sendToClients(new S2CTeamUpdatePacket(name, playerName, S2CTeamUpdatePacket.Action.JOINED, false), getOnlinePlayers());
            playerOnline(playerEntity, true);
            // Advancement Sync
            if (Services.PLATFORM.getConfig().syncAdvancements()) {
                Set<AdvancementHolder> advancements = ((AdvancementAccessor) playerEntity.getAdvancements()).getVisibleAdvancements();
                for (AdvancementHolder advancement : advancements) {
                    if (playerEntity.getAdvancements().getOrStartProgress(advancement).isDone()) {
                        addAdvancement(advancement);
                    }
                }
            }
        }
        teamDB.setDirty();
    }

    private void removePlayer(UUID player) {
        players.remove(player);
        var playerEntity = teamDB.serverLevel.getServer().getPlayerList().getPlayer(player);
        String playerName = getNameFromUUID(player);
        // Scoreboard
        var playerScoreboardTeam = teamDB.scoreboard.getPlayersTeam(playerName);
        if (playerScoreboardTeam != null && playerScoreboardTeam.isAlliedTo(scoreboardTeam)) {
            teamDB.scoreboard.removePlayerFromTeam(playerName, scoreboardTeam);
        }
        // Packets
        if (playerEntity != null) {
            playerOffline(playerEntity, true);
            Services.PLATFORM.sendToClient(new S2CTeamClearPacket(), playerEntity);
            Services.PLATFORM.sendToClient(new S2CTeamUpdatePacket(name, playerName, S2CTeamUpdatePacket.Action.LEFT, true), playerEntity);
            Services.PLATFORM.sendToClients(new S2CTeamUpdatePacket(name, playerName, S2CTeamUpdatePacket.Action.LEFT, false), getOnlinePlayers());
            ((IHasTeam) playerEntity).setTeam(null);
        }
        teamDB.setDirty();
    }

    private String getNameFromUUID(UUID id) {
        if (id == null) {
            return "Unknown";
        }
        var profileCache = teamDB.serverLevel.getServer().getProfileCache();
        if (profileCache == null) {
            return "Unknown";
        }
        return profileCache.get(id).map(GameProfile::getName).orElse("Unknown");
    }

    static ModTeam fromNBT(CompoundTag compound, TeamDB teamDB) {
        ChatFormatting color = ChatFormatting.getByName(compound.getString("colour"));
        if (color == null) {
            color = ChatFormatting.WHITE;
        }
        CollisionRule collisionRule = CollisionRule.byName(compound.getString("collision"));
        if (collisionRule == null) {
            collisionRule = CollisionRule.ALWAYS;
        }
        Visibility deathMessages = Visibility.byName(compound.getString("deathMessages"));
        if (deathMessages == null) {
            deathMessages = Visibility.ALWAYS;
        }
        Visibility nameTags = Visibility.byName(compound.getString("nameTags"));
        if (nameTags == null) {
            nameTags = Visibility.ALWAYS;
        }
        
        ModTeam team = new Builder(compound.getString("name"))
                .setColour(color)
                .setCollisionRule(collisionRule)
                .setDeathMessageVisibilityRule(deathMessages)
                .setNameTagVisibilityRule(nameTags)
                .setFriendlyFireAllowed(compound.getBoolean("friendlyFire"))
                .setShowFriendlyInvisibles(compound.getBoolean("showInvisible"))
                .complete(teamDB);

        ListTag players = compound.getList("players", Tag.TAG_STRING);
        for (var elem : players) {
            try {
                team.addPlayer(UUID.fromString(elem.getAsString()));
            } catch (IllegalArgumentException e) {
                TeamsHUDPlus.LOGGER.error("Failed to parse UUID for team {}: {}", compound.getString("name"), elem.getAsString(), e);
            }
        }

        ListTag advancements = compound.getList("advancements", Tag.TAG_STRING);
        for (var adv : advancements) {
            ResourceLocation id = ResourceLocation.tryParse(adv.getAsString());
            if (id != null) {
                var advancement = teamDB.serverLevel.getServer().getAdvancements().getAdvancement(id);
                if (advancement != null) {
                    team.addAdvancement(advancement);
                }
            }
        }

        return team;
    }

    CompoundTag toNBT() {
        CompoundTag compound = new CompoundTag();
        compound.putString("name", name);
        compound.putString("colour", scoreboardTeam.getColor().getName());
        compound.putString("collision", scoreboardTeam.getCollisionRule().name);
        compound.putString("deathMessages", scoreboardTeam.getDeathMessageVisibility().name);
        compound.putString("nameTags", scoreboardTeam.getNameTagVisibility().name);
        compound.putBoolean("friendlyFire", scoreboardTeam.isAllowFriendlyFire());
        compound.putBoolean("showInvisible", scoreboardTeam.canSeeFriendlyInvisibles());

        ListTag playerList = new ListTag();
        for (var player : players) {
            playerList.add(StringTag.valueOf(player.toString()));
        }
        compound.put("players", playerList);

        ListTag advList = new ListTag();
        for (var advancement : advancements) {
            advList.add(StringTag.valueOf(advancement.id().toString()));
        }
        compound.put("advancements", advList);

        return compound;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MutableComponent getFormattedName(Component name) {
        return scoreboardTeam.getFormattedName(name);
    }

    @Override
    public boolean canSeeFriendlyInvisibles() {
        return scoreboardTeam.canSeeFriendlyInvisibles();
    }

    /**
     * Sets whether friendly invisibles are visible
     * @param value true to show friendly invisible players
     */
    public void setShowFriendlyInvisibles(boolean value) {
        scoreboardTeam.setSeeFriendlyInvisibles(value);
    }

    @Override
    public boolean isAllowFriendlyFire() {
        return scoreboardTeam.isAllowFriendlyFire();
    }

    /**
     * Sets whether friendly fire is allowed
     * @param value true to allow friendly fire
     */
    public void setFriendlyFireAllowed(boolean value) {
        scoreboardTeam.setAllowFriendlyFire(value);
    }

    @Override
    public Visibility getNameTagVisibility() {
        return scoreboardTeam.getNameTagVisibility();
    }

    /**
     * Sets name tag visibility rule
     * @param value The visibility rule to set
     */
    public void setNameTagVisibilityRule(Visibility value) {
        scoreboardTeam.setNameTagVisibility(value);
    }

    @Override
    public ChatFormatting getColor() {
        return scoreboardTeam.getColor();
    }

    /**
     * Sets the team color
     * @param colour The color formatting to set
     */
    public void setColour(ChatFormatting colour) {
        scoreboardTeam.setColor(colour);
    }

    @Override
    public Collection<String> getPlayers() {
        return scoreboardTeam.getPlayers();
    }

    @Override
    public Visibility getDeathMessageVisibility() {
        return scoreboardTeam.getDeathMessageVisibility();
    }

    /**
     * Sets death message visibility rule
     * @param value The visibility rule to set
     */
    public void setDeathMessageVisibilityRule(Visibility value) {
        scoreboardTeam.setDeathMessageVisibility(value);
    }

    @Override
    public CollisionRule getCollisionRule() {
        return scoreboardTeam.getCollisionRule();
    }

    /**
     * Sets the collision rule
     * @param value The collision rule to set
     */
    public void setCollisionRule(CollisionRule value) {
        scoreboardTeam.setCollisionRule(value);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ModTeam team && Objects.equals(team.getName(), this.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    /**
     * Exception thrown when team operations fail
     */
    public static class TeamException extends Exception {
        /**
         * Creates a new team exception
         * @param message The error message component
         */
        public TeamException(Component message) {
            super(message.getString());
        }
    }

    /**
     * Builder for creating configured ModTeam instances
     */
    public static class Builder {

        private final String name;
        private boolean showFriendlyInvisibles = Services.PLATFORM.getConfig().showInvisibleTeammates();
        private boolean friendlyFireAllowed = Services.PLATFORM.getConfig().friendlyFireEnabled();
        private Visibility nameTagVisibilityRule = Services.PLATFORM.getConfig().nameTagVisibility();
        private ChatFormatting colour = Services.PLATFORM.getConfig().colour();
        private Visibility deathMessageVisibilityRule = Services.PLATFORM.getConfig().deathMessageVisibility();
        private CollisionRule collisionRule = Services.PLATFORM.getConfig().collisionRule();

        /**
         * Creates a new team builder
         * @param name The team name
         */
        public Builder(String name) {
            this.name = name;
        }

        /**
         * Sets whether friendly invisibles should be visible
         * @param showFriendlyInvisibles true to show invisible teammates
         * @return this builder
         */
        public Builder setShowFriendlyInvisibles(boolean showFriendlyInvisibles) {
            this.showFriendlyInvisibles = showFriendlyInvisibles;
            return this;
        }

        /**
         * Sets whether friendly fire is allowed
         * @param friendlyFireAllowed true to allow friendly fire
         * @return this builder
         */
        public Builder setFriendlyFireAllowed(boolean friendlyFireAllowed) {
            this.friendlyFireAllowed = friendlyFireAllowed;
            return this;
        }

        /**
         * Sets the name tag visibility rule
         * @param nameTagVisibilityRule The visibility rule
         * @return this builder
         */
        public Builder setNameTagVisibilityRule(Visibility nameTagVisibilityRule) {
            this.nameTagVisibilityRule = nameTagVisibilityRule;
            return this;
        }

        /**
         * Sets the team color
         * @param colour The color formatting
         * @return this builder
         */
        public Builder setColour(ChatFormatting colour) {
            this.colour = colour;
            return this;
        }

        /**
         * Sets the death message visibility rule
         * @param deathMessageVisibilityRule The visibility rule
         * @return this builder
         */
        public Builder setDeathMessageVisibilityRule(Visibility deathMessageVisibilityRule) {
            this.deathMessageVisibilityRule = deathMessageVisibilityRule;
            return this;
        }

        /**
         * Sets the collision rule
         * @param collisionRule The collision rule
         * @return this builder
         */
        public Builder setCollisionRule(CollisionRule collisionRule) {
            this.collisionRule = collisionRule;
            return this;
        }

        /**
         * Completes the builder and creates the team
         * @param teamDB The team database to register with
         * @return The configured team
         */
        public ModTeam complete(TeamDB teamDB) {
            ModTeam team = new ModTeam(teamDB.scoreboard,name,teamDB);
            team.setShowFriendlyInvisibles(showFriendlyInvisibles);
            team.setFriendlyFireAllowed(friendlyFireAllowed);
            team.setNameTagVisibilityRule(nameTagVisibilityRule);
            team.setColour(colour);
            team.setDeathMessageVisibilityRule(deathMessageVisibilityRule);
            team.setCollisionRule(collisionRule);
            return team;
        }
    }

    /**
     * Gets the underlying scoreboard team
     * @return The PlayerTeam instance
     */
    public PlayerTeam getScoreboardTeam() {
        return scoreboardTeam;
    }
}
