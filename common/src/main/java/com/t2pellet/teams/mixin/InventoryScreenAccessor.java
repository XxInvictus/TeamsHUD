package com.t2pellet.teams.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Mixin accessor for AbstractContainerScreen to expose screen positioning.
 * Provides access to private positioning fields for overlay rendering.
 */
@Mixin(AbstractContainerScreen.class)
public interface InventoryScreenAccessor {

    /**
     * Gets the left position of the inventory screen
     * @return The X coordinate
     */
    @Accessor("leftPos")
    int getX();
    
    /**
     * Gets the top position of the inventory screen
     * @return The Y coordinate
     */
    @Accessor("topPos")
    int getY();
    
    /**
     * Gets the width of the inventory background
     * @return The width in pixels
     */
    @Accessor("imageWidth")
    int getBackgroundWidth();

}
