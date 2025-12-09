package com.xxinvictus.teamshudplus.client.ui.toast;

import net.minecraft.client.resources.language.I18n;

/**
 * Toast notification shown when a player is invited to a team.
 */
public class ToastInvited extends RespondableTeamToast {

    /**
     * Creates a team invitation toast.
     * @param team The team name
     */
    public ToastInvited(String team) {
        super(team);
    }

    @Override
    public String title() {
        return I18n.get("teamshudplus.toast.invite", team);
    }

}
