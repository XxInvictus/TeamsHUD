package com.xxinvictus.teamshudplus.mixin;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.PlayerAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

/**
 * Mixin accessor for PlayerAdvancements to expose visible advancements.
 * Provides access to the private visible advancements set.
 */
@Mixin(PlayerAdvancements.class)
public interface AdvancementAccessor {

    /**
     * Gets the set of visible advancements for the player
     * @return The set of advancements that are visible to the player
     */
    @Accessor("visible")
    Set<AdvancementHolder> getVisibleAdvancements();

}
