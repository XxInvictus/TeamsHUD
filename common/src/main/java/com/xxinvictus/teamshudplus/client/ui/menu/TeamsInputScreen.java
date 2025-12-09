package com.xxinvictus.teamshudplus.client.ui.menu;

import com.xxinvictus.teamshudplus.TeamsHUDPlus;
import com.xxinvictus.teamshudplus.core.ModComponents;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Base class for team screens with text input fields.
 * Provides an input field, submit button, and back button.
 */
public abstract class TeamsInputScreen extends TeamsScreen {

    private static final ResourceLocation TEXTURE = TeamsHUDPlus.id("textures/gui/smaller_background.png");
    private static final int WIDTH = 120;
    private static final int HEIGHT = 110;

    /** The text input field */
    protected EditBox inputField;
    /** The submit button */
    protected Button submitButton;
    private String prevInputText = "";

    /**
     * Creates an input screen.
     * @param parent The parent screen
     * @param title The screen title
     */
    public TeamsInputScreen(Screen parent, Component title) {
        super(parent, title);
    }

    @Override
    protected void init() {
        super.init();
        inputField = addRenderableWidget(new EditBox(minecraft.font, x + (getWidth() - 100) / 2, y + 10, 100, 20, ModComponents.DEFAULT_TEXT));
        submitButton = addRenderableWidget(Button.builder(getSubmitText(),this::onSubmit).bounds(x + (getWidth() - 100) / 2, y + HEIGHT - 55, 100, 20).build());
        submitButton.active = submitCondition();
        addRenderableWidget(Button.builder(ModComponents.GO_BACK_TEXT, button -> minecraft.setScreen(parent)).bounds(x + (getWidth() - 100) / 2, y + HEIGHT - 30, 100, 20).build());
    }

    @Override
    public void tick() {
        if (!prevInputText.equals(inputField.getValue())) {
            submitButton.active = submitCondition();
        }
    }

    @Override
    protected int getWidth() {
        return WIDTH;
    }

    @Override
    protected int getHeight() {
        return HEIGHT;
    }

    @Override
    protected ResourceLocation getBackgroundTexture() {
        return TEXTURE;
    }

    /**
     * Gets the text for the submit button.
     * @return The submit button text
     */
    protected abstract Component getSubmitText();

    /**
     * Called when the submit button is pressed.
     * @param widget The button that was pressed
     */
    protected abstract void onSubmit(Button widget);

    /**
     * Determines whether the submit button should be enabled.
     * @return true if the submit button should be active
     */
    protected abstract boolean submitCondition();

}
