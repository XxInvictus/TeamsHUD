package com.t2pellet.teams.client.ui.hud;

import com.t2pellet.teams.platform.Services;
import net.minecraft.client.Minecraft;

/**
 * Manages dragging and repositioning of HUD overlay elements.
 * Provides locking mechanism to prevent accidental moves during gameplay.
 */
public class HudDragManager {
    
    /**
     * Identifies which HUD element can be dragged
     */
    public enum DragTarget {
        /** No element being dragged */
        NONE,
        /** Status overlay (health/hunger) */
        STATUS,
        /** Compass overlay */
        COMPASS
    }
    
    private static DragTarget currentlyDragging = DragTarget.NONE;
    private static int dragStartX = 0;
    private static int dragStartY = 0;
    private static int elementStartX = 0;
    private static int elementStartY = 0;
    private static boolean locked = true; // Runtime lock state, defaults to locked
    
    /**
     * Checks if HUD elements are locked from dragging
     * @return true if locked
     */
    public static boolean isLocked() {
        return locked;
    }
    
    /**
     * Toggles the lock state for HUD dragging
     */
    public static void toggleLock() {
        locked = !locked;
    }
    
    /**
     * Checks if a drag operation is currently in progress
     * @return true if dragging
     */
    public static boolean isDragging() {
        return currentlyDragging != DragTarget.NONE;
    }
    
    /**
     * Gets the current drag target
     * @return The drag target, or NONE if not dragging
     */
    public static DragTarget getCurrentTarget() {
        return currentlyDragging;
    }
    
    /**
     * Starts a drag operation for a HUD element
     * @param target The element to drag
     * @param mouseX Current mouse X position
     * @param mouseY Current mouse Y position
     * @param elementX Element's current X position
     * @param elementY Element's current Y position
     * @return true if drag started successfully
     */
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
    
    /**
     * Updates the position of the dragged element based on mouse movement
     * @param mouseX Current mouse X position
     * @param mouseY Current mouse Y position
     */
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
    
    /**
     * Ends the current drag operation
     */
    public static void endDrag() {
        if (isDragging()) {
            currentlyDragging = DragTarget.NONE;
        }
    }
    
    /**
     * Checks if the mouse is over a scaled rectangular area
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param x Area X position
     * @param y Area Y position
     * @param width Area width (before scaling)
     * @param height Area height (before scaling)
     * @param scale Scale factor
     * @return true if mouse is within the scaled area
     */
    public static boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height, float scale) {
        int scaledWidth = (int) (width * scale);
        int scaledHeight = (int) (height * scale);
        return mouseX >= x && mouseX <= x + scaledWidth && mouseY >= y && mouseY <= y + scaledHeight;
    }
    
    /**
     * Resets a HUD element to its default position
     * @param target The element to reset
     */
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
