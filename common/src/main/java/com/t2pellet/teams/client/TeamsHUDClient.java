package com.t2pellet.teams.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.t2pellet.teams.ScreenDuck;
import com.t2pellet.teams.TeamsHUD;
import com.t2pellet.teams.client.core.ClientTeam;
import com.t2pellet.teams.client.core.ClientTeamDB;
import com.t2pellet.teams.client.ui.hud.CompassOverlay;
import com.t2pellet.teams.client.ui.hud.StatusOverlay;
import com.t2pellet.teams.client.ui.menu.TeamsLonelyScreen;
import com.t2pellet.teams.client.ui.menu.TeamsMainScreen;
import com.t2pellet.teams.client.ui.toast.ToastInvited;
import com.t2pellet.teams.client.ui.toast.ToastInviteSent;
import com.t2pellet.teams.client.ui.toast.ToastJoin;
import com.t2pellet.teams.client.ui.toast.ToastLeave;
import com.t2pellet.teams.client.ui.toast.ToastRequested;
import com.t2pellet.teams.mixin.InventoryScreenAccessor;
import com.t2pellet.teams.network.client.S2CTeamPlayerDataPacket;
import com.t2pellet.teams.network.client.S2CTeamUpdatePacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;

import java.util.UUID;

/**
 * Client-side initialization and event handling for TeamsHUD.
 * Manages HUD overlays, key bindings, screen buttons, and client-side packet handling.
 */
public class TeamsHUDClient {

    /** Status overlay showing teammate health and hunger */
    public static final StatusOverlay status = new StatusOverlay();
    /** Compass overlay showing teammate positions */
    public static final CompassOverlay compass = new CompassOverlay();

    /**
     * Registers a key mapping with the Minecraft client.
     * @param keyMapping The key mapping to register
     */
    public static void registerKeybinding(KeyMapping keyMapping) {
        Minecraft.getInstance().options.keyMappings = ArrayUtils.add(Minecraft.getInstance().options.keyMappings, keyMapping);
    }

    /** Texture for the teams button in the inventory screen */
    public static final ResourceLocation TEAMS_BUTTON_TEXTURE = TeamsHUD.id("textures/gui/buttonsmall.png");

    /**
     * Registers all mod key bindings.
     */
    public static void registerKeybinds() {
        // Register keybinds
        for (TeamsKeys.TeamsKey key : TeamsKeys.KEYS) {
            key.register();
        }
    }

    /**
     * Clears client-side team data when disconnecting from a server.
     */
    public static void clientDisconnect() {
        ClientTeam.INSTANCE.reset();
        ClientTeamDB.INSTANCE.clear();
    }

    /**
     * Adds the teams button to the inventory screen after it initializes.
     * @param minecraft The Minecraft client instance
     * @param screen The screen being initialized
     * @param scaledWidth The scaled screen width
     * @param scaledHeight The scaled screen height
     */
    public static void afterScreenInit(Minecraft minecraft, Screen screen, int scaledWidth, int scaledHeight){
        if (screen instanceof InventoryScreen inventoryScreen && minecraft.gameMode != null && !minecraft.gameMode.hasInfiniteItems()) {
            InventoryScreenAccessor screenAccessor = ((InventoryScreenAccessor) screen);
            ((ScreenDuck)inventoryScreen).$addButton(new ImageButton(screenAccessor.getX() + screenAccessor.getBackgroundWidth() - 19, screenAccessor.getY() + 4, 15, 14, 0, 0, 13, TEAMS_BUTTON_TEXTURE, (button) -> {
                if (ClientTeam.INSTANCE.isInTeam()) {
                    minecraft.setScreen(new TeamsMainScreen(minecraft.screen));

                } else {
                    minecraft.setScreen(new TeamsLonelyScreen(minecraft.screen));
                }
            }){
                @Override
                protected boolean clicked(double pMouseX, double pMouseY) {
                    return this.active && this.visible && pMouseX >= (double)this.getX() && pMouseY >= (double)this.getY() && pMouseX < (double)(this.getX() + this.width) && pMouseY < (double)(this.getY() + this.height);
                }
            });
        }
    }

    /**
     * Handles end of client tick. Processes key bindings and updates teammate distances.
     */
    public static void endClientTick() {
        for (var key : TeamsKeys.KEYS) {
            if (key.keyBinding.consumeClick()) {
                key.onPress.execute(Minecraft.getInstance());
            }
        }
        
        // Update teammate distances
        com.t2pellet.teams.client.core.TeammateDistanceTracker.tick();
    }
    
    /**
     * Handles mouse click events for HUD dragging.
     * @param mouseX The mouse X position
     * @param mouseY The mouse Y position
     * @param button The mouse button clicked
     * @return true if the click was handled
     */
    public static boolean onMouseClick(double mouseX, double mouseY, int button) {
        return onMouseClick(mouseX, mouseY, button, false);
    }
    
