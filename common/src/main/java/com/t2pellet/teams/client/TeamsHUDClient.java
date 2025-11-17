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

public class TeamsHUDClient {

    public static final StatusOverlay status = new StatusOverlay();
    public static final CompassOverlay compass = new CompassOverlay();

    public static void registerKeybinding(KeyMapping keyMapping) {
        Minecraft.getInstance().options.keyMappings = ArrayUtils.add(Minecraft.getInstance().options.keyMappings, keyMapping);
    }

    public static final ResourceLocation TEAMS_BUTTON_TEXTURE = TeamsHUD.id("textures/gui/buttonsmall.png");

    public static void registerKeybinds() {
        // Register keybinds
        for (TeamsKeys.TeamsKey key : TeamsKeys.KEYS) {
            key.register();
        }
    }

    public static void clientDisconnect() {
        ClientTeam.INSTANCE.reset();
        ClientTeamDB.INSTANCE.clear();
    }

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

    public static void endClientTick() {
        for (var key : TeamsKeys.KEYS) {
            if (key.keyBinding.consumeClick()) {
                key.onPress.execute(Minecraft.getInstance());
            }
        }
    }
    
    public static boolean onMouseClick(double mouseX, double mouseY, int button) {
        // Only handle left click (button 0)
        if (button != 0 || com.t2pellet.teams.client.ui.hud.HudDragManager.isLocked()) {
            return false;
        }
        
        Minecraft client = Minecraft.getInstance();
        int scaledMouseX = (int) (mouseX * client.getWindow().getGuiScaledWidth() / client.getWindow().getScreenWidth());
        int scaledMouseY = (int) (mouseY * client.getWindow().getGuiScaledHeight() / client.getWindow().getScreenHeight());
        
        // Check if clicking on status overlay
        if (status.enabled && !ClientTeam.INSTANCE.isTeamEmpty()) {
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
    
    public static boolean onMouseDrag(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!com.t2pellet.teams.client.ui.hud.HudDragManager.isDragging()) {
            return false;
        }
        
        Minecraft client = Minecraft.getInstance();
        int scaledMouseX = (int) (mouseX * client.getWindow().getGuiScaledWidth() / client.getWindow().getScreenWidth());
        int scaledMouseY = (int) (mouseY * client.getWindow().getGuiScaledHeight() / client.getWindow().getScreenHeight());
        
        com.t2pellet.teams.client.ui.hud.HudDragManager.updateDrag(scaledMouseX, scaledMouseY);
        return true;
    }
    
    public static boolean onMouseRelease(double mouseX, double mouseY, int button) {
        if (!com.t2pellet.teams.client.ui.hud.HudDragManager.isDragging()) {
            return false;
        }
        
        com.t2pellet.teams.client.ui.hud.HudDragManager.endDrag();
        return true;
    }

    public static void handleTeamUpdatePacket(String team, String player, S2CTeamUpdatePacket.Action action, boolean isLocal) {
        switch (action) {
            case JOINED -> Minecraft.getInstance().getToasts().addToast(new ToastJoin(team, player, isLocal));
            case LEFT -> Minecraft.getInstance().getToasts().addToast(new ToastLeave(team, player, isLocal));
        }
    }

    public static void handleTeamRequestedPacket(String name, UUID id) {
        Minecraft.getInstance().getToasts().addToast(new ToastRequested(ClientTeam.INSTANCE.getName(), name, id));
    }

    public static void handleTeamInviteSentPacket(String team,String player) {
        Minecraft.getInstance().getToasts().addToast(new ToastInviteSent(team, player));
    }

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

    public static void handleTeamInvitedPacket(String team) {
        Minecraft.getInstance().getToasts().addToast(new ToastInvited(team));
    }
}
