package com.atsuishio.superbwarfare.config.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class DisplayConfig {

    public static ForgeConfigSpec.BooleanValue KILL_INDICATION;
    public static ForgeConfigSpec.BooleanValue AMMO_HUD;
    public static ForgeConfigSpec.BooleanValue FLOAT_CROSS_HAIR;
    public static ForgeConfigSpec.BooleanValue CAMERA_ROTATE;
    public static ForgeConfigSpec.BooleanValue ARMOR_PLATE_HUD;
    public static ForgeConfigSpec.BooleanValue STAMINA_HUD;
    public static ForgeConfigSpec.IntValue WEAPON_SCREEN_SHAKE;
    public static ForgeConfigSpec.IntValue EXPLOSION_SCREEN_SHAKE;
    public static ForgeConfigSpec.IntValue SHOCK_SCREEN_SHAKE;

    public static void init(ForgeConfigSpec.Builder builder) {
        builder.push("display");

        builder.comment("Set true if you want to show kill indication while killing an entity");
        KILL_INDICATION = builder.define("kill_indication", true);

        builder.comment("Set true to show ammo and gun info on HUD");
        AMMO_HUD = builder.define("ammo_hud", true);

        builder.comment("Set true to enable float cross hair");
        FLOAT_CROSS_HAIR = builder.define("float_cross_hair", true);

        builder.comment("Set true to enable camera rotate when holding a gun");
        CAMERA_ROTATE = builder.define("camera_rotate", true);

        builder.comment("Set true to enable armor plate hud");
        ARMOR_PLATE_HUD = builder.define("armor_plate_hud", true);

        builder.comment("Set true to enable stamina hud");
        STAMINA_HUD = builder.define("stamina_hud", true);

        builder.comment("The strength of screen shaking while firing with a weapon");
        WEAPON_SCREEN_SHAKE = builder.defineInRange("weapon_screen_shake", 100, 0, 100);

        builder.comment("The strength of screen shaking while exploding");
        EXPLOSION_SCREEN_SHAKE = builder.defineInRange("explosion_screen_shake", 100, 0, 100);

        builder.comment("The strength of screen shaking when shocked");
        SHOCK_SCREEN_SHAKE = builder.defineInRange("shock_screen_shake", 100, 0, 100);

        builder.pop();
    }
}
