package com.xxinvictus.teamshudplus.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.xxinvictus.teamshudplus.client.TeamsHUDPlusClient;

/**
 * Mixin for PlayerTabOverlay to adjust tab list positioning when compass HUD is showing.
 */
@Mixin(PlayerTabOverlay.class)
public class TabHudMixin {

    @Shadow @Final private Minecraft minecraft;

    /**
     * Modifies the tab list vertical position to avoid overlap with compass HUD.
     */
    @ModifyVariable(method = "render", at = @At("STORE"), ordinal = 9)
    private int onRenderTabList(int p) {
        if (TeamsHUDPlusClient.compass.isShowing()) {
            float scaledHeight = minecraft.getWindow().getGuiScaledHeight();
            return (int) (scaledHeight * 0.01) + 12 + 16;
        }
        return p;
    }

}
