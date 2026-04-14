package com.xxinvictus.teamshudplus.client.ui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.xxinvictus.teamshudplus.client.core.ClientTeam;
import com.xxinvictus.teamshudplus.platform.Services;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * Renders a compass-style HUD showing teammate positions and distances.
 * Displays teammate heads around a compass overlay indicating their relative direction.
 */
public class CompassOverlay {

    private static final int HUD_WIDTH = 182;
    private static final int HUD_HEIGHT = 5;

    private static final int MIN_DIST = 12;
    private static final float MIN_SCALE = 0.2f;
    private static final float MAX_SCALE = 0.4f;
    private static final float MIN_ALPHA = 0.4f;

    static final ResourceLocation GUI_ICONS_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/icons.png");

    /** Whether the compass overlay is enabled */
    public boolean enabled = true;
    private final Minecraft client;
    private boolean isShowing = false;
    private int baseX = 0;
    private int baseY = 0;

    /**
     * Creates a new compass overlay
     */
    public CompassOverlay() {
        this.client = Minecraft.getInstance();
    }

    /**
     * Checks if the compass is currently being shown
     * @return true if the compass is visible
     */
    public boolean isShowing() {
        return isShowing;
    }
    
    /**
     * Gets the base X coordinate of the compass
     * @return The X coordinate
     */
    public int getBaseX() {
        return baseX;
    }
    
    /**
     * Gets the base Y coordinate of the compass
     * @return The Y coordinate
     */
    public int getBaseY() {
        return baseY;
    }
    
    /**
     * Gets the width of the compass overlay
     * @return The width in pixels
     */
    public int getWidth() {
        return HUD_WIDTH;
    }
    
    /**
     * Gets the height of the compass overlay including space for player heads
     * @return The height in pixels
     */
    public int getHeight() {
        // Return unscaled height - scaling will be applied in hit detection
        return HUD_HEIGHT + 50; // Include space for heads
    }
    
    /**
     * Gets the scale factor for the compass overlay from config
     * @return The scale factor
     */
    public float getScale() {
        return Services.PLATFORM.getConfig().compassOverlayScale();
    }

