package com.xxinvictus.teamshudplus.core;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Central registry for all translatable text components used in the mod.
 * Provides localized text for menus, buttons, and messages.
 */
public class ModComponents {

    /** Create team menu title */
    public static final Component CREATE_TITLE = translatable("teamshudplus.menu.create.title");
    /** Lonely player menu title */
    public static final Component LONELY_MENU_TITLE = translatable("teamshudplus.menu.lonely.title");
    /** Create team instruction text */
    public static final Component CREATE_TEXT2 = translatable("teamshudplus.menu.create.text");
    /** Invite player button text */
    public static final Component INVITE_TEXT = translatable("teamshudplus.menu.invite");
    /** Leave team button text */
    public static final Component LEAVE_TEXT = translatable("teamshudplus.menu.leave");
    /** Go back button text */
    public static final Component GO_BACK_TEXT = translatable("teamshudplus.menu.return");
    /** Teams menu title */
    public static final Component TEAMS_MENU_TITLE = translatable("teamshudplus.menu.title");
    /** Default input field text */
    public static final Component DEFAULT_TEXT = translatable("teamshudplus.menu.input");
    /** Create team button text */
    public static final Component CREATE_TEXT = translatable("teamshudplus.menu.create");
    /** Lonely player message text */
    public static final Component LONELY_TEXT = translatable("teamshudplus.menu.lonely.alone");
    /** Duplicate team error message */
    public static final Component DUPLICATE_TEAM = translatable("teamshudplus.error.duplicateteam");
    /** Invite player menu title */
    public static final Component INVITE_TITLE_TEXT = translatable("teamshudplus.menu.invite.title");
    /** Invite player instruction text */
    public static final Component INVITE_TEXT2 = translatable("teamshudplus.menu.invite.text");
    /** Toggle HUD lock button text */
    public static final Component TOGGLE_HUD_LOCK_TEXT = translatable("teamshudplus.menu.toggle_hud_lock");

    /**
     * Creates a translatable component from a language key
     * @param key The translation key
     * @param args Optional formatting arguments
     * @return The translatable component
     */
    public static MutableComponent translatable(String key, Object... args) {
        return Component.translatable(key,args);
    }

    /**
     * Creates a literal text component
     * @param text The literal text
     * @return The text component
     */
    public static MutableComponent literal(String text) {
        return Component.literal(text);
    }

}
