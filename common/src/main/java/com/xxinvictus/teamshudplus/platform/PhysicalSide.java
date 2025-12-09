package com.xxinvictus.teamshudplus.platform;

/**
 * Represents the physical side where code is executing
 */
public enum PhysicalSide {
    /** Client-side execution */
    CLIENT,
    /** Server-side execution */
    SERVER;

    /**
     * Checks if this is the client side
     * @return true if client side
     */
    public boolean isClient() {
        return this == CLIENT;
    }
}
