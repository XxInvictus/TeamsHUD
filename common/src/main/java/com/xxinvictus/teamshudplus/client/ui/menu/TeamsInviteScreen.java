package com.xxinvictus.teamshudplus.client.ui.menu;

import com.xxinvictus.teamshudplus.client.core.ClientTeam;
import com.xxinvictus.teamshudplus.client.ui.toast.ToastInviteSent;
import com.xxinvictus.teamshudplus.core.ModComponents;
import com.xxinvictus.teamshudplus.network.server.C2STeamInvitePacket;
import com.xxinvictus.teamshudplus.platform.Services;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Screen for inviting a player to the team.
 */
public class TeamsInviteScreen extends TeamsInputScreen {

    /**
     * Creates a team invite screen.
     * @param parent The parent screen
     */
    public TeamsInviteScreen(Screen parent) {
        super(parent, ModComponents.INVITE_TITLE_TEXT);
    }
    @Override
    protected Component getSubmitText() {
        return ModComponents.INVITE_TEXT2;
    }

    @Override
    protected void onSubmit(Button widget) {
        Services.PLATFORM.sendToServer(new C2STeamInvitePacket(inputField.getValue()));
        minecraft.getToasts().addToast(new ToastInviteSent(ClientTeam.INSTANCE.getName(), inputField.getValue()));
        minecraft.setScreen(parent);
    }

    @Override
    protected boolean submitCondition() {
        String clientName = minecraft.player.getName().getString();
        return minecraft.getConnection().getOnlinePlayers()
                .stream()
                .anyMatch(entry -> !entry.getProfile().getName().equals(clientName) && entry.getProfile().getName().equals(inputField.getValue()));
    }
}
