package com.xxinvictus.teamshudplus.client.ui.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.xxinvictus.teamshudplus.TeamsHUDPlus;
import com.xxinvictus.teamshudplus.client.core.ClientTeam;
import com.xxinvictus.teamshudplus.core.ModComponents;
import com.xxinvictus.teamshudplus.network.server.C2STeamKickPacket;
import com.xxinvictus.teamshudplus.platform.Services;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * UI widget representing a teammate in the team member list.
 * Shows teammate name, status, and action buttons (kick, favorite).
 */
public class TeammateEntry extends AbstractWidget {

    /** Width of the entry widget */
    static final int WIDTH = 244;
    /** Height of the entry widget */
    static final int HEIGHT = 24;
    private static final ResourceLocation TEXTURE = TeamsHUDPlus.id("textures/gui/screen_background.png");

    private Button kickButton;
    private TexturedToggleWidget favButton;
    private final Minecraft client;
    private final ClientTeam.Teammate teammate;
    private final int x;
    private final int y;

    /**
     * Creates a teammate entry widget.
     * @param teammate The teammate data
     * @param x The X position
     * @param y The Y position
     * @param local Whether this is the local player
     */
    public TeammateEntry(ClientTeam.Teammate teammate, int x, int y, boolean local) {
        super(x,y,WIDTH,HEIGHT, ModComponents.literal(teammate.name));
        this.client = Minecraft.getInstance();
        this.teammate = teammate;
        this.x = x;
        this.y = y;
        if (!local) {
            this.favButton = new TexturedToggleWidget(x + WIDTH - 12, y + 8, 8, 8, 0, 190, TEXTURE, button -> {
                if (ClientTeam.INSTANCE.isFavourite(teammate)) {
                    ClientTeam.INSTANCE.removeFavourite(teammate);
                } else {
                    ClientTeam.INSTANCE.addFavourite(teammate);
                }
            }, () -> ClientTeam.INSTANCE.isFavourite(teammate));
        }
        if (ClientTeam.INSTANCE.hasPermissions()) {
            this.kickButton = createTexturedButton(x + WIDTH - 24, y + 8, 8, 8, 16, 190, TEXTURE, button -> {
                Services.PLATFORM.sendToServer(new C2STeamKickPacket(ClientTeam.INSTANCE.getName(), teammate.id));
                ClientTeam.INSTANCE.removePlayer(teammate.id);
            });
        }
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Background
        renderBackground(graphics);
        // Head
        float scale = 0.5F;
        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, scale);
        graphics.blit(RenderType::guiTextured, teammate.skin, (int) ((x + 4) / scale), (int) ((y + 4) / scale), 32, 32, 32, 32, 64, 64);
        graphics.pose().popPose();
        // Nameplate
        graphics.drawString(client.font, teammate.name, x + 24, y + 12 - (client.font.lineHeight / 2), ChatFormatting.BLACK.getColor(),false);
        // Buttons
        if (favButton != null) {
            favButton.render(graphics, mouseX, mouseY, delta);
        }
        if (kickButton != null) {
            kickButton.render(graphics, mouseX, mouseY, delta);
        }
    }

    private void renderBackground(GuiGraphics graphics) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(RenderType::guiTextured, TEXTURE, x, y, 0, 166, WIDTH, HEIGHT, 256, 256);
    }


    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.FOCUSED;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    private static Button createTexturedButton(int x, int y, int width, int height, int u, int v, ResourceLocation texture, Button.OnPress onPress) {
        return new Button(x, y, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION) {
            @Override
            protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
                graphics.blit(RenderType::guiTextured, texture, this.getX(), this.getY(), u, v, this.width, this.height, 256, 256);
            }
        };
    }

    /**
     * Gets the kick button widget.
     * @return The kick button
     */
    public Button getKickButton() {
        return kickButton;
    }

    /**
     * Gets the favorite toggle button widget.
     * @return The favorite button
     */
    public TexturedToggleWidget getFavButton() {
        return favButton;
    }
}
