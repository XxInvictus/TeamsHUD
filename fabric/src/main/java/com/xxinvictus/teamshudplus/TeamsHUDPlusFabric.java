package com.xxinvictus.teamshudplus;

import com.xxinvictus.teamshudplus.TeamsHUDPlus;
import com.xxinvictus.teamshudplus.command.TeamCommand;
import com.xxinvictus.teamshudplus.config.ConfigMigration;
import com.xxinvictus.teamshudplus.config.TeamsConfig;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

/**
 * Fabric platform entry point for TeamsHUDPlus.
 * Registers configs, event listeners, and initializes common code.
 */
public class TeamsHUDPlusFabric implements ModInitializer {

	private static TeamsConfig config;

	/**
	 * Gets the Fabric configuration instance.
	 * @return The config instance
	 */
	public static TeamsConfig getConfig() {
		return config;
	}

	@Override
	public void onInitialize() {
		TeamsHUDPlus.LOGGER.info("Teams fabric mod init!");

		ServerLifecycleEvents.SERVER_STARTED.register(TeamsHUDPlus::onServerStarted);
		ServerLifecycleEvents.SERVER_STOPPED.register(TeamsHUDPlus::onServerStopped);
		
		// Attempt to migrate old config if it exists
		ConfigMigration.migrateFabricConfig(ConfigMigration.getConfigDirectory());
		
		// MultiloaderConfig registration
		AutoConfig.register(TeamsConfig.class, JanksonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(TeamsConfig.class).getConfig();
		// Command registration
		CommandRegistrationCallback.EVENT.register((dispatcher,a,b) -> TeamCommand.register(dispatcher));
		// Event hooks
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer player = handler.player;
			TeamsHUDPlus.playerConnect(player);
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			ServerPlayer player = handler.player;
			TeamsHUDPlus.playerDisconnect(player);
		});
		ServerPlayerEvents.COPY_FROM.register(TeamsHUDPlus::playerClone);
		TeamsHUDPlus.init();
	}
}
