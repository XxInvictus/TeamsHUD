package com.xxinvictus.teamshudplus.client.ui.toast;

import com.xxinvictus.teamshudplus.client.TeamsKeys;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.language.I18n;

/**
 * A team toast that can be responded to with accept/reject actions.
 * Displays key bindings for accepting or rejecting team invitations.
 */
public abstract class RespondableTeamToast extends TeamToast {

    private boolean responded = false;

    /**
     * Creates a respondable team toast.
     * @param team The team name
     */
    public RespondableTeamToast(String team) {
        super(team);
    }

    /**
     * Marks this toast as responded to, causing it to hide.
     */
    public void respond() {
        responded = true;
    }

    @Override
    public String subTitle() {
        String rejectKey = TeamsKeys.REJECT.getLocalizedName();
        String acceptKey = TeamsKeys.ACCEPT.getLocalizedName();
        return I18n.get("teamshudplus.toast.respond", rejectKey, acceptKey);
    }

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent manager, long startTime) {
        if (responded) {
            return Visibility.HIDE;
        }
        return super.render(graphics, manager, startTime);
    }
}
