package com.xxinvictus.teamshudplus.client.ui.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import com.xxinvictus.teamshudplus.platform.MultiloaderConfig;
import com.xxinvictus.teamshudplus.platform.Services;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * Base class for team-related toast notifications.
 * Displays team messages with a configurable duration.
 */
public abstract class TeamToast implements Toast {

    private static final ResourceLocation TOAST_TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/toasts.png");

    /** The team name */
    public final String team;
    private boolean firstDraw = true;
    private long firstDrawTime;

    /**
     * Creates a team toast.
     * @param team The team name
     */
    public TeamToast(String team) {
        this.team = team;
    }

    /**
     * Gets the title text for this toast.
     * @return The title string
     */
    public abstract String title();

    /**
     * Gets the subtitle text for this toast.
     * @return The subtitle string
     */
    public abstract String subTitle();

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent manager, long startTime) {
        if (firstDraw) {
            firstDrawTime = startTime;
            firstDraw = false;
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(RenderType::guiTextured, TOAST_TEXTURE, 0, 0, 0, 64, this.width(), this.height(), 256, 256);
        graphics.drawString(manager.getMinecraft().font, title(), 22, 7, ChatFormatting.WHITE.getColor());
        graphics.drawString(manager.getMinecraft().font, subTitle(), 22, 18, 0xff000000,false);

        return startTime - firstDrawTime < Services.PLATFORM.getConfig().toastDuration() * 1000L && team != null ? Visibility.SHOW : Visibility.HIDE;    }
}
