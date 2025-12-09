package com.xxinvictus.teamshudplus.client.ui.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xxinvictus.teamshudplus.client.core.ClientTeam;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Base class for all team-related UI screens.
 * Provides common rendering and positioning logic.
 */
public abstract class TeamsScreen extends Screen {

    /** The parent screen to return to when closing */
    public final Screen parent;
    /** The X position of the screen */
    protected int x;
    /** The Y position of the screen */
    protected int y;
    /** Whether the player is currently in a team */
    protected boolean inTeam;

    /**
     * Creates a teams screen.
     * @param parent The parent screen
     * @param title The screen title
     */
    public TeamsScreen(Screen parent, Component title) {
        super(title);
        this.parent = parent;
        inTeam = ClientTeam.INSTANCE.isInTeam();
    }

    @Override
    protected void init() {
        x = (width - getWidth()) / 2;
        y = (height - getHeight()) / 2;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.pose().pushPose();
        graphics.blit(getBackgroundTexture(), x, y, 0, 0, getWidth(), getHeight());
        graphics.pose().popPose();
        super.render(graphics, mouseX, mouseY, delta);
    }

    /**
     * Gets the width of the screen background.
     * @return The screen width in pixels
     */
    protected abstract int getWidth();

    /**
     * Gets the height of the screen background.
     * @return The screen height in pixels
     */
    protected abstract int getHeight();

    /**
     * Gets the background texture for this screen.
     * @return The texture resource location
     */
    protected abstract ResourceLocation getBackgroundTexture();
    
}
