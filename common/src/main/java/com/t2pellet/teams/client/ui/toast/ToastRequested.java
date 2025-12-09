package com.t2pellet.teams.client.ui.toast;

import net.minecraft.client.resources.language.I18n;

import java.util.UUID;

/**
 * Toast notification shown when another player requests to join the team.
 */
public class ToastRequested extends RespondableTeamToast {

    /** The requesting player's UUID */
    public final UUID id;
    private final String name;

    /**
     * Creates a join request received toast.
     * @param team The team name
     * @param name The requesting player's name
     * @param id The requesting player's UUID
     */
    public ToastRequested(String team, String name, UUID id) {
        super(team);
        this.name = name;
        this.id = id;
    }

    @Override
    public String title() {
        return I18n.get("teams.toast.requested", name);
    }
}
