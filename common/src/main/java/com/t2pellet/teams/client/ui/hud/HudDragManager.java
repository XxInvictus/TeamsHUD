package com.t2pellet.teams.client.ui.hud;

import com.t2pellet.teams.platform.Services;
import net.minecraft.client.Minecraft;

public class HudDragManager {
    
    public enum DragTarget {
        NONE,
        STATUS,
        COMPASS
    }
    
    private static DragTarget currentlyDragging = DragTarget.NONE;
    private static int dragStartX = 0;
    private static int dragStartY = 0;
    private static int elementStartX = 0;
    private static int elementStartY = 0;
    private static boolean locked = true; // Runtime lock state, defaults to locked
    
    public static boolean isLocked() {
        return locked;
    }
    
    public static void toggleLock() {
        locked = !locked;
    }
    
    public static boolean isDragging() {
        return currentlyDragging != DragTarget.NONE;
    }
    
    public static DragTarget getCurrentTarget() {
        return currentlyDragging;
    }
    
    public static boolean startDrag(DragTarget target, int mouseX, int mouseY, int elementX, int elementY) {
        if (isLocked() || isDragging()) {
            return false;
        }
        
        currentlyDragging = target;
        dragStartX = mouseX;
        dragStartY = mouseY;
        elementStartX = elementX;
        elementStartY = elementY;
        return true;
    }
    
    public static void updateDrag(int mouseX, int mouseY) {
        if (!isDragging()) {
            return;
        }
        
        int deltaX = mouseX - dragStartX;
        int deltaY = mouseY - dragStartY;
        int newX = elementStartX + deltaX;
        int newY = elementStartY + deltaY;
        
        // Clamp to screen bounds
        Minecraft client = Minecraft.getInstance();
        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();
        
        newX = Math.max(0, Math.min(newX, screenWidth - 100));
        newY = Math.max(0, Math.min(newY, screenHeight - 100));
        
        switch (currentlyDragging) {
            case STATUS -> {
                Services.PLATFORM.getConfig().setStatusOverlayX(newX);
                Services.PLATFORM.getConfig().setStatusOverlayY(newY);
            }
            case COMPASS -> {
                Services.PLATFORM.getConfig().setCompassOverlayX(newX);
                Services.PLATFORM.getConfig().setCompassOverlayY(newY);
            }
            case NONE -> {}
        }
    }
    
    public static void endDrag() {
        if (isDragging()) {
            currentlyDragging = DragTarget.NONE;
        }
    }
    
    public static boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height, float scale) {
        int scaledWidth = (int) (width * scale);
        int scaledHeight = (int) (height * scale);
        return mouseX >= x && mouseX <= x + scaledWidth && mouseY >= y && mouseY <= y + scaledHeight;
    }
    
    public static void resetPosition(DragTarget target) {
        switch (target) {
            case STATUS -> {
                Services.PLATFORM.getConfig().setStatusOverlayX(-1);
                Services.PLATFORM.getConfig().setStatusOverlayY(-1);
            }
            case COMPASS -> {
                Services.PLATFORM.getConfig().setCompassOverlayX(-1);
                Services.PLATFORM.getConfig().setCompassOverlayY(-1);
            }
            case NONE -> {}
        }
    }
}
