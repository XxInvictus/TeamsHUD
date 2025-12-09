package com.t2pellet.teams.mixin;

import com.t2pellet.teams.TeamsHUD;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
            TeamsHUD.onAdvancement(player, advancement);
    }
}
