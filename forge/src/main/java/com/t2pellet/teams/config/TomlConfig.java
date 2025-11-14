package com.t2pellet.teams.config;

import com.t2pellet.teams.platform.MultiloaderConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.world.scores.Team;
import net.minecraftforge.common.ForgeConfigSpec;

public class TomlConfig implements MultiloaderConfig {

    @Override
    public boolean showInvisibleTeammates() {
        return Server.showInvisibleTeammates.get();
    }

    @Override
    public boolean friendlyFireEnabled() {
        return Server.friendlyFireEnabled.get();
    }

    @Override
    public Team.Visibility nameTagVisibility() {
        return Server.nameTagVisibility.get();
    }

    @Override
    public ChatFormatting colour() {
        return Server.colour.get();
    }

    @Override
    public Team.Visibility deathMessageVisibility() {
        return Server.deathMessageVisibility.get();
    }

    @Override
    public Team.CollisionRule collisionRule() {
        return Server.collisionRule.get();
    }

    @Override
    public boolean enableCompassHUD() {
        return Client.enableCompassHUD.get();
    }

    @Override
    public boolean enableStatusHUD() {
        return Client.enableStatusHUD.get();
    }

    @Override
    public int toastDuration() {
        return Client.toastDuration.get();
    }

    @Override
    public boolean showHunger() {
        return Client.showHunger.get();
    }

    @Override
    public boolean syncAdvancements() {
        return Server.syncAdvancements.get();
    }

    @Override
    public int compassDetectionDistance() {
        return Client.compassDetectionDistance.get();
    }

    @Override
    public int maxCompassDetectionDistance() {
        return Server.maxCompassDetectionDistance.get();
    }

    public static class Server {
        public static ForgeConfigSpec.BooleanValue showInvisibleTeammates;
        public static ForgeConfigSpec.BooleanValue friendlyFireEnabled;
        public static ForgeConfigSpec.EnumValue<Team.Visibility> nameTagVisibility;
        public static ForgeConfigSpec.EnumValue<ChatFormatting> colour;
        public static ForgeConfigSpec.EnumValue<Team.Visibility> deathMessageVisibility;
        public static ForgeConfigSpec.ConfigValue<Team.CollisionRule> collisionRule;
        public static ForgeConfigSpec.BooleanValue syncAdvancements;
        public static ForgeConfigSpec.IntValue maxCompassDetectionDistance;

        public Server(ForgeConfigSpec.Builder builder) {
            builder.push("general");
            showInvisibleTeammates = builder.define("show_invisible_teammates",true);
            friendlyFireEnabled = builder.define("friendly_fire_enabled",false);
            nameTagVisibility = builder.defineEnum("name_tag_visibility", Team.Visibility.ALWAYS);
            colour = builder.defineEnum("colour",ChatFormatting.BOLD);
            deathMessageVisibility = builder.defineEnum("death_message_visibility", Team.Visibility.ALWAYS);
            collisionRule = builder.comment("Note that 'push own team' and 'push other teams' are swapped.").defineEnum("collision_rule", Team.CollisionRule.PUSH_OWN_TEAM);
            syncAdvancements = builder.comment("Sync advancements between team members").define("sync_advancements", true);
            maxCompassDetectionDistance = builder.comment("Server-enforced maximum compass detection distance (in blocks). Client settings cannot exceed this value.").defineInRange("max_compass_detection_distance", 512, 16, 2048);
            builder.pop();
        }
    }

    public static class Client {
        public static ForgeConfigSpec.BooleanValue enableCompassHUD;
        public static ForgeConfigSpec.BooleanValue enableStatusHUD;
        public static ForgeConfigSpec.IntValue toastDuration;
        public static ForgeConfigSpec.BooleanValue showHunger;
        public static ForgeConfigSpec.IntValue compassDetectionDistance;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.push("visual");
            enableCompassHUD = builder.define("enable_compass_hud",true);
            enableStatusHUD = builder.define("enable_status_hud",true);
            toastDuration = builder.comment("How long teams toast notifications should last").defineInRange("toast_duration",5,0,100000000);
            showHunger = builder.comment("Show other team members' hunger").define("show_hunger",true);
            compassDetectionDistance = builder.comment("Maximum detection distance for the compass HUD (in blocks)").defineInRange("compass_detection_distance", 128, 16, 1024);
            builder.pop();
        }
    }
}
