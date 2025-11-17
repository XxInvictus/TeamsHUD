package com.t2pellet.teams.client.ui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.t2pellet.teams.TeamsHUD;
import com.t2pellet.teams.client.core.ClientTeam;
import com.t2pellet.teams.core.ModComponents;
import com.t2pellet.teams.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class StatusOverlay {

    private static final ResourceLocation ICONS = TeamsHUD.id("textures/gui/hudicons.png");
    private static final int OVERLAY_WIDTH = 80;
    private static final int OVERLAY_HEIGHT_PER_PLAYER = 46;

    public boolean enabled = true;
    private final Minecraft client;
    private int offsetY = 0;
    private int baseX = 0;
    private int baseY = 0;
    private int totalHeight = 0;

    public StatusOverlay() {
        this.client = Minecraft.getInstance();
    }
    
    public int getBaseX() {
        return baseX;
    }
    
    public int getBaseY() {
        return baseY;
    }
    
    public int getWidth() {
        return OVERLAY_WIDTH;
    }
    
    public int getHeight() {
        // Return unscaled height - scaling will be applied in hit detection
        return totalHeight;
    }
    
    public float getScale() {
        return Services.PLATFORM.getConfig().statusOverlayScale();
    }

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
        totalHeight = offsetY;
        
        graphics.pose().popPose();
        
        // Render drag indicator if unlocked (outside scale transformation)
        if (!HudDragManager.isLocked() && totalHeight > 0) {
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

        // Draw name
        graphics.drawString(client.font, Component.literal(teammate.name), posX + 20, posY - 15, ChatFormatting.WHITE.getColor());

        // Update count & offset
        offsetY += 46;
    }

}
