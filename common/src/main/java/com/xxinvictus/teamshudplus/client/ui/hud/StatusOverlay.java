package com.xxinvictus.teamshudplus.client.ui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.xxinvictus.teamshudplus.TeamsHUDPlus;
import com.xxinvictus.teamshudplus.client.core.ClientTeam;
import com.xxinvictus.teamshudplus.core.ModComponents;
import com.xxinvictus.teamshudplus.platform.Services;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Renders teammate health and hunger status overlay.
 * Shows player heads with health bars and hunger levels.
 */
public class StatusOverlay {

    private static final ResourceLocation ICONS = TeamsHUDPlus.id("textures/gui/hudicons.png");
    private static final int OVERLAY_WIDTH = 100; // Increased from 80 to accommodate distance text
    private static final int OVERLAY_HEIGHT_PER_PLAYER = 46;

    /** Whether the status overlay is enabled */
    public boolean enabled = true;
    private final Minecraft client;
    private int offsetY = 0;
    private int baseX = 0;
    private int baseY = 0;
    private int totalHeight = 0;

    /**
     * Creates a new status overlay
     */
    public StatusOverlay() {
        this.client = Minecraft.getInstance();
    }
    
    /**
     * Gets the base X coordinate of the overlay
     * @return The X coordinate
     */
    public int getBaseX() {
        return baseX;
    }
    
    /**
     * Gets the base Y coordinate of the overlay
     * @return The Y coordinate
     */
    public int getBaseY() {
        return baseY;
    }
    
    /**
     * Gets the width of the status overlay
     * @return The width in pixels
     */
    public int getWidth() {
        return OVERLAY_WIDTH;
    }
    
    /**
     * Gets the total height of the status overlay
     * @return The height in pixels
     */
    public int getHeight() {
        // Return unscaled height - scaling will be applied in hit detection
        return totalHeight;
    }
    
    /**
     * Gets the scale factor for the status overlay from config
     * @return The scale factor
     */
    public float getScale() {
        return Services.PLATFORM.getConfig().statusOverlayScale();
    }

    /**
     * Renders the status overlay to the screen
     * @param graphics The graphics context for rendering
     */
    public void render(GuiGraphics graphics) {
        offsetY = 0;
        
        // Get position and scale from config
        int configX = Services.PLATFORM.getConfig().statusOverlayX();
        int configY = Services.PLATFORM.getConfig().statusOverlayY();
        float scale = Services.PLATFORM.getConfig().statusOverlayScale();
        
        // Use default position if not set
        if (configX == -1) {
            baseX = (int) Math.round(client.getWindow().getGuiScaledWidth() * 0.003);
        } else {
            baseX = configX;
        }
        
        if (configY == -1) {
            baseY = client.getWindow().getGuiScaledHeight() / 4 - 5;
        } else {
            baseY = configY;
        }
        
        // Apply scale transformation
        graphics.pose().pushPose();
        graphics.pose().translate(baseX, baseY, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.pose().translate(-baseX, -baseY, 0);
        
        List<ClientTeam.Teammate> teammates = ClientTeam.INSTANCE.getTeammates();
        int shown = 0;
        for (int i = 0; i < teammates.size() && shown < 4; ++i) {
            if (client.player.getUUID().equals(teammates.get(i).id)) {
                continue;
            }
            renderStatus(graphics, teammates.get(i));
            ++shown;
        }
        
        // Store unscaled height, will be scaled for hit detection
        // Use minimum height even when empty so overlay can be dragged
        totalHeight = Math.max(offsetY, shown > 0 ? offsetY : OVERLAY_HEIGHT_PER_PLAYER);
        
        graphics.pose().popPose();
        
        // Render drag indicator if unlocked (outside scale transformation)
        if (!HudDragManager.isLocked()) {
            renderDragIndicator(graphics, scale);
        }
    }
    
    private void renderDragIndicator(GuiGraphics graphics, float scale) {
        // Draw a subtle border to indicate draggability (using scaled dimensions)
        int color = HudDragManager.getCurrentTarget() == HudDragManager.DragTarget.STATUS 
            ? 0x8800FF00 : 0x44FFFFFF;
        int scaledWidth = (int) (OVERLAY_WIDTH * scale);
        int scaledHeight = (int) (totalHeight * scale);
        graphics.fill(baseX - 1, baseY - 1, baseX + scaledWidth + 1, baseY + scaledHeight + 1, color);
    }

    private void renderStatus(GuiGraphics graphics, ClientTeam.Teammate teammate) {
        if (!Services.PLATFORM.getConfig().enableStatusHUD() || !enabled) return;

        // Dont render dead players
        if (teammate.getHealth() <= 0) return;
        
        int posX = baseX;
        int posY = baseY + offsetY;

        // Health
        String health = String.valueOf(Math.round(teammate.getHealth()));
        graphics.blit(ICONS,posX + 20, posY, 0, 0, 9, 9);
        graphics.drawString(client.font, ModComponents.literal(health), posX + 32, posY, ChatFormatting.WHITE.getColor());

        // Hunger
        if (Services.PLATFORM.getConfig().showHunger()) {
            String hunger = String.valueOf(teammate.getHunger());
            graphics.blit(ICONS, posX + 46, posY, 9, 0, 9, 9);
            graphics.drawString(client.font, ModComponents.literal(hunger), posX + 58, posY, ChatFormatting.WHITE.getColor());
        }

        // Draw skin (using relative positioning)
        graphics.pose().pushPose();
        graphics.pose().scale(0.5F, 0.5F, 0.5F);
        graphics.blit(teammate.skin, 2 * (posX + 4), 2 * posY + 8, 32, 32, 32, 32);
        graphics.pose().popPose();

        // Draw name with optional distance
        String nameText = teammate.name;
        if (Services.PLATFORM.getConfig().showTeammateDistance() && teammate.getDistance() >= 0) {
            boolean showDistance = true;
            
            // If config requires compass range check, verify teammate is within range
            if (Services.PLATFORM.getConfig().distanceOnlyWithinCompassRange()) {
                int compassDetectionDistance = Services.PLATFORM.getConfig().compassDetectionDistance();
                showDistance = teammate.getDistance() <= compassDetectionDistance;
            }
            
            if (showDistance) {
                int distance = (int) Math.round(teammate.getDistance());
                nameText = teammate.name + " - " + distance + "m";
            }
        }
        graphics.drawString(client.font, Component.literal(nameText), posX + 20, posY - 15, ChatFormatting.WHITE.getColor());

        // Update count & offset
        offsetY += 46;
    }

}
