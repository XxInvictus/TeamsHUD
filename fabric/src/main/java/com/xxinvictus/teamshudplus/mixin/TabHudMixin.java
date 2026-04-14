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
 *
 * WARNING: This mixin is extremely fragile. The @ModifyVariable ordinal = 9 targets the
 * 10th local variable stored in PlayerTabOverlay.render(), which depends on the exact
 * bytecode layout. This ordinal needs verification against the 26.1.2 bytecode; if the
 * render method's local variable count or order changed, the ordinal must be updated.
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
