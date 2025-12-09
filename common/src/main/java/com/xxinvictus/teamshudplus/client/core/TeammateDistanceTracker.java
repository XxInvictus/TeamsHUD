package com.xxinvictus.teamshudplus.client.core;

import com.xxinvictus.teamshudplus.platform.Services;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

/**
 * Tracks and updates teammate distances with configurable frequency
 */
public class TeammateDistanceTracker {
    
    private static int tickCounter = 0;
    private static final Minecraft client = Minecraft.getInstance();
    
    /**
     * Called every client tick to update distances based on config frequency
     */
    public static void tick() {
        if (!Services.PLATFORM.getConfig().showTeammateDistance()) {
            return;
        }
        
        int updateFrequency = Services.PLATFORM.getConfig().teammateDistanceUpdateFrequency();
        tickCounter++;
        
        if (tickCounter >= updateFrequency) {
            tickCounter = 0;
            updateAllDistances();
        }
    }
    
    /**
     * Update distances for all teammates
     */
    private static void updateAllDistances() {
        if (client.player == null || client.level == null) {
            return;
        }
        
        for (ClientTeam.Teammate teammate : ClientTeam.INSTANCE.getTeammates()) {
            if (client.player.getUUID().equals(teammate.id)) {
                continue;
            }
            
            Player teammatePlayer = client.level.getPlayerByUUID(teammate.id);
            if (teammatePlayer != null) {
                double distance = calculateDistance(client.player, teammatePlayer);
                teammate.setDistance(distance);
            } else {
                // Player not loaded, mark distance as unknown
                teammate.setDistance(-1.0);
            }
        }
    }
    
    /**
     * Calculate horizontal distance between two players (X-Z plane only)
     */
    private static double calculateDistance(Player player1, Player player2) {
        double diffX = player1.position().x - player2.position().x;
        double diffZ = player1.position().z - player2.position().z;
        return Math.sqrt(diffX * diffX + diffZ * diffZ);
    }
    
    /**
     * Reset tick counter (e.g., when config changes)
     */
    public static void reset() {
        tickCounter = 0;
    }
}
