package com.xxinvictus.teamshudplus.client.ui.menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * A button that toggles between two texture states based on a boolean supplier.
 */
public class TexturedToggleWidget extends Button {

    private final int xTexStart;
    private final int yTexStart;
    private final int yDiffTex;
    private final ResourceLocation resourceLocation;
    private final int textureWidth;
    private final int textureHeight;
    private final Supplier<Boolean> booleanSupplier;

    /**
     * Creates a textured toggle widget.
     * @param x X position
     * @param y Y position
     * @param width Width
     * @param height Height
     * @param u Texture U coordinate
     * @param v Texture V coordinate
     * @param texture Texture resource
     * @param onPress Press handler
     * @param booleanSupplier Supplier for toggle state
     */
    public TexturedToggleWidget(int x, int y, int width, int height, int u, int v, ResourceLocation texture, Button.OnPress onPress, Supplier<Boolean> booleanSupplier) {
        this(x, y, width, height, u, v, height, texture, 256, 256, onPress, booleanSupplier);
    }

    /**
     * Creates a textured toggle widget with texture difference.
     * @param x X position
     * @param y Y position
     * @param width Width
     * @param height Height
     * @param xTexStart Texture X start
     * @param yTexStart Texture Y start
     * @param yDiffTex Texture Y difference
     * @param texture Texture resource
     * @param onPress Press handler
     * @param supplier Toggle state supplier
     */
    public TexturedToggleWidget(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation texture, Button.OnPress onPress, Supplier<Boolean> supplier) {
        this(x, y, width, height, xTexStart, yTexStart, yDiffTex, texture, 256, 256, onPress, supplier);
    }

    /**
     * Creates a textured toggle widget with custom texture dimensions.
     * @param x X position
     * @param y Y position
     * @param width Width
     * @param height Height
     * @param xTexStart Texture X start
     * @param yTexStart Texture Y start
     * @param yDiffTex Texture Y difference
     * @param texture Texture resource
     * @param textureWidth Texture width
     * @param textureHeight Texture height
     * @param onPress Press handler
     * @param supplier Toggle state supplier
     */
    public TexturedToggleWidget(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation texture, int textureWidth, int textureHeight, Button.OnPress onPress, Supplier<Boolean> supplier) {
        super(x, y, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.yDiffTex = yDiffTex;
        this.resourceLocation = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.booleanSupplier = supplier;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int u = xTexStart;
        if (booleanSupplier.get()) {
            u += width;
        }
        int v = yTexStart;
        if (this.isHoveredOrFocused()) {
            v += yDiffTex;
        }
        graphics.blit(RenderType::guiTextured, this.resourceLocation, this.getX(), this.getY(), u, v, this.width, this.height, this.textureWidth, this.textureHeight);
    }
}