    /**
     * Renders the compass overlay to the screen
     * @param graphics The graphics context for rendering
     */
    public void render(GuiGraphics graphics) {
        if (!Services.PLATFORM.getConfig().enableCompassHUD() || !enabled) {
            isShowing = false;
            return;
        }

        // Get position and scale from config
        int configX = Services.PLATFORM.getConfig().compassOverlayX();
        int configY = Services.PLATFORM.getConfig().compassOverlayY();
        float scale = Services.PLATFORM.getConfig().compassOverlayScale();
        
        // Use default position if not set (centered at top)
        if (configX == -1) {
            baseX = (client.getWindow().getGuiScaledWidth() - HUD_WIDTH) / 2;
        } else {
            baseX = configX;
        }
        
        if (configY == -1) {
            baseY = 5 + HUD_HEIGHT / 2;
        } else {
            baseY = configY;
        }

        // Apply scale transformation
        graphics.pose().pushPose();
        graphics.pose().translate(baseX, baseY, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.pose().translate(-baseX, -baseY, 0);

        // Render heads
        boolean renderedAnyHead = false;
        float minScale = 1.0F;
        for (var teammate : ClientTeam.INSTANCE.getTeammates()) {
            if (client.player.getUUID().equals(teammate.id)) continue;
            Player player = client.level.getPlayerByUUID(teammate.id);
            if (player != null) {
                double rotationHead = caculateRotationHead();
                float scaleFactor = calculateScaleFactor(player);
                if (scaleFactor < minScale) minScale = scaleFactor;
                double renderFactor = calculateRenderFactor(player, rotationHead);
                renderHUDHead(graphics, teammate.skin, scaleFactor, renderFactor);
                renderedAnyHead = true;
            }
        }

        // Render bar
        if (ClientTeam.INSTANCE.isInTeam() && !ClientTeam.INSTANCE.isTeamEmpty() && renderedAnyHead) {
            float alpha = (1 - minScale) * (1 - MIN_ALPHA) + MIN_ALPHA;
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
            graphics.blit(RenderType::guiTextured, GUI_ICONS_LOCATION, baseX, baseY, 0, 74, HUD_WIDTH, HUD_HEIGHT, 256, 256);
            
            RenderSystem.disableBlend();
            isShowing = true;
        } else {
            isShowing = false;
        }
        
        graphics.pose().popPose();
        
        // Render drag indicator if unlocked (outside scale transformation)
        if (!HudDragManager.isLocked() && isShowing) {
            renderDragIndicator(graphics, scale);
        }
    }
    
    private void renderDragIndicator(GuiGraphics graphics, float scale) {
        // Draw a subtle border to indicate draggability (using scaled dimensions)
        int color = HudDragManager.getCurrentTarget() == HudDragManager.DragTarget.COMPASS 
            ? 0x8800FF00 : 0x44FFFFFF;
        int scaledWidth = (int) (HUD_WIDTH * scale);
        int scaledHeight = (int) ((HUD_HEIGHT + 50) * scale);
        graphics.fill(baseX - 1, baseY - 1, baseX + scaledWidth + 1, baseY + scaledHeight + 1, color);
    }

    private double caculateRotationHead() {
        double rotationHead = client.player.getYHeadRot() % 360;
        if (rotationHead > 180) {
            rotationHead = rotationHead - 360;
        } else if (rotationHead < -180) {
            rotationHead = 360 + rotationHead;
        }
        return rotationHead;
    }

    private float calculateScaleFactor(Player player) {
        double diffPosX = player.position().x - client.player.position().x;
        double diffPosZ = player.position().z - client.player.position().z;
        double magnitude =  Math.sqrt(diffPosX * diffPosX + diffPosZ * diffPosZ);
        int clientMaxDist = Services.PLATFORM.getConfig().compassDetectionDistance();
        int serverMaxDist = Services.PLATFORM.getConfig().maxCompassDetectionDistance();
        int maxDist = Math.min(clientMaxDist, serverMaxDist);

        if (magnitude >= maxDist) {
            return 1;
        } else if (magnitude <= MIN_DIST) {
            return 0;
        } else {
            return (float) ((magnitude - MIN_DIST) / (maxDist - MIN_DIST));
        }
    }

    private double calculateRenderFactor(Player player, double rotationHead) {
        double diffPosX = player.position().x - client.player.position().x;
        double diffPosZ = player.position().z - client.player.position().z;
        double magnitude = Math.sqrt(diffPosX * diffPosX + diffPosZ * diffPosZ);
        diffPosX /= magnitude;
        diffPosZ /= magnitude;
        double angle = Math.atan(diffPosZ / diffPosX) * 180 / Math.PI + 90;
        if (diffPosX >= 0) {
            angle -= 180;
        }
        double renderFactor = (angle - rotationHead) / 180;
        if (renderFactor > 1) {
            renderFactor = renderFactor - 2;
        }
        if (renderFactor < -1) {
            renderFactor = 2 + renderFactor;
        }
        return renderFactor;
    }

    private void renderHUDHead(GuiGraphics graphics, ResourceLocation skin, float scaleFactor, double renderFactor) {
        int x = (int) (baseX + HUD_WIDTH / 2 - HUD_WIDTH / 4 + renderFactor * HUD_WIDTH / 2 + 41);
        int y = baseY + HUD_HEIGHT + 4;
        float sizeFactor = scaleFactor * (MAX_SCALE - MIN_SCALE) + MIN_SCALE;
        float alphaFactor = (1 - scaleFactor) * (1 - MIN_ALPHA) + MIN_ALPHA;
        graphics.pose().pushPose();
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alphaFactor);
        graphics.pose().scale(sizeFactor, sizeFactor, sizeFactor);
        if (1 - Math.abs(renderFactor) < Math.min(alphaFactor, 0.6f)) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (float) (1 - Math.abs(renderFactor)));
            graphics.blit(RenderType::guiTextured, skin, Math.round(x / sizeFactor), Math.round(y / sizeFactor), 32, 32, 32, 32, 64, 64);
        } else {
            graphics.blit(RenderType::guiTextured, skin, Math.round(x / sizeFactor), Math.round(y / sizeFactor), 32, 32, 32, 32, 64, 64);
        }
        RenderSystem.disableBlend();
        graphics.pose().popPose();
    }



}
