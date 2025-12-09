package com.xxinvictus.teamshudplus.client.ui.menu;

import com.xxinvictus.teamshudplus.client.core.ClientTeamDB;
import com.xxinvictus.teamshudplus.core.ModComponents;
import com.xxinvictus.teamshudplus.network.server.C2STeamCreatePacket;
import com.xxinvictus.teamshudplus.platform.Services;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Screen for creating a new team.
 */
public class TeamsCreateScreen extends TeamsInputScreen {

    /**
     * Creates a team creation screen.
     * @param parent The parent screen
     */
    public TeamsCreateScreen(Screen parent) {
        super(parent, ModComponents.CREATE_TITLE);
    }

    @Override
    protected Component getSubmitText() {
        return ModComponents.CREATE_TEXT2;
    }

    @Override
    protected void onSubmit(Button widget) {
        minecraft.setScreen(new TeamsMainScreen(null));
        Services.PLATFORM.sendToServer(new C2STeamCreatePacket(inputField.getValue()));
    }

    @Override
    protected boolean submitCondition() {
        return !ClientTeamDB.INSTANCE.containsTeam(inputField.getValue());
    }
}
