package com.xxinvictus.teamshudplus.client;

import com.xxinvictus.teamshudplus.client.TeamsHUDPlusClient;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Forge client-side initialization and event handling.
 */
public class TeamsHUDPlusClientForge {

    private static boolean wasMousePressed = false;

    /** Forge overlay for compass HUD */
    public static final IGuiOverlay compass = (gui, graphics, partialTick, width, height) -> TeamsHUDPlusClient.compass.render(graphics);
    /** Forge overlay for status HUD */
    public static final IGuiOverlay status = (gui, graphics, partialTick, width, height) -> TeamsHUDPlusClient.status.render(graphics);

    /**
     * Initializes Forge client-side event listeners.
     * @param bus The mod event bus
     */
    public static void init(IEventBus bus) {
        bus.addListener(TeamsHUDPlusClientForge::setup);
        bus.addListener(TeamsHUDPlusClientForge::registerOverlays);
        MinecraftForge.EVENT_BUS.addListener(TeamsHUDPlusClientForge::clientTick);
        MinecraftForge.EVENT_BUS.addListener(TeamsHUDPlusClientForge::clientDisconnect);
        MinecraftForge.EVENT_BUS.addListener(TeamsHUDPlusClientForge::addButton);
    }

    static void clientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        TeamsHUDPlusClient.clientDisconnect();
    }
    
    static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            TeamsHUDPlusClient.endClientTick();
            handleMouseDrag();
        }
    }
    
    private static void handleMouseDrag() {
        Minecraft client = Minecraft.getInstance();
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

    static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("compass",compass);
        event.registerAboveAll("status",status);
    }

    static void addButton(ScreenEvent.Init.Post event) {
        TeamsHUDPlusClient.afterScreenInit(Minecraft.getInstance(),event.getScreen(),Minecraft.getInstance().getWindow().getGuiScaledWidth(),Minecraft.getInstance().getWindow().getGuiScaledHeight());
    }

    static void setup(FMLClientSetupEvent event) {
        TeamsHUDPlusClient.registerKeybinds();
    }
}
