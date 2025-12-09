package com.xxinvictus.teamshudplus.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.xxinvictus.teamshudplus.TeamsHUDPlus;

/**
 * Mixin for PlayerAdvancements to intercept advancement awards.
 * Triggers team-wide advancement notifications on the Fabric platform.
 */
@Mixin(PlayerAdvancements.class)
public class AdvancementMixinFabric {

    @Shadow private ServerPlayer player;

    /**
     * Injects into the award method to handle completed advancements.
     * Called when a player completes an advancement criterion.
     * @param advancement The advancement being awarded
     * @param criterionName The criterion that was completed
     * @param ci Callback info returnable
     */
    @Inject(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerAdvancements;markForVisibilityUpdate(Lnet/minecraft/advancements/Advancement;)V"))
    public void advancementCompleted(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> ci) {
            TeamsHUDPlus.onAdvancement(player, advancement);
    }
}
