package com.t2pellet.teams;

import net.minecraft.client.gui.components.Button;

/**
 * Duck interface for Screen to add button functionality.
 * Implemented via mixin to allow programmatic button addition.
 */
public interface ScreenDuck {

    /**
     * Adds a button to the screen
     * @param b The button to add
     * @return The added button
     */
    Button $addButton(Button b);

}
