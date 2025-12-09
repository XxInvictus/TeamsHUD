package com.xxinvictus.teamshudplus.client.ui.toast;

import net.minecraft.client.resources.language.I18n;

/**
 * Toast notification shown when a team invitation is successfully sent.
 */
public class ToastInviteSent extends TeamToast {

    private String player;

    /**
     * Creates an invite sent toast.
     * @param team The team name
     * @param player The player who was invited
     */
    public ToastInviteSent(String team, String player) {
        super(team);
        this.player = player;
    }

    @Override
    public String title() {
        return I18n.get("teams.toast.invitesent");
    }

    @Override
    public String subTitle() {
        return I18n.get("teams.toast.invitesent.details", player);
    }
}
