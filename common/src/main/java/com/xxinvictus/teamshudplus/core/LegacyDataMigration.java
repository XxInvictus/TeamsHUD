package com.xxinvictus.teamshudplus.core;

import com.xxinvictus.teamshudplus.TeamsHUDPlus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;

/**
 * Optional helper class for migrating team data from previous mod versions.
 * 
 * This is mainly provided as a safety measure. Since the NBT keys and file names
 * haven't changed between TeamsHUD and TeamsHUDPlus, migration should happen
 * automatically. However, this class can be used to:
 * 
 * 1. Verify that old data exists and can be read
 * 2. Log migration status for debugging
 * 3. Handle edge cases where manual intervention might be needed
 * 
 * Usage: Call {@link #checkForLegacyData(MinecraftServer)} during server startup
 * to verify migration status.
 */
public class LegacyDataMigration {
    
    private static final String TEAMS_DATA_FILE = "teams.dat";
    
    /**
     * Checks if legacy team data exists and logs migration status.
     * 
     * This method is non-destructive and only reads existing data to verify
     * it can be loaded properly. The actual loading is handled by TeamDB.
     * 
     * @param server The Minecraft server instance
     * @return true if legacy data was found and is readable, false otherwise
     */
    public static boolean checkForLegacyData(MinecraftServer server) {
        try {
            File worldDir = server.getWorldPath(LevelResource.ROOT).toFile();
            File dataDir = new File(worldDir, "data");
            File teamsFile = new File(dataDir, TEAMS_DATA_FILE);
            
            if (teamsFile.exists()) {
                TeamsHUDPlus.LOGGER.info("Found existing team data at: {}", teamsFile.getAbsolutePath());
                
                try {
                    CompoundTag nbt = NbtIo.readCompressed(teamsFile);
                    if (nbt != null && nbt.contains("teams")) {
                        int teamCount = nbt.getList("teams", 10).size();
                        TeamsHUDPlus.LOGGER.info("Team data contains {} team(s). Data will be loaded automatically.", teamCount);
                        return true;
                    } else {
                        TeamsHUDPlus.LOGGER.warn("Team data file exists but appears to be empty or corrupted.");
                    }
                } catch (IOException e) {
                    TeamsHUDPlus.LOGGER.error("Failed to read existing team data. Migration may fail.", e);
                }
            } else {
                TeamsHUDPlus.LOGGER.info("No existing team data found. This is normal for new worlds.");
            }
        } catch (Exception e) {
            TeamsHUDPlus.LOGGER.error("Error checking for legacy data", e);
        }
        
        return false;
    }
    
    /**
     * Gets information about the team data file for debugging purposes.
     * 
     * @param server The Minecraft server instance
     * @return A human-readable string describing the team data status
     */
    public static String getDataStatus(MinecraftServer server) {
        try {
            File worldDir = server.getWorldPath(LevelResource.ROOT).toFile();
            File dataDir = new File(worldDir, "data");
            File teamsFile = new File(dataDir, TEAMS_DATA_FILE);
            
            if (!teamsFile.exists()) {
                return "No team data file found (new world or no teams created yet)";
            }
            
            long fileSizeKB = teamsFile.length() / 1024;
            String lastModified = new java.util.Date(teamsFile.lastModified()).toString();
            
            try {
                CompoundTag nbt = NbtIo.readCompressed(teamsFile);
                if (nbt != null && nbt.contains("teams")) {
                    int teamCount = nbt.getList("teams", 10).size();
                    return String.format("Team data: %d team(s), %dKB, last modified: %s", 
                        teamCount, fileSizeKB, lastModified);
                }
            } catch (IOException e) {
                return String.format("Team data file exists (%dKB) but cannot be read: %s", 
                    fileSizeKB, e.getMessage());
            }
            
            return String.format("Team data file exists (%dKB) but appears empty", fileSizeKB);
        } catch (Exception e) {
            return "Error checking team data: " + e.getMessage();
        }
    }
    
    /**
     * Verifies that a player's team assignment can be read from their NBT data.
     * 
     * This is useful for debugging player-specific migration issues.
     * Note: This should only be called when the player is offline, as online
     * player data is managed in memory.
     * 
     * @param server The Minecraft server instance
     * @param playerUUID The player's UUID as a string
     * @return The team name if found, null otherwise
     */
    public static String checkPlayerTeamData(MinecraftServer server, String playerUUID) {
        try {
            File worldDir = server.getWorldPath(LevelResource.ROOT).toFile();
            File playerDataDir = new File(worldDir, "playerdata");
            File playerFile = new File(playerDataDir, playerUUID + ".dat");
            
            if (!playerFile.exists()) {
                TeamsHUDPlus.LOGGER.debug("No player data file found for UUID: {}", playerUUID);
                return null;
            }
            
            CompoundTag nbt = NbtIo.readCompressed(playerFile);
            if (nbt.contains("playerTeam")) {
                String teamName = nbt.getString("playerTeam");
                TeamsHUDPlus.LOGGER.debug("Player {} is in team: {}", playerUUID, teamName);
                return teamName;
            } else {
                TeamsHUDPlus.LOGGER.debug("Player {} has no team assignment", playerUUID);
            }
        } catch (Exception e) {
            TeamsHUDPlus.LOGGER.error("Error reading player team data for {}", playerUUID, e);
        }
        
        return null;
    }
}
