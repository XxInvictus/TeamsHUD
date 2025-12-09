package com.t2pellet.teams.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;

public class TeamsHUDClientFabric implements ClientModInitializer {

    private static boolean wasMousePressed = false;

    @Override
    public void onInitializeClient() {
        // Register HUDs
        HudRenderCallback.EVENT.register((graphics, tickDelta) -> {
            TeamsHUDClient.status.render(graphics);
            TeamsHUDClient.compass.render(graphics);
        });

        // Handle keybinds and mouse dragging
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            TeamsHUDClient.endClientTick();
            handleMouseDrag(client);
        });

        // Register events
        ClientLoginConnectionEvents.DISCONNECT.register((handler, client) -> TeamsHUDClient.clientDisconnect());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> TeamsHUDClient.clientDisconnect());

        ScreenEvents.AFTER_INIT.register(TeamsHUDClient::afterScreenInit);

        TeamsHUDClient.registerKeybinds();

    }
    
    private static void handleMouseDrag(Minecraft client) {
        if (client.screen != null) {
            return; // Don't handle dragging when a screen is open
        }
        
        boolean isMousePressed = org.lwjgl.glfw.GLFW.glfwGetMouseButton(client.getWindow().getWindow(), 0) == 1;
        double[] mouseX = new double[1];
        double[] mouseY = new double[1];
        org.lwjgl.glfw.GLFW.glfwGetCursorPos(client.getWindow().getWindow(), mouseX, mouseY);
        
        if (isMousePressed && !wasMousePressed) {
            // Mouse button just pressed
            TeamsHUDClient.onMouseClick(mouseX[0], mouseY[0], 0);
        } else if (isMousePressed && wasMousePressed) {
            // Mouse button held down (dragging)
            TeamsHUDClient.onMouseDrag(mouseX[0], mouseY[0], 0, 0, 0);
        } else if (!isMousePressed && wasMousePressed) {
            // Mouse button just released
            TeamsHUDClient.onMouseRelease(mouseX[0], mouseY[0], 0);
        }
        
        wasMousePressed = isMousePressed;
    }
}