    /**
     * Handles mouse click events for HUD dragging with optional coordinate scaling.
     * @param mouseX The mouse X position
     * @param mouseY The mouse Y position
     * @param button The mouse button clicked
     * @param alreadyScaled Whether coordinates are already GUI-scaled
     * @return true if the click was handled
     */
    public static boolean onMouseClick(double mouseX, double mouseY, int button, boolean alreadyScaled) {
        // Only handle left click (button 0)
        if (button != 0 || com.t2pellet.teams.client.ui.hud.HudDragManager.isLocked()) {
            return false;
        }
        
        Minecraft client = Minecraft.getInstance();
        int scaledMouseX;
        int scaledMouseY;
        
        if (alreadyScaled) {
            // Called from a Screen - coordinates are already GUI scaled
            scaledMouseX = (int) mouseX;
            scaledMouseY = (int) mouseY;
        } else {
            // Called from GLFW - need to convert from window to GUI scaled coordinates
            scaledMouseX = (int) (mouseX * client.getWindow().getGuiScaledWidth() / client.getWindow().getScreenWidth());
            scaledMouseY = (int) (mouseY * client.getWindow().getGuiScaledHeight() / client.getWindow().getScreenHeight());
        }
        
        // Check if clicking on status overlay
        if (status.enabled && ClientTeam.INSTANCE.isInTeam()) {
            int statusX = status.getBaseX();
            int statusY = status.getBaseY();
            int statusWidth = status.getWidth();
            int statusHeight = status.getHeight();
            float statusScale = status.getScale();
            
            if (com.t2pellet.teams.client.ui.hud.HudDragManager.isMouseOver(scaledMouseX, scaledMouseY, statusX, statusY, statusWidth, statusHeight, statusScale)) {
                com.t2pellet.teams.client.ui.hud.HudDragManager.startDrag(
                    com.t2pellet.teams.client.ui.hud.HudDragManager.DragTarget.STATUS,
                    scaledMouseX, scaledMouseY, statusX, statusY
                );
                return true;
            }
        }
        
        // Check if clicking on compass overlay
        if (compass.enabled && compass.isShowing()) {
            int compassX = compass.getBaseX();
            int compassY = compass.getBaseY();
            int compassWidth = compass.getWidth();
            int compassHeight = compass.getHeight();
            float compassScale = compass.getScale();
            
            if (com.t2pellet.teams.client.ui.hud.HudDragManager.isMouseOver(scaledMouseX, scaledMouseY, compassX, compassY, compassWidth, compassHeight, compassScale)) {
                com.t2pellet.teams.client.ui.hud.HudDragManager.startDrag(
                    com.t2pellet.teams.client.ui.hud.HudDragManager.DragTarget.COMPASS,
                    scaledMouseX, scaledMouseY, compassX, compassY
                );
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Handles mouse drag events for HUD repositioning.
     * @param mouseX The mouse X position
     * @param mouseY The mouse Y position
     * @param button The mouse button
     * @param dragX The drag delta X
     * @param dragY The drag delta Y
     * @return true if the drag was handled
     */
    public static boolean onMouseDrag(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return onMouseDrag(mouseX, mouseY, button, dragX, dragY, false);
    }
    
    /**
     * Handles mouse drag events for HUD repositioning with optional coordinate scaling.
     * @param mouseX The mouse X position
     * @param mouseY The mouse Y position
     * @param button The mouse button
     * @param dragX The drag delta X
     * @param dragY The drag delta Y
     * @param alreadyScaled Whether coordinates are already GUI-scaled
     * @return true if the drag was handled
     */
    public static boolean onMouseDrag(double mouseX, double mouseY, int button, double dragX, double dragY, boolean alreadyScaled) {
        if (!com.t2pellet.teams.client.ui.hud.HudDragManager.isDragging()) {
            return false;
        }
        
        Minecraft client = Minecraft.getInstance();
        int scaledMouseX;
        int scaledMouseY;
        
        if (alreadyScaled) {
            // Called from a Screen - coordinates are already GUI scaled
            scaledMouseX = (int) mouseX;
            scaledMouseY = (int) mouseY;
        } else {
            // Called from GLFW - need to convert from window to GUI scaled coordinates
            scaledMouseX = (int) (mouseX * client.getWindow().getGuiScaledWidth() / client.getWindow().getScreenWidth());
            scaledMouseY = (int) (mouseY * client.getWindow().getGuiScaledHeight() / client.getWindow().getScreenHeight());
        }
        
        com.t2pellet.teams.client.ui.hud.HudDragManager.updateDrag(scaledMouseX, scaledMouseY);
        return true;
    }
    
    /**
     * Handles mouse release events to end HUD dragging.
     * @param mouseX The mouse X position
     * @param mouseY The mouse Y position
     * @param button The mouse button
     * @return true if the release was handled
     */
    public static boolean onMouseRelease(double mouseX, double mouseY, int button) {
        return onMouseRelease(mouseX, mouseY, button, false);
    }
    
    /**
     * Handles mouse release events to end HUD dragging with optional coordinate scaling.
     * @param mouseX The mouse X position
     * @param mouseY The mouse Y position
     * @param button The mouse button
     * @param alreadyScaled Whether coordinates are already GUI-scaled
     * @return true if the release was handled
     */
    public static boolean onMouseRelease(double mouseX, double mouseY, int button, boolean alreadyScaled) {
        if (!com.t2pellet.teams.client.ui.hud.HudDragManager.isDragging()) {
            return false;
        }
        
        com.t2pellet.teams.client.ui.hud.HudDragManager.endDrag();
        return true;
    }

    /**
     * Handles team update packets from the server (join/leave events).
     * @param team The team name
     * @param player The player name
     * @param action The update action (JOIN or LEAVE)
     * @param isLocal Whether this is the local player
     */
    public static void handleTeamUpdatePacket(String team, String player, S2CTeamUpdatePacket.Action action, boolean isLocal) {
        switch (action) {
            case JOINED -> Minecraft.getInstance().getToasts().addToast(new ToastJoin(team, player, isLocal));
            case LEFT -> Minecraft.getInstance().getToasts().addToast(new ToastLeave(team, player, isLocal));
        }
    }

    /**
     * Handles team request packets from the server (player requested to join).
     * @param name The requester's name
     * @param id The requester's UUID
     */
    public static void handleTeamRequestedPacket(String name, UUID id) {
        Minecraft.getInstance().getToasts().addToast(new ToastRequested(ClientTeam.INSTANCE.getName(), name, id));
    }

    /**
     * Handles team invite sent packets from the server (invite was sent to player).
     * @param team The team name
     * @param player The invited player name
     */
    public static void handleTeamInviteSentPacket(String team,String player) {
        Minecraft.getInstance().getToasts().addToast(new ToastInviteSent(team, player));
    }

    /**
     * Handles team player data packets from the server (add/update/remove players).
     * @param tag The NBT tag containing player data
     */
    public static void handleTeamPlayerDataPacket(CompoundTag tag) {
        UUID uuid = tag.getUUID(S2CTeamPlayerDataPacket.ID_KEY);
        S2CTeamPlayerDataPacket.Type type;
        try {
            type = S2CTeamPlayerDataPacket.Type.valueOf(tag.getString(S2CTeamPlayerDataPacket.TYPE_KEY));
        } catch (IllegalArgumentException e) {
            TeamsHUD.LOGGER.error("Invalid player data packet type: {}", tag.getString(S2CTeamPlayerDataPacket.TYPE_KEY));
            return;
        }
        
        switch (type) {
            case ADD -> {
                if (ClientTeam.INSTANCE.hasPlayer(uuid)) return;

                String name = tag.getString(S2CTeamPlayerDataPacket.NAME_KEY);
                float health = tag.getFloat(S2CTeamPlayerDataPacket.HEALTH_KEY);
                int hunger = tag.getInt(S2CTeamPlayerDataPacket.HUNGER_KEY);

                // Get skin data
                String skinVal = tag.getString(S2CTeamPlayerDataPacket.SKIN_KEY);
                String skinSig = tag.getString(S2CTeamPlayerDataPacket.SKIN_SIG_KEY);
                // Force download
                if (!skinVal.isEmpty()) {
                    GameProfile dummy = new GameProfile(UUID.randomUUID(), "");
                    dummy.getProperties().put("textures", new Property("textures", skinVal, skinSig));
                    Minecraft.getInstance().getSkinManager().registerSkins(dummy, (textureType, id, texture) -> {
                        if (textureType == MinecraftProfileTexture.Type.SKIN) {
                            ClientTeam.INSTANCE.addPlayer(uuid, name, id, health, hunger);
                        }
                    }, false);
                } else {
                    ClientTeam.INSTANCE.addPlayer(uuid, name, DefaultPlayerSkin.getDefaultSkin(uuid), health, hunger);
                }
            }
            case UPDATE -> {
                float health = tag.getFloat(S2CTeamPlayerDataPacket.HEALTH_KEY);
                int hunger = tag.getInt(S2CTeamPlayerDataPacket.HUNGER_KEY);
                ClientTeam.INSTANCE.updatePlayer(uuid, health, hunger);
            }
            case REMOVE -> {
                ClientTeam.INSTANCE.removePlayer(uuid);
            }
        }
    }

    /**
     * Handles team invited packets from the server (received invite to team).
     * @param team The team name
     */
    public static void handleTeamInvitedPacket(String team) {
        Minecraft.getInstance().getToasts().addToast(new ToastInvited(team));
    }
}
