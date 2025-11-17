package com.t2pellet.teams.config;

import com.t2pellet.teams.TeamsHUD;
import com.t2pellet.teams.platform.MultiloaderConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.ChatFormatting;
import net.minecraft.world.scores.Team;

@Config(name = TeamsHUD.MODID)
public class TeamsConfig implements ConfigData, MultiloaderConfig {

    @ConfigEntry.Category("ModTeam Defaults")
    public boolean showInvisibleTeammates = true;
    @ConfigEntry.Category("ModTeam Defaults")
    public boolean friendlyFireEnabled = false;
    @ConfigEntry.Category("ModTeam Defaults")
    public Team.Visibility nameTagVisibility = Team.Visibility.ALWAYS;
    @ConfigEntry.Category("ModTeam Defaults")
    public ChatFormatting colour = ChatFormatting.BOLD;
    @ConfigEntry.Category("ModTeam Defaults")
    public Team.Visibility deathMessageVisibility = Team.Visibility.ALWAYS;
    @ConfigEntry.Category("ModTeam Defaults")
    @Comment("Note that 'push own team' and 'push other teams' are swapped.")
    public Team.CollisionRule collisionRule = Team.CollisionRule.PUSH_OWN_TEAM;
    @ConfigEntry.Category("ModTeam Defaults")
    @Comment("Sync advancements between team members")
    public boolean syncAdvancements = true;
    @ConfigEntry.Category("ModTeam Defaults")
    @Comment("Server-enforced maximum compass detection distance (in blocks). Client settings cannot exceed this value.")
    public int maxCompassDetectionDistance = 512;

    @ConfigEntry.Category("Visual")
    public boolean enableCompassHUD = true;
    @ConfigEntry.Category("Visual")
    public boolean enableStatusHUD = true;
    @ConfigEntry.Category("Visual")
    @Comment("How long teams toast notifications should last")
    public int toastDuration = 5;
    @ConfigEntry.Category("Visual")
    @Comment("Show other team members' hunger")
    public boolean showHunger = true;
    @ConfigEntry.Category("Visual")
    @Comment("Maximum detection distance for the compass HUD (in blocks)")
    public int compassDetectionDistance = 128;
    
    @ConfigEntry.Category("HUD Positioning")
    @Comment("X position of the status overlay (-1 for default)")
    public int statusOverlayX = -1;
    @ConfigEntry.Category("HUD Positioning")
    @Comment("Y position of the status overlay (-1 for default)")
    public int statusOverlayY = -1;
    @ConfigEntry.Category("HUD Positioning")
    @Comment("X position of the compass overlay (-1 for default)")
    public int compassOverlayX = -1;
    @ConfigEntry.Category("HUD Positioning")
    @Comment("Y position of the compass overlay (-1 for default)")
    public int compassOverlayY = -1;
    @ConfigEntry.Category("HUD Positioning")
    @Comment("Lock HUD elements to prevent dragging")
    public boolean hudLocked = true;
    @ConfigEntry.Category("HUD Positioning")
    @Comment("Scale of the status overlay (1.0 = default, 0.1 = 10%, 2.0 = 200%)")
    public float statusOverlayScale = 1.0f;
    @ConfigEntry.Category("HUD Positioning")
    @Comment("Scale of the compass overlay (1.0 = default, 0.1 = 10%, 2.0 = 200%)")
    public float compassOverlayScale = 1.0f;

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
    public boolean hudLocked() {
        return hudLocked;
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
    public void setHudLocked(boolean locked) {
        this.hudLocked = locked;
    }

    @Override
    public void setStatusOverlayScale(float scale) {
        this.statusOverlayScale = scale;
    }

    @Override
    public void setCompassOverlayScale(float scale) {
        this.compassOverlayScale = scale;
    }
}
