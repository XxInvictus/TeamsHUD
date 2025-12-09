package com.xxinvictus.teamshudplus.config;

import com.xxinvictus.teamshudplus.TeamsHUDPlus;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for migrating configuration from the old 'teams' mod to 'teamshudplus'.
 * 
 * This class handles automatic migration of config files on first load:
 * - Fabric: teams.json → teamshudplus.json
 * - Forge: teams-client.toml / teams-common.toml → teamshudplus-client.toml / teamshudplus-common.toml
 */
public class ConfigMigration {
    
    /**
     * Attempts to migrate Fabric config from old 'teams.json' to new 'teamshudplus.json'.
     * Only performs migration if the new config doesn't exist but the old one does.
     * 
     * @param configDir The config directory path
     * @return true if migration was performed, false otherwise
     */
    public static boolean migrateFabricConfig(Path configDir) {
        Path oldConfig = configDir.resolve("teams.json");
        Path newConfig = configDir.resolve("teamshudplus.json");
        
        return migrateConfigFile(oldConfig, newConfig, "Fabric");
    }
    
    /**
     * Attempts to migrate Forge client config from old to new.
     * Only performs migration if the new config doesn't exist but the old one does.
     * 
     * @param configDir The config directory path
     * @return true if migration was performed, false otherwise
     */
    public static boolean migrateForgeClientConfig(Path configDir) {
        Path oldConfig = configDir.resolve("teams-client.toml");
        Path newConfig = configDir.resolve("teamshudplus-client.toml");
        
        return migrateConfigFile(oldConfig, newConfig, "Forge Client");
    }
    
    /**
     * Attempts to migrate Forge server/common config from old to new.
     * Only performs migration if the new config doesn't exist but the old one does.
     * 
     * @param configDir The config directory path
     * @return true if migration was performed, false otherwise
     */
    public static boolean migrateForgeServerConfig(Path configDir) {
        // Try both possible old names
        Path oldConfigCommon = configDir.resolve("teams-common.toml");
        Path oldConfigServer = configDir.resolve("teams-server.toml");
        Path newConfig = configDir.resolve("teamshudplus-server.toml");
        
        // Try common first, then server
        Path oldConfig = Files.exists(oldConfigCommon) ? oldConfigCommon : oldConfigServer;
        
        return migrateConfigFile(oldConfig, newConfig, "Forge Server");
    }
    
    /**
     * Internal method to perform the actual file migration.
     * 
     * @param oldConfig Path to the old config file
     * @param newConfig Path to the new config file
     * @param configType Description of config type for logging
     * @return true if migration was performed, false otherwise
     */
    private static boolean migrateConfigFile(Path oldConfig, Path newConfig, String configType) {
        // Only migrate if new config doesn't exist
        if (Files.exists(newConfig)) {
            return false;
        }
        
        // Only migrate if old config exists
        if (!Files.exists(oldConfig)) {
            return false;
        }
        
        try {
            TeamsHUDPlus.LOGGER.info("Found old {} config at: {}", configType, oldConfig);
            TeamsHUDPlus.LOGGER.info("Migrating to: {}", newConfig);
            
            // Copy the file
            Files.copy(oldConfig, newConfig);
            
            TeamsHUDPlus.LOGGER.info("Successfully migrated {} config! Your settings have been preserved.", configType);
            TeamsHUDPlus.LOGGER.info("Note: The old config file has been kept for safety. You may delete it if migration was successful.");
            
            return true;
        } catch (Exception e) {
            TeamsHUDPlus.LOGGER.error("Failed to migrate {} config from {} to {}", 
                configType, oldConfig, newConfig, e);
            TeamsHUDPlus.LOGGER.error("You may need to manually copy your settings from the old config file.");
            return false;
        }
    }
    
    /**
     * Gets the config directory path based on the current working directory.
     * 
     * @return Path to the config directory
     */
    public static Path getConfigDirectory() {
        return Paths.get("config");
    }
}
