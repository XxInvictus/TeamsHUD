package com.t2pellet.teams.platform;

import com.t2pellet.teams.platform.services.IPlatformHelper;
import com.t2pellet.teams.TeamsHUD;

import java.util.ServiceLoader;

/**
 * Service loader for platform-specific implementations.
 * Uses Java's ServiceLoader to locate platform implementations at runtime.
 * This allows the common code to work with both Forge and Fabric.
 */
public class Services {

    /**
     * Platform helper service providing platform-specific functionality.
     * Automatically loaded based on the current mod loader platform.
     */
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    /**
     * Loads a service implementation for the current platform.
     * The implementation is defined in META-INF/services with the service interface name.
     * @param clazz The service interface class
     * @param <T> The service type
     * @return The loaded service implementation
     * @throws NullPointerException if no service implementation is found
     */
    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        TeamsHUD.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}