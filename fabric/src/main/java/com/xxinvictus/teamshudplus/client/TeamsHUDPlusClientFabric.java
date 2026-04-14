package com.xxinvictus.teamshudplus.client;

import com.xxinvictus.teamshudplus.TeamsHUDPlus;
import com.xxinvictus.teamshudplus.client.TeamsHUDPlusClient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;

/**
 * Fabric client-side initialization and event handling.
 */
public class TeamsHUDPlusClientFabric implements ClientModInitializer {

    private static boolean wasMousePressed = false;

    @Override
    public void onInitializeClient() {
        // Register HUDs using the layered HUD system
        HudLayerRegistrationCallback.EVENT.register((layeredDrawer) -> {
            layeredDrawer.attachLayerAfter(IdentifiedLayer.CROSSHAIR, TeamsHUDPlus.id("status"), (context, tickCounter) -> {
                TeamsHUDPlusClient.status.render(context);
            });
            layeredDrawer.attachLayerAfter(IdentifiedLayer.CROSSHAIR, TeamsHUDPlus.id("compass"), (context, tickCounter) -> {
                TeamsHUDPlusClient.compass.render(context);
            });
        });

        // Handle keybinds and mouse dragging
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            TeamsHUDPlusClient.endClientTick();
            handleMouseDrag(client);
        });

        // Register events
        ClientLoginConnectionEvents.DISCONNECT.register((handler, client) -> TeamsHUDPlusClient.clientDisconnect());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> TeamsHUDPlusClient.clientDisconnect());

        ScreenEvents.AFTER_INIT.register(TeamsHUDPlusClient::afterScreenInit);

        TeamsHUDPlusClient.registerKeybinds();

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
            TeamsHUDPlusClient.onMouseClick(mouseX[0], mouseY[0], 0);
        } else if (isMousePressed && wasMousePressed) {
            // Mouse button held down (dragging)
            TeamsHUDPlusClient.onMouseDrag(mouseX[0], mouseY[0], 0, 0, 0);
        } else if (!isMousePressed && wasMousePressed) {
            // Mouse button just released
            TeamsHUDPlusClient.onMouseRelease(mouseX[0], mouseY[0], 0);
        }
        
        wasMousePressed = isMousePressed;
    }
}
