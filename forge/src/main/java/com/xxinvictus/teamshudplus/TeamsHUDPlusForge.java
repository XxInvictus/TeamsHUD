package com.xxinvictus.teamshudplus;

import com.xxinvictus.teamshudplus.TeamsHUDPlus;
import com.xxinvictus.teamshudplus.client.TeamsHUDPlusClientForge;
import com.xxinvictus.teamshudplus.command.TeamCommand;
import com.xxinvictus.teamshudplus.config.ConfigMigration;
import com.xxinvictus.teamshudplus.config.TomlConfig;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Forge platform entry point for TeamsHUDPlus.
 * Registers configs, event listeners, and initializes client/common code.
 */
@Mod(TeamsHUDPlus.MODID)
public class TeamsHUDPlusForge {
    
    /**
     * Forge mod constructor. Registers configs and event listeners.
     */
    public TeamsHUDPlusForge() {
        TeamsHUDPlus.LOGGER.info("Teams forge mod init!");
        
        // Attempt to migrate old configs if they exist
        ConfigMigration.migrateForgeClientConfig(ConfigMigration.getConfigDirectory());
        ConfigMigration.migrateForgeServerConfig(ConfigMigration.getConfigDirectory());
        
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, SERVER_SPEC);
        IEventBus bus  = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.addListener(this::registerCommand);
        MinecraftForge.EVENT_BUS.addListener(this::login);
        MinecraftForge.EVENT_BUS.addListener(this::logout);
        MinecraftForge.EVENT_BUS.addListener(this::playerClone);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarted);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStopped);
        MinecraftForge.EVENT_BUS.addListener(this::onAdvancement);

        if (FMLEnvironment.dist.isClient()) {
            TeamsHUDPlusClientForge.init(bus);
        }
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
    
        // Use Forge to bootstrap the Common mod.
        TeamsHUDPlus.init();
    }

    /** Client-side configuration instance */
    public static final TomlConfig.Client CLIENT;
    /** Client-side configuration specification */
    public static final ForgeConfigSpec CLIENT_SPEC;

    /** Server-side configuration instance */
    public static final TomlConfig.Server SERVER;
    /** Server-side configuration specification */
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        final Pair<TomlConfig.Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(TomlConfig.Client::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
        final Pair<TomlConfig.Server, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(TomlConfig.Server::new);
        SERVER_SPEC = specPair2.getRight();
        SERVER = specPair2.getLeft();
    }

    /**
     * Handles advancement earn events.
     */
    private void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        TeamsHUDPlus.onAdvancement((ServerPlayer) event.getEntity(),event.getAdvancement());
    }

    /**
     * Handles server started event.
     */
    private void onServerStarted(ServerStartedEvent event) {
        TeamsHUDPlus.onServerStarted(event.getServer());
    }

    /**
     * Handles server stopped event.
     */
    private void onServerStopped(ServerStoppedEvent event) {
        TeamsHUDPlus.onServerStopped(event.getServer());
    }

    /**
     * Handles player login event.
     */
    private void login(PlayerEvent.PlayerLoggedInEvent event) {
        TeamsHUDPlus.playerConnect((ServerPlayer) event.getEntity());
    }

    /**
     * Handles player logout event.
     */
    private void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        TeamsHUDPlus.playerDisconnect((ServerPlayer) event.getEntity());
    }

    /**
     * Handles player clone event (respawn/dimension change).
     */
    private void playerClone(PlayerEvent.Clone event) {
        TeamsHUDPlus.playerClone((ServerPlayer) event.getOriginal(), (ServerPlayer) event.getEntity(),!event.isWasDeath());
    }

    /**
     * Registers commands with the dispatcher.
     */
    private void registerCommand(RegisterCommandsEvent event) {
        TeamCommand.register(event.getDispatcher());
    }

    /**
     * Common setup event handler.
     */
    private void commonSetup(FMLCommonSetupEvent event) {
    }

}