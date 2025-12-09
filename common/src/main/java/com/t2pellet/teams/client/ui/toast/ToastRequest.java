package com.t2pellet.teams.client.ui.toast;

import net.minecraft.client.resources.language.I18n;

/**
 * Toast notification shown when a join request is sent.
 */
public class ToastRequest extends TeamToast {

    /**
     * Creates a join request toast.
     * @param team The team name
     */
    public ToastRequest(String team) {
        super(team);
    }

    @Override
    public String title() {
        return I18n.get("teams.toast.request");
    }

    @Override
    public String subTitle() {
        return I18n.get("teams.toast.request.details", team);
    }
}
