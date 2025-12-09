package com.xxinvictus.teamshudplus.client.ui.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.xxinvictus.teamshudplus.TeamsHUDPlus;
import com.xxinvictus.teamshudplus.client.ui.toast.ToastRequest;
import com.xxinvictus.teamshudplus.core.ModComponents;
import com.xxinvictus.teamshudplus.network.server.C2STeamRequestPacket;
import com.xxinvictus.teamshudplus.platform.Services;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * UI widget representing a team entry in the teams list.
 * Displays team name and a join/request button.
 */
public class TeamEntry extends AbstractWidget {

    /** Width of the entry widget */
    static final int WIDTH = 244;
    /** Height of the entry widget */
    static final int HEIGHT = 24;
    private static final ResourceLocation TEXTURE = TeamsHUDPlus.id("textures/gui/screen_background.png");

    /** Button to request joining the team */
    public final ImageButton joinButton;
    private Minecraft client;
    private String team;
    private int x;
    private int y;

    /**
     * Creates a team entry widget.
     * @param team The team name
     * @param x The X position
     * @param y The Y position
     */
    public TeamEntry(String team, int x, int y) {
        super(x,y,WIDTH,HEIGHT, ModComponents.literal(team));
        this.client = Minecraft.getInstance();
        this.team = team;
        this.x = x;
        this.y = y;
        this.joinButton = new ImageButton(x + WIDTH - 24, y + 8, 8, 8, 24, 190, TEXTURE, button -> {
            Services.PLATFORM.sendToServer(new C2STeamRequestPacket(team));
            client.getToasts().addToast(new ToastRequest(team));
            client.setScreen(null);
        });
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Background
        renderBackground(graphics);
        // Name
        graphics.drawString(client.font, team, x + 8, y + 12 - (int) (client.font.lineHeight / 2), ChatFormatting.BLACK.getColor());
        // Buttons
        joinButton.render(graphics, mouseX, mouseY, delta);
    }

    private void renderBackground(GuiGraphics graphics) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, x, y, 0, 166, WIDTH, HEIGHT);
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.FOCUSED;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

}
