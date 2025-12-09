package com.xxinvictus.teamshudplus.config;

import com.xxinvictus.teamshudplus.TeamsHUDPlus;
import com.xxinvictus.teamshudplus.platform.MultiloaderConfig;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.ChatFormatting;
import net.minecraft.world.scores.Team;

/**
 * Fabric configuration implementation using Cloth Config.
 * Contains all mod settings for team defaults, visual options, HUD positioning, and distance tracking.
 */
@Config(name = TeamsHUDPlus.MODID)
public class TeamsConfig implements ConfigData, MultiloaderConfig {

    /** Whether teammates should see each other when invisible */
    @ConfigEntry.Category("ModTeam Defaults")
    public boolean showInvisibleTeammates = true;
    /** Whether friendly fire is enabled between teammates */
    @ConfigEntry.Category("ModTeam Defaults")
    public boolean friendlyFireEnabled = false;
    /** Visibility setting for team name tags */
    @ConfigEntry.Category("ModTeam Defaults")
    public Team.Visibility nameTagVisibility = Team.Visibility.ALWAYS;
    /** Team color formatting */
    @ConfigEntry.Category("ModTeam Defaults")
    public ChatFormatting colour = ChatFormatting.BOLD;
    /** Visibility setting for death messages */
    @ConfigEntry.Category("ModTeam Defaults")
    public Team.Visibility deathMessageVisibility = Team.Visibility.ALWAYS;
    /** Collision rule for team members */
    @ConfigEntry.Category("ModTeam Defaults")
    @Comment("Note that 'push own team' and 'push other teams' are swapped.")
    public Team.CollisionRule collisionRule = Team.CollisionRule.PUSH_OWN_TEAM;
    /** Whether to sync advancements between team members */
    @ConfigEntry.Category("ModTeam Defaults")
    @Comment("Sync advancements between team members")
    public boolean syncAdvancements = true;
    /** Server-enforced maximum compass detection distance */
    @ConfigEntry.Category("ModTeam Defaults")
    @Comment("Server-enforced maximum compass detection distance (in blocks). Client settings cannot exceed this value.")
    public int maxCompassDetectionDistance = 512;

    /** Whether compass HUD is enabled */
    @ConfigEntry.Category("Visual")
    public boolean enableCompassHUD = true;
    /** Whether status HUD is enabled */
    @ConfigEntry.Category("Visual")
    public boolean enableStatusHUD = true;
    /** Toast notification duration in seconds */
    @ConfigEntry.Category("Visual")
    @Comment("How long teams toast notifications should last")
    public int toastDuration = 5;
    /** Whether to show teammate hunger bars */
    @ConfigEntry.Category("Visual")
    @Comment("Show other team members' hunger")
    public boolean showHunger = true;
    /** Maximum compass detection distance */
    @ConfigEntry.Category("Visual")
    @Comment("Maximum detection distance for the compass HUD (in blocks)")
    public int compassDetectionDistance = 128;
    
    /** Status overlay X position */
    @ConfigEntry.Category("HUD Positioning")
    @Comment("X position of the status overlay (-1 for default)")
    public int statusOverlayX = -1;
    /** Status overlay Y position */
    @ConfigEntry.Category("HUD Positioning")
    @Comment("Y position of the status overlay (-1 for default)")
    public int statusOverlayY = -1;
    /** Compass overlay X position */
    @ConfigEntry.Category("HUD Positioning")
    @Comment("X position of the compass overlay (-1 for default)")
    public int compassOverlayX = -1;
    /** Compass overlay Y position */
    @ConfigEntry.Category("HUD Positioning")
    @Comment("Y position of the compass overlay (-1 for default)")
    public int compassOverlayY = -1;
    /** Status overlay scale factor */
    @ConfigEntry.Category("HUD Positioning")
    @Comment("Scale of the status overlay (1.0 = default, 0.1 = 10%, 2.0 = 200%)")
    public float statusOverlayScale = 1.0f;
    /** Compass overlay scale factor */
    @ConfigEntry.Category("HUD Positioning")
    @Comment("Scale of the compass overlay (1.0 = default, 0.1 = 10%, 2.0 = 200%)")
    public float compassOverlayScale = 1.0f;

    /** Whether to show distance to teammates */
    @ConfigEntry.Category("Distance Counter")
    @Comment("Show distance to teammates next to their names")
    public boolean showTeammateDistance = false;
    /** Distance update frequency in ticks */
    @ConfigEntry.Category("Distance Counter")
    @Comment("How often to update teammate distances (in ticks). Higher = less overhead, lower = more accurate. Default: 20 (1 second)")
    public int teammateDistanceUpdateFrequency = 20;
    /** Whether distance is only shown within compass range */
    @ConfigEntry.Category("Distance Counter")
    @Comment("Only show distance when teammate is within compass detection range. Requires showTeammateDistance to be true.")
    public boolean distanceOnlyWithinCompassRange = false;

    @Override
    public boolean showInvisibleTeammates() {
        return showInvisibleTeammates;
    }

    @Override
    public boolean friendlyFireEnabled() {
        return friendlyFireEnabled;
    }

    @Override
    public Team.Visibility nameTagVisibility() {
        return nameTagVisibility;
    }

    @Override
    public ChatFormatting colour() {
        return colour;
    }

    @Override
    public Team.Visibility deathMessageVisibility() {
        return deathMessageVisibility;
    }

    @Override
    public Team.CollisionRule collisionRule() {
        return collisionRule;
    }

    @Override
    public boolean enableCompassHUD() {
        return enableCompassHUD;
    }

    @Override
    public boolean enableStatusHUD() {
        return enableStatusHUD;
    }

    @Override
    public int toastDuration() {
        return toastDuration;
    }

    @Override
    public boolean showHunger() {
        return showHunger;
    }

    @Override
    public boolean syncAdvancements() {
        return syncAdvancements;
    }

    @Override
    public int compassDetectionDistance() {
        return compassDetectionDistance;
    }

    @Override
    public int maxCompassDetectionDistance() {
        return maxCompassDetectionDistance;
    }

    @Override
    public int statusOverlayX() {
        return statusOverlayX;
    }

    @Override
    public int statusOverlayY() {
        return statusOverlayY;
    }

    @Override
    public int compassOverlayX() {
        return compassOverlayX;
    }

    @Override
    public int compassOverlayY() {
        return compassOverlayY;
    }

    @Override
    public float statusOverlayScale() {
        return statusOverlayScale;
    }

    @Override
    public float compassOverlayScale() {
        return compassOverlayScale;
    }

    @Override
    public void setStatusOverlayX(int x) {
        this.statusOverlayX = x;
    }

    @Override
    public void setStatusOverlayY(int y) {
        this.statusOverlayY = y;
    }

    @Override
    public void setCompassOverlayX(int x) {
        this.compassOverlayX = x;
    }

    @Override
    public void setCompassOverlayY(int y) {
        this.compassOverlayY = y;
    }

    @Override
    public void setStatusOverlayScale(float scale) {
        this.statusOverlayScale = scale;
    }

    @Override
    public void setCompassOverlayScale(float scale) {
        this.compassOverlayScale = scale;
    }

    @Override
    public boolean showTeammateDistance() {
        return showTeammateDistance;
    }

    @Override
    public int teammateDistanceUpdateFrequency() {
        return teammateDistanceUpdateFrequency;
    }

    @Override
    public boolean distanceOnlyWithinCompassRange() {
        return distanceOnlyWithinCompassRange;
    }
}
