package com.t2pellet.teams.platform;

import net.minecraft.ChatFormatting;
import net.minecraft.world.scores.Team;

public interface MultiloaderConfig {

    boolean showInvisibleTeammates();
    boolean friendlyFireEnabled();
    Team.Visibility nameTagVisibility();
    ChatFormatting colour();
    Team.Visibility deathMessageVisibility();
    Team.CollisionRule collisionRule();

    boolean enableCompassHUD();
    boolean enableStatusHUD();
    int toastDuration();
    boolean showHunger();
    boolean syncAdvancements();
    int compassDetectionDistance();
    int maxCompassDetectionDistance();
    
    // HUD positioning
    int statusOverlayX();
    int statusOverlayY();
    int compassOverlayX();
    int compassOverlayY();
    boolean hudLocked();
    float statusOverlayScale();
    float compassOverlayScale();
    
    void setStatusOverlayX(int x);
    void setStatusOverlayY(int y);
    void setCompassOverlayX(int x);
    void setCompassOverlayY(int y);
    void setHudLocked(boolean locked);
    void setStatusOverlayScale(float scale);
    void setCompassOverlayScale(float scale);
}
