package com.t2pellet.teams.client.ui.toast;

import net.minecraft.client.resources.language.I18n;

/**
 * Toast notification shown when a player leaves a team.
 */
public class ToastLeave extends TeamToast {

    private String name;
    private boolean local;

    /**
     * Creates a team leave toast.
     * @param team The team name
     * @param name The player's name
     * @param local Whether this is the local player leaving
     */
    public ToastLeave(String team, String name, boolean local) {
        super(team);
        this.name = name;
        this.local = local;
    }

    @Override
    public String title() {
        return local ? I18n.get("teams.toast.leave") : I18n.get("teams.toast.left");
    }

    @Override
    public String subTitle() {
        return local ? I18n.get("teams.toast.leave.details", team) : I18n.get("teams.toast.left.details", name);
    }
}
