package com.xxinvictus.teamshudplus.mixin;

import com.mojang.authlib.GameProfile;
import com.xxinvictus.teamshudplus.TeamsHUDPlus;
import com.xxinvictus.teamshudplus.core.IHasTeam;
import com.xxinvictus.teamshudplus.core.ModTeam;
import com.xxinvictus.teamshudplus.core.TeamDB;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for ServerPlayer to implement team functionality.
 * Adds team membership tracking and persistence to server players.
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements IHasTeam {
	/** Shadowed game mode field */
	@Shadow @Final public ServerPlayerGameMode gameMode;

	/** Shadowed server level accessor 
	 * @return The server level
	 */
	@Shadow public abstract ServerLevel serverLevel();

	/** The team this player is in */
	@Unique
	private ModTeam team;

	/**
	 * Mixin constructor.
	 * @param $$0 The level
	 * @param $$1 The block position
	 * @param $$2 The yaw rotation
	 * @param $$3 The game profile
	 */
	public ServerPlayerMixin(Level $$0, BlockPos $$1, float $$2, GameProfile $$3) {
		super($$0, $$1, $$2, $$3);
	}


	@Override
	public boolean hasTeam() {
		return team != null;
	}

	@Override
	public ModTeam getTeam() {
		return team;
	}

	/**
	 * Sets the player's team.
	 */
	@Override
	public void setTeam(ModTeam team) {
		this.team = team;
	}

	/**
	 * Checks if another player is a teammate.
	 */
	@Override
	public boolean isTeammate(ServerPlayer other) {
		if (team == null || other == null) {
			return false;
		}
		ModTeam otherTeam = ((IHasTeam) other).getTeam();
		if (otherTeam == null) {
			return false;
		}
		return team.equals(otherTeam);
	}

	/**
	 * Saves team data to NBT.
	 */
	@Inject(at = @At(value = "TAIL"), method = "addAdditionalSaveData")
	private void writeCustomDataToNbt(CompoundTag nbt, CallbackInfo info) {
		if (team != null) {
			nbt.putString("playerTeam", team.getName());
		}
	}

	/**
	 * Loads team data from NBT.
	 */
	@Inject(at = @At(value = "TAIL"), method = "readAdditionalSaveData")
	private void readCustomDataFromNbt(CompoundTag nbt, CallbackInfo info) {
		if (team == null && nbt.contains("playerTeam")) {
			team = TeamDB.getOrMakeDefault(this.serverLevel().getServer()).getTeam(nbt.getString("playerTeam"));
			if (team == null || !team.hasPlayer(getUUID())) {
				team = null;
			}
		}
	}

	@Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/level/ServerPlayer;getHealth()F",ordinal = 1), method = "doTick")
	private void playerTick(CallbackInfo info) {
		var player = (ServerPlayer) ((Object) this);
		TeamsHUDPlus.onPlayerHealthUpdate(player,player.getHealth(),player.getFoodData().getFoodLevel());
	}

	@Override
	public boolean isSpectator() {
		return this.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
	}

	@Override
	public boolean isCreative() {
		return this.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
	}
}
