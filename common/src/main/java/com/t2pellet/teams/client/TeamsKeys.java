package com.t2pellet.teams.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.t2pellet.teams.client.core.ClientTeam;
import com.t2pellet.teams.client.ui.toast.ToastInvited;
import com.t2pellet.teams.client.ui.toast.ToastRequested;
import com.t2pellet.teams.network.server.C2STeamJoinPacket;
import com.t2pellet.teams.platform.Services;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import org.lwjgl.glfw.GLFW;

/**
 * Defines all key bindings for the TeamsHUD mod.
 * Includes accept/reject invitations, toggle HUD, and toggle HUD lock.
 */
public class TeamsKeys {

    /**
     * Represents a key binding with its associated action.
     */
    public static class TeamsKey {
        /**
         * Functional interface for key press actions.
         */
        @FunctionalInterface
        public interface OnPress {
            /**
             * Executes the key press action.
             * @param client The Minecraft client instance
             */
            void execute(Minecraft client);
        }

        private TeamsKey(String keyName, int keyBind, OnPress action) {
            keyBinding = new KeyMapping(
                    keyName,
                    InputConstants.Type.KEYSYM,
                    keyBind,
                    "key.category.teams"
            );
            onPress = action;
        }

        /**
         * Registers this key binding with the platform.
         */
        public void register() {
            Services.PLATFORM.registerKeyBinding(keyBinding);
        }

        /**
         * Gets the localized name of this key binding.
         * @return The localized key name
         */
        public String getLocalizedName() {
            return keyBinding.getTranslatedKeyMessage().getString();
        }

        /** The Minecraft key mapping */
        final KeyMapping keyBinding;
        /** The action to execute when pressed */
        final OnPress onPress;
    }

    /** Key to accept team invitations or requests (default: right bracket) */
    public static final TeamsKey ACCEPT = new TeamsKey("key.teams.accept", GLFW.GLFW_KEY_RIGHT_BRACKET, client -> {
        var toastManager = client.getToasts();
        ToastInvited invited = toastManager.getToast(ToastInvited.class, Toast.NO_TOKEN);
        if (invited != null) {
            invited.respond();
            Services.PLATFORM.sendToServer(new C2STeamJoinPacket(invited.team));
        } else {
            ToastRequested requested = toastManager.getToast(ToastRequested.class, Toast.NO_TOKEN);
            if (requested != null) {
                requested.respond();
                Services.PLATFORM.sendToServer(new C2STeamJoinPacket(ClientTeam.INSTANCE.getName()));
            }
        }
    });

    /** Key to reject team invitations or requests (default: left bracket) */
    public static final TeamsKey REJECT = new TeamsKey("key.teams.reject", GLFW.GLFW_KEY_LEFT_BRACKET, client -> {
        var toastManager = client.getToasts();
        ToastInvited toast = toastManager.getToast(ToastInvited.class, Toast.NO_TOKEN);
        if (toast != null) {
            toast.respond();
        } else {
            ToastRequested requested = toastManager.getToast(ToastRequested.class, Toast.NO_TOKEN);
            if (requested != null) {
                requested.respond();
            }
        }
    });

    /** Key to toggle HUD visibility (default: B) */
    public static final TeamsKey TOGGLE_HUD = new TeamsKey("key.teams.toggle_hud", GLFW.GLFW_KEY_B, client -> {
        TeamsHUDClient.compass.enabled = !TeamsHUDClient.compass.enabled;
        TeamsHUDClient.status.enabled = !TeamsHUDClient.status.enabled;
    });

    /** Key to toggle HUD lock/unlock for repositioning (default: L) */
    public static final TeamsKey TOGGLE_HUD_LOCK = new TeamsKey("key.teams.toggle_hud_lock", GLFW.GLFW_KEY_L, client -> {
        if (com.t2pellet.teams.client.ui.hud.HudDragManager.isLocked()) {
            // Unlock and open drag screen
            com.t2pellet.teams.client.ui.hud.HudDragManager.toggleLock();
            client.setScreen(new com.t2pellet.teams.client.ui.menu.HudDragScreen());
        } else {
            // Lock (this happens from within the drag screen via ESC/L key)
            com.t2pellet.teams.client.ui.hud.HudDragManager.toggleLock();
            client.player.displayClientMessage(net.minecraft.network.chat.Component.literal("HUD locked"), true);
        }
    });

    /** Array of all registered keys */
    static final TeamsKey[] KEYS = {
            ACCEPT,
            REJECT,
            TOGGLE_HUD,
            TOGGLE_HUD_LOCK
    };

}
