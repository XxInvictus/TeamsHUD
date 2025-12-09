package com.xxinvictus.teamshudplus.client.ui.menu;

import com.xxinvictus.teamshudplus.client.TeamsHUDPlusClient;
import com.xxinvictus.teamshudplus.client.ui.hud.HudDragManager;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Transparent overlay screen that allows dragging HUD elements with visible mouse cursor.
 * Provides an interface for repositioning status and compass overlays.
 */
public class HudDragScreen extends Screen {
    
    private boolean isDragging = false;
    
    /**
     * Creates a new HUD drag screen
     */
    public HudDragScreen() {
        super(Component.literal("HUD Drag Mode"));
    }
    
    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause the game
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Don't render any background - keep it transparent
        // The HUD overlays will render themselves through the normal HUD rendering
        
        // Show instruction text
        graphics.drawString(this.font, "Click and drag HUD elements to reposition. Press ESC or L to exit.", 
            10, 10, 0xFFFFFFFF, true);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            // Try to start dragging on an overlay
            // Pass true for alreadyScaled since Screen provides GUI-scaled coordinates
            boolean started = TeamsHUDPlusClient.onMouseClick(mouseX, mouseY, button, true);
            if (started) {
                isDragging = true;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDragging) {
            // Pass true for alreadyScaled since Screen provides GUI-scaled coordinates
            TeamsHUDPlusClient.onMouseDrag(mouseX, mouseY, button, dragX, dragY, true);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isDragging) {
            // Pass true for alreadyScaled since Screen provides GUI-scaled coordinates
            TeamsHUDPlusClient.onMouseRelease(mouseX, mouseY, button, true);
            isDragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC or L key exits drag mode
        if (keyCode == 256 || keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_L) { // ESC or L
            HudDragManager.toggleLock(); // Lock the HUD
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public void onClose() {
        // Return to game
        this.minecraft.setScreen(null);
    }
}
