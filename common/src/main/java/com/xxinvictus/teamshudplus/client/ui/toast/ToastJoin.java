package com.xxinvictus.teamshudplus.client.ui.toast;

import net.minecraft.client.resources.language.I18n;

/**
 * Toast notification shown when a player joins a team.
 */
public class ToastJoin extends TeamToast {

    private String name;
    private boolean local;

    /**
     * Creates a team join toast.
     * @param team The team name
     * @param name The player's name
     * @param local Whether this is the local player joining
     */
    public ToastJoin(String team, String name, boolean local) {
        super(team);
        this.name = name;
        this.local = local;
    }

    @Override
    public String title() {
        return local ? I18n.get("teamshudplus.toast.join") : I18n.get("teamshudplus.toast.joined");
    }

    @Override
    public String subTitle() {
        return local ? I18n.get("teamshudplus.toast.join.details", team) : I18n.get("teamshudplus.toast.joined.details", name);
    }
}
