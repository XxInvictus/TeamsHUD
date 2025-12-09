package com.t2pellet.teams.client.ui.menu;

import com.t2pellet.teams.TeamsHUD;
import com.t2pellet.teams.client.core.ClientTeam;
import com.t2pellet.teams.core.ModComponents;
import com.t2pellet.teams.network.server.C2STeamLeavePacket;
import com.t2pellet.teams.platform.Services;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

/**
 * Main team screen shown when the player is in a team.
 * Displays team members and management options.
 */
public class TeamsMainScreen extends TeamsScreen {

    /** Width of the screen */
    static final int WIDTH = 256;
    /** Height of the screen */
    static final int HEIGHT = 166;
    private static final ResourceLocation TEXTURE = new ResourceLocation(TeamsHUD.MODID, "textures/gui/screen_background.png");

    /**
     * Creates a main team screen.
     * @param parent The parent screen
     */
    public TeamsMainScreen(Screen parent) {
        super(parent, ModComponents.TEAMS_MENU_TITLE);
    }

    @Override
    protected void init() {
        super.init();
        int yPos = y + 12;
        int xPos = x + (WIDTH - com.t2pellet.teams.client.ui.menu.TeammateEntry.WIDTH) / 2;
        // Add player buttons
        for (var teammate : ClientTeam.INSTANCE.getTeammates()) {
            boolean local = minecraft.player.getUUID().equals(teammate.id);
            var entry = new TeammateEntry(teammate, xPos, yPos, local);
            addRenderableOnly(entry);
            if (entry.getFavButton() != null) {
                addWidget(entry.getFavButton());
            }
            if (entry.getKickButton() != null) {
                addWidget(entry.getKickButton());
            }
            yPos += 24;
        }
        // Add menu buttons
        addRenderableWidget(Button.builder(ModComponents.LEAVE_TEXT,button -> {
            Services.PLATFORM.sendToServer(new C2STeamLeavePacket());
            minecraft.setScreen(new TeamsLonelyScreen(parent));
        }).bounds(this.width / 2  - 125, y + HEIGHT - 30, 80, 20).build());
        addRenderableWidget(Button.builder(ModComponents.INVITE_TEXT,button -> minecraft.setScreen(new TeamsInviteScreen(this)))
                .bounds(this.width / 2  - 40, y + HEIGHT - 30, 80, 20).build());
        addRenderableWidget(Button.builder(ModComponents.GO_BACK_TEXT, button -> minecraft.setScreen(parent)).bounds(this.width / 2  + 45, y + HEIGHT - 30, 80, 20).build());
        
        // Add HUD lock toggle button (above the bottom row)
        addRenderableWidget(Button.builder(ModComponents.TOGGLE_HUD_LOCK_TEXT, button -> {
            if (com.t2pellet.teams.client.ui.hud.HudDragManager.isLocked()) {
                // Unlock and open drag screen
                com.t2pellet.teams.client.ui.hud.HudDragManager.toggleLock();
                minecraft.setScreen(new HudDragScreen());
            }
        }).bounds(this.width / 2 - 60, y + HEIGHT - 55, 120, 20).build());
    }

    @Override
    protected int getWidth() {
        return WIDTH;
    }

    @Override
    protected int getHeight() {
        return HEIGHT;
    }

    @Override
    protected ResourceLocation getBackgroundTexture() {
        return TEXTURE;
    }

    /**
     * Refreshes this screen or returns to parent if team was left.
     */
    public void refresh() {
        if (!ClientTeam.INSTANCE.isInTeam()) {
            minecraft.setScreen(parent);
        } else {
            minecraft.setScreen(new TeamsMainScreen(parent));
        }
    }

}
