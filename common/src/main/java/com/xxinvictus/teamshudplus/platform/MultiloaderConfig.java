package com.xxinvictus.teamshudplus.platform;

import net.minecraft.ChatFormatting;
import net.minecraft.world.scores.Team;

/**
 * Configuration interface for multiloader support.
 * Provides access to all mod configuration options across platforms.
 */
public interface MultiloaderConfig {

    /** @return Whether invisible teammates should be shown */
    boolean showInvisibleTeammates();
    /** @return Whether friendly fire is enabled */
    boolean friendlyFireEnabled();
    /** @return Name tag visibility rule */
    Team.Visibility nameTagVisibility();
    /** @return Team color */
    ChatFormatting colour();
    /** @return Death message visibility rule */
    Team.Visibility deathMessageVisibility();
    /** @return Collision rule */
    Team.CollisionRule collisionRule();

    /** @return Whether compass HUD is enabled */
    boolean enableCompassHUD();
    /** @return Whether status HUD is enabled */
    boolean enableStatusHUD();
    /** @return Toast notification duration in ticks */
    int toastDuration();
    /** @return Whether hunger bar should be shown */
    boolean showHunger();
    /** @return Whether advancements should be synced */
    boolean syncAdvancements();
    /** @return Compass detection distance in blocks */
    int compassDetectionDistance();
    /** @return Maximum compass detection distance in blocks */
    int maxCompassDetectionDistance();
    
    /** @return Status overlay X position (-1 for default) */
    int statusOverlayX();
    /** @return Status overlay Y position (-1 for default) */
    int statusOverlayY();
    /** @return Compass overlay X position (-1 for default) */
    int compassOverlayX();
    /** @return Compass overlay Y position (-1 for default) */
    int compassOverlayY();
    /** @return Status overlay scale factor */
    float statusOverlayScale();
    /** @return Compass overlay scale factor */
    float compassOverlayScale();
    
    /** @param x Status overlay X position */
    void setStatusOverlayX(int x);
    /** @param y Status overlay Y position */
    void setStatusOverlayY(int y);
    /** @param x Compass overlay X position */
    void setCompassOverlayX(int x);
    /** @param y Compass overlay Y position */
    void setCompassOverlayY(int y);
    /** @param scale Status overlay scale factor */
    void setStatusOverlayScale(float scale);
    /** @param scale Compass overlay scale factor */
    void setCompassOverlayScale(float scale);
    
    /** @return Whether teammate distance should be displayed */
    boolean showTeammateDistance();
    /** @return Distance update frequency in ticks */
    int teammateDistanceUpdateFrequency();
    /** @return Whether distance is only shown within compass range */
    boolean distanceOnlyWithinCompassRange();
}
