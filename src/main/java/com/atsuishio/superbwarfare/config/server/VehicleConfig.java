package com.atsuishio.superbwarfare.config.server;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class VehicleConfig {

    public static ForgeConfigSpec.BooleanValue COLLISION_DESTROY_BLOCKS;
    public static ForgeConfigSpec.BooleanValue COLLISION_DESTROY_HARD_BLOCKS;
    public static ForgeConfigSpec.BooleanValue COLLISION_DESTROY_BLOCKS_BEASTLY;
    public static ForgeConfigSpec.BooleanValue VEHICLE_ITEM_PICKUP;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> COLLISION_ENTITY_WHITELIST;

    public static final List<? extends String> DEFAULT_COLLISION_ENTITY_WHITELIST = List.of();

    public static ForgeConfigSpec.IntValue REPAIR_COOLDOWN;
    public static ForgeConfigSpec.DoubleValue REPAIR_AMOUNT;

    public static ForgeConfigSpec.IntValue MK42_HP;
    public static ForgeConfigSpec.IntValue MK42_AP_DAMAGE;
    public static ForgeConfigSpec.IntValue MK42_AP_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue MK42_AP_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.IntValue MK42_HE_DAMAGE;
    public static ForgeConfigSpec.IntValue MK42_HE_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue MK42_HE_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue MLE1934_HP;
    public static ForgeConfigSpec.IntValue MLE1934_AP_DAMAGE;
    public static ForgeConfigSpec.IntValue MLE1934_AP_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue MLE1934_AP_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.IntValue MLE1934_HE_DAMAGE;
    public static ForgeConfigSpec.IntValue MLE1934_HE_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue MLE1934_HE_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue HEAVY_MACHINE_GUN_DAMAGE;

    public static ForgeConfigSpec.IntValue ANNIHILATOR_HP;
    public static ForgeConfigSpec.IntValue ANNIHILATOR_SHOOT_COST;
    public static ForgeConfigSpec.IntValue ANNIHILATOR_MAX_ENERGY;

    public static ForgeConfigSpec.IntValue LASER_TOWER_HP;
    public static ForgeConfigSpec.IntValue LASER_TOWER_COOLDOWN;
    public static ForgeConfigSpec.IntValue LASER_TOWER_DAMAGE;
    public static ForgeConfigSpec.IntValue LASER_TOWER_SHOOT_COST;
    public static ForgeConfigSpec.IntValue LASER_TOWER_MAX_ENERGY;

    public static ForgeConfigSpec.IntValue SPEEDBOAT_HP;
    public static ForgeConfigSpec.IntValue SPEEDBOAT_ENERGY_COST;
    public static ForgeConfigSpec.IntValue SPEEDBOAT_MAX_ENERGY;

    public static ForgeConfigSpec.IntValue WHEELCHAIR_HP;
    public static ForgeConfigSpec.IntValue WHEELCHAIR_JUMP_ENERGY_COST;
    public static ForgeConfigSpec.IntValue WHEELCHAIR_MOVE_ENERGY_COST;
    public static ForgeConfigSpec.IntValue WHEELCHAIR_MAX_ENERGY;

    public static ForgeConfigSpec.IntValue AH_6_HP;
    public static ForgeConfigSpec.IntValue AH_6_MIN_ENERGY_COST;
    public static ForgeConfigSpec.IntValue AH_6_MAX_ENERGY_COST;
    public static ForgeConfigSpec.IntValue AH_6_MAX_ENERGY;
    public static ForgeConfigSpec.IntValue AH_6_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue AH_6_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue AH_6_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.IntValue AH_6_ROCKET_DAMAGE;
    public static ForgeConfigSpec.IntValue AH_6_ROCKET_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue AH_6_ROCKET_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.BooleanValue AH_6_CANNON_DESTROY;

    public static ForgeConfigSpec.IntValue LAV_150_HP;
    public static ForgeConfigSpec.IntValue LAV_150_ENERGY_COST;
    public static ForgeConfigSpec.IntValue LAV_150_MAX_ENERGY;
    public static ForgeConfigSpec.IntValue LAV_150_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue LAV_150_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue LAV_150_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.DoubleValue LAV_150_MACHINE_GUN_DAMAGE;

    public static ForgeConfigSpec.IntValue TOM_6_HP;
    public static ForgeConfigSpec.IntValue TOM_6_MAX_ENERGY;
    public static ForgeConfigSpec.IntValue TOM_6_ENERGY_COST;
    public static ForgeConfigSpec.IntValue TOM_6_BOMB_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue TOM_6_BOMB_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue BMP_2_HP;
    public static ForgeConfigSpec.IntValue BMP_2_ENERGY_COST;
    public static ForgeConfigSpec.IntValue BMP_2_MAX_ENERGY;
    public static ForgeConfigSpec.IntValue BMP_2_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue BMP_2_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue BMP_2_CANNON_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue BMD_4_HP;
    public static ForgeConfigSpec.IntValue BMD_4_ENERGY_COST;
    public static ForgeConfigSpec.IntValue BMD_4_MAX_ENERGY;
    public static ForgeConfigSpec.IntValue BMD_4_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue BMD_4_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue BMD_4_CANNON_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue YX_100_HP;
    public static ForgeConfigSpec.IntValue YX_100_SHOOT_COST;
    public static ForgeConfigSpec.IntValue YX_100_ENERGY_COST;
    public static ForgeConfigSpec.IntValue YX_100_MAX_ENERGY;
    public static ForgeConfigSpec.IntValue YX_100_AP_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue YX_100_AP_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue YX_100_AP_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.IntValue YX_100_HE_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue YX_100_HE_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue YX_100_HE_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.IntValue YX_100_SWARM_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue YX_100_SWARM_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue PRISM_TANK_HP;
    public static ForgeConfigSpec.IntValue PRISM_TANK_MAX_ENERGY;
    public static ForgeConfigSpec.IntValue PRISM_TANK_ENERGY_COST;
    public static ForgeConfigSpec.IntValue PRISM_TANK_DAMAGE_MODE_1;
    public static ForgeConfigSpec.IntValue PRISM_TANK_SHOOT_COST_MODE_1;
    public static ForgeConfigSpec.IntValue PRISM_TANK_AOE_DAMAGE;
    public static ForgeConfigSpec.IntValue PRISM_TANK_AOE_RADIUS;
    public static ForgeConfigSpec.IntValue PRISM_TANK_DAMAGE_MODE_2;
    public static ForgeConfigSpec.IntValue PRISM_TANK_SHOOT_COST_MODE_2;

    public static ForgeConfigSpec.IntValue HPJ11_HP;
    public static ForgeConfigSpec.IntValue HPJ11_MAX_ENERGY;
    public static ForgeConfigSpec.DoubleValue HPJ11_DAMAGE;
    public static ForgeConfigSpec.DoubleValue HPJ11_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue HPJ11_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.IntValue HPJ11_SHOOT_COST;
    public static ForgeConfigSpec.IntValue HPJ11_SEEK_COST;

    public static ForgeConfigSpec.IntValue A_10_HP;
    public static ForgeConfigSpec.IntValue A_10_MAX_ENERGY_COST;
    public static ForgeConfigSpec.IntValue A_10_MAX_ENERGY;
    public static ForgeConfigSpec.IntValue A_10_CANNON_DAMAGE;
    public static ForgeConfigSpec.IntValue A_10_CANNON_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue A_10_CANNON_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.IntValue A_10_ROCKET_DAMAGE;
    public static ForgeConfigSpec.IntValue A_10_ROCKET_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.DoubleValue A_10_ROCKET_EXPLOSION_RADIUS;

    public static void init(ForgeConfigSpec.Builder builder) {
        builder.push("vehicle");

        builder.comment("Allows vehicles to destroy blocks via collision");
        COLLISION_DESTROY_BLOCKS = builder.define("collision_destroy_blocks", false);

        builder.comment("Allows vehicles to destroy hard blocks via collision");
        COLLISION_DESTROY_HARD_BLOCKS = builder.define("collision_destroy_hard_blocks", false);

        builder.comment("Allows vehicles to destroy blocks via collision like a beast");
        COLLISION_DESTROY_BLOCKS_BEASTLY = builder.define("collision_destroy_blocks_beastly", false);

        builder.comment("Allow vehicles to pick up items");
        VEHICLE_ITEM_PICKUP = builder.define("vehicle_item_pickup", true);

        builder.comment("List of entities that can be damaged by collision");
        COLLISION_ENTITY_WHITELIST = builder.defineList("collision_entity_whitelist",
                DEFAULT_COLLISION_ENTITY_WHITELIST,
                e -> e instanceof String);

        builder.push("repair");

        builder.comment("The cooldown of vehicle repair. Set a negative value to disable vehicle repair");
        REPAIR_COOLDOWN = builder.defineInRange("repair_cooldown", 200, -1, 10000000);

        builder.comment("The amount of health restored per tick when a vehicle is self-repairing");
        REPAIR_AMOUNT = builder.defineInRange("repair_amount", 0.05d, 0, 10000000);

        builder.pop();

        builder.push("MK-42");

        builder.comment("The health of MK-42");
        MK42_HP = builder.defineInRange("mk_42_hp", 350, 1, 10000000);

        builder.comment("The AP shell damage of MK-42");
        MK42_AP_DAMAGE = builder.defineInRange("mk_42_ap_damage", 450, 1, 10000000);

        builder.comment("The AP shell explosion damage of MK-42");
        MK42_AP_EXPLOSION_DAMAGE = builder.defineInRange("mk_42_ap_explosion_damage", 120, 1, 10000000);

        builder.comment("The AP shell explosion radius of MK-42");
        MK42_AP_EXPLOSION_RADIUS = builder.defineInRange("mk_42_ap_explosion_radius", 3, 1, 50);

        builder.comment("The HE shell damage of MK-42");
        MK42_HE_DAMAGE = builder.defineInRange("mk_42_he_damage", 150, 1, 10000000);

        builder.comment("The HE shell explosion damage of MK-42");
        MK42_HE_EXPLOSION_DAMAGE = builder.defineInRange("mk_42_he_explosion_damage", 200, 1, 10000000);

        builder.comment("The HE shell explosion radius of MK-42");
        MK42_HE_EXPLOSION_RADIUS = builder.defineInRange("mk_42_he_explosion_radius", 10, 1, 50);

        builder.pop();

        builder.push("MLE-1934");

        builder.comment("The health of MLE-1934");
        MLE1934_HP = builder.defineInRange("mle_1934_hp", 350, 1, 10000000);

        builder.comment("The AP shell damage of MLE-1934");
        MLE1934_AP_DAMAGE = builder.defineInRange("mle_1934_ap_damage", 500, 1, 10000000);

        builder.comment("The AP shell explosion damage of MLE-1934");
        MLE1934_AP_EXPLOSION_DAMAGE = builder.defineInRange("mle_1934_ap_explosion_damage", 150, 1, 10000000);

        builder.comment("The AP shell explosion radius of MLE-1934");
        MLE1934_AP_EXPLOSION_RADIUS = builder.defineInRange("mle_1934_ap_explosion_radius", 4, 1, 50);

        builder.comment("The HE shell damage of MLE-1934");
        MLE1934_HE_DAMAGE = builder.defineInRange("mle_1934_he_damage", 180, 1, 10000000);

        builder.comment("The HE shell explosion damage of MLE-1934");
        MLE1934_HE_EXPLOSION_DAMAGE = builder.defineInRange("mle_1934_he_explosion_damage", 240, 1, 10000000);

        builder.comment("The HE shell explosion radius of MLE-1934");
        MLE1934_HE_EXPLOSION_RADIUS = builder.defineInRange("mle_1934_he_explosion_radius", 12, 1, 50);

        builder.pop();

        builder.push("Heavy Machine Gun");

        builder.comment("The gun damage of 12.7mm HMG");
        HEAVY_MACHINE_GUN_DAMAGE = builder.defineInRange("heavy_machine_gun_damage", 25, 1, 10000000);

        builder.pop();

        builder.push("Annihilator");

        builder.comment("The health of Annihilator");
        ANNIHILATOR_HP = builder.defineInRange("annihilator_hp", 1200, 1, 10000000);

        builder.comment("The energy cost of Annihilator per shoot");
        ANNIHILATOR_SHOOT_COST = builder.defineInRange("annihilator_shoot_cost", 2000000, 0, 2147483647);

        builder.comment("The max energy storage of Annihilator");
        ANNIHILATOR_MAX_ENERGY = builder.defineInRange("annihilator_max_energy", 20000000, 0, 2147483647);

        builder.pop();

        builder.push("Laser Tower");

        builder.comment("The health of Laser Tower");
        LASER_TOWER_HP = builder.defineInRange("laser_tower_hp", 100, 1, 10000000);

        builder.comment("The damage of Laser Tower");
        LASER_TOWER_DAMAGE = builder.defineInRange("laser_tower_damage", 15, 1, 10000000);

        builder.comment("The cooldown time(ticks) of Laser Tower");
        LASER_TOWER_COOLDOWN = builder.defineInRange("laser_tower_cooldown", 40, 15, 10000000);

        builder.comment("The energy cost of Laser Tower per shoot");
        LASER_TOWER_SHOOT_COST = builder.defineInRange("laser_tower_shoot_cost", 5000, 0, 2147483647);

        builder.comment("The max energy storage of Laser Tower");
        LASER_TOWER_MAX_ENERGY = builder.defineInRange("laser_tower_max_energy", 500000, 0, 2147483647);

        builder.pop();

        builder.push("Speedboat");

        builder.comment("The health of Speedboat");
        SPEEDBOAT_HP = builder.defineInRange("speedboat_hp", 200, 1, 10000000);

        builder.comment("The energy cost of Speedboat per tick");
        SPEEDBOAT_ENERGY_COST = builder.defineInRange("speedboat_energy_cost", 16, 0, 2147483647);

        builder.comment("The max energy storage of Speedboat");
        SPEEDBOAT_MAX_ENERGY = builder.defineInRange("speedboat_max_energy", 500000, 0, 2147483647);

        builder.pop();

        builder.push("Wheelchair");

        builder.comment("The health of the wheelchair");
        WHEELCHAIR_HP = builder.defineInRange("wheelchair_hp", 30, 1, 10000000);

        builder.comment("The jump energy cost of the wheelchair");
        WHEELCHAIR_JUMP_ENERGY_COST = builder.defineInRange("wheelchair_jump_energy_cost", 400, 0, 2147483647);

        builder.comment("The move energy cost of the wheelchair");
        WHEELCHAIR_MOVE_ENERGY_COST = builder.defineInRange("wheelchair_move_energy_cost", 1, 0, 2147483647);

        builder.comment("The max energy storage of the wheelchair");
        WHEELCHAIR_MAX_ENERGY = builder.defineInRange("wheelchair_max_energy", 24000, 0, 2147483647);

        builder.pop();

        builder.push("AH_6");

        builder.comment("The health of AH-6");
        AH_6_HP = builder.defineInRange("ah_6_hp", 250, 1, 10000000);

        builder.comment("The min energy cost of AH-6 per tick");
        AH_6_MIN_ENERGY_COST = builder.defineInRange("ah_6_min_energy_cost", 64, 0, 2147483647);

        builder.comment("The max energy cost of AH-6 per tick");
        AH_6_MAX_ENERGY_COST = builder.defineInRange("ah_6_max_energy_cost", 128, 0, 2147483647);

        builder.comment("The max energy storage of AH-6");
        AH_6_MAX_ENERGY = builder.defineInRange("ah_6_max_energy", 5000000, 0, 2147483647);

        builder.comment("The cannon damage of AH-6");
        AH_6_CANNON_DAMAGE = builder.defineInRange("ah_6_cannon_damage", 25, 1, 10000000);

        builder.comment("The cannon explosion damage of AH-6");
        AH_6_CANNON_EXPLOSION_DAMAGE = builder.defineInRange("ah_6_cannon_explosion_damage", 13, 1, 10000000);

        builder.comment("The cannon explosion damage of AH-6");
        AH_6_CANNON_EXPLOSION_RADIUS = builder.defineInRange("ah_6_cannon_explosion_damage", 4d, 1, 10000000);

        builder.comment("The rocket damage of AH-6");
        AH_6_ROCKET_DAMAGE = builder.defineInRange("ah_6_rocket_damage", 80, 1, 10000000);

        builder.comment("The rocket explosion damage of AH-6");
        AH_6_ROCKET_EXPLOSION_DAMAGE = builder.defineInRange("ah_6_rocket_explosion_damage", 40, 1, 10000000);

        builder.comment("The rocket explosion radius of AH-6");
        AH_6_ROCKET_EXPLOSION_RADIUS = builder.defineInRange("ah_6_rocket_explosion_radius", 5, 1, 10000000);

        builder.comment("Whether to destroy the block when cannon of AH-6 hits a block");
        AH_6_CANNON_DESTROY = builder.define("ah_6_cannon_destroy", true);

        builder.pop();

        builder.push("LAV-150");

        builder.comment("The health of LAV-150");
        LAV_150_HP = builder.defineInRange("lav_150_hp", 250, 1, 10000000);

        builder.comment("The energy cost of LAV-150 per tick");
        LAV_150_ENERGY_COST = builder.defineInRange("lav_150_energy_cost", 64, 0, 2147483647);

        builder.comment("The max energy storage of LAV-150");
        LAV_150_MAX_ENERGY = builder.defineInRange("lav_150_max_energy", 5000000, 0, 2147483647);

        builder.comment("The cannon damage of LAV-150");
        LAV_150_CANNON_DAMAGE = builder.defineInRange("lav_150_cannon_damage", 45, 1, 10000000);

        builder.comment("The cannon explosion damage of LAV-150");
        LAV_150_CANNON_EXPLOSION_DAMAGE = builder.defineInRange("lav_150_cannon_explosion_damage", 12, 1, 10000000);

        builder.comment("The cannon explosion radius of LAV-150");
        LAV_150_CANNON_EXPLOSION_RADIUS = builder.defineInRange("lav_150_cannon_explosion_radius", 4d, 1d, 10000000d);

        builder.comment("The machine gun damage of LAV-150");
        LAV_150_MACHINE_GUN_DAMAGE = builder.defineInRange("lav_150_machine_gun_damage", 9.5, 1d, 10000000d);

        builder.pop();

        builder.push("Tom-6");

        builder.comment("The health of Tom-6");
        TOM_6_HP = builder.defineInRange("tom_6_hp", 40, 1, 10000000);

        builder.comment("The energy cost of Tom-6 per tick");
        TOM_6_ENERGY_COST = builder.defineInRange("tom_6_energy_cost", 16, 0, 2147483647);

        builder.comment("The max energy storage of Tom-6");
        TOM_6_MAX_ENERGY = builder.defineInRange("tom_6_max_energy", 100000, 0, 2147483647);

        builder.comment("The Melon Bomb explosion damage of Tom-6");
        TOM_6_BOMB_EXPLOSION_DAMAGE = builder.defineInRange("tom_6_bomb_explosion_damage", 500, 1, 10000000);

        builder.comment("The Melon Bomb explosion radius of Tom-6");
        TOM_6_BOMB_EXPLOSION_RADIUS = builder.defineInRange("tom_6_bomb_explosion_radius", 10d, 1d, 10000000d);

        builder.pop();

        builder.push("BMP-2");

        builder.comment("The health of BMP-2");
        BMP_2_HP = builder.defineInRange("bmp_2_hp", 300, 1, 10000000);

        builder.comment("The energy cost of BMP-2 per tick");
        BMP_2_ENERGY_COST = builder.defineInRange("bmp_2_energy_cost", 64, 0, 2147483647);

        builder.comment("The max energy storage of BMP-2");
        BMP_2_MAX_ENERGY = builder.defineInRange("bmp_2_max_energy", 5000000, 0, 2147483647);

        builder.comment("The cannon damage of BMP-2");
        BMP_2_CANNON_DAMAGE = builder.defineInRange("bmp_2_cannon_damage", 55, 1, 10000000);

        builder.comment("The cannon explosion damage of BMP-2");
        BMP_2_CANNON_EXPLOSION_DAMAGE = builder.defineInRange("bmp_2_cannon_explosion_damage", 15, 1, 10000000);

        builder.comment("The cannon explosion radius of BMP-2");
        BMP_2_CANNON_EXPLOSION_RADIUS = builder.defineInRange("bmp_2_cannon_explosion_radius", 4d, 1d, 10000000d);

        builder.pop();

        builder.push("BMD-4");

        builder.comment("The health of BMD-4");
        BMD_4_HP = builder.defineInRange("bmd_4_hp", 250, 1, 10000000);

        builder.comment("The energy cost of BMD-4 per tick");
        BMD_4_ENERGY_COST = builder.defineInRange("bmd_4_energy_cost", 48, 0, 2147483647);

        builder.comment("The max energy storage of BMD-4");
        BMD_4_MAX_ENERGY = builder.defineInRange("bmd_4_max_energy", 4000000, 0, 2147483647);

        builder.comment("The cannon damage of BMD-4");
        BMD_4_CANNON_DAMAGE = builder.defineInRange("bmd_4_cannon_damage", 45, 1, 10000000);

        builder.comment("The cannon explosion damage of BMD-4");
        BMD_4_CANNON_EXPLOSION_DAMAGE = builder.defineInRange("bmd_4_cannon_explosion_damage", 12, 1, 10000000);

        builder.comment("The cannon explosion radius of BMD-4");
        BMD_4_CANNON_EXPLOSION_RADIUS = builder.defineInRange("bmd_4_cannon_explosion_radius", 3.5d, 1d, 10000000d);

        builder.pop();

        builder.push("YX-100");

        builder.comment("The health of YX-100");
        YX_100_HP = builder.defineInRange("yx_100_hp", 500, 1, 10000000);

        builder.comment("The energy cost of YX-100 per tick");
        YX_100_ENERGY_COST = builder.defineInRange("yx_100_energy_cost", 128, 0, 2147483647);

        builder.comment("The energy cost of YX-100 per shoot");
        YX_100_SHOOT_COST = builder.defineInRange("yx_100_shoot_cost", 24000, 0, 2147483647);

        builder.comment("The max energy storage of YX-100");
        YX_100_MAX_ENERGY = builder.defineInRange("yx_100_max_energy", 20000000, 0, 2147483647);

        builder.comment("The cannon damage of YX-100");
        YX_100_AP_CANNON_DAMAGE = builder.defineInRange("yx_100_ap_cannon_damage", 500, 1, 10000000);

        builder.comment("The cannon explosion damage of YX-100");
        YX_100_AP_CANNON_EXPLOSION_DAMAGE = builder.defineInRange("yx_100_ap_cannon_explosion_damage", 100, 1, 10000000);

        builder.comment("The cannon explosion radius of YX-100");
        YX_100_AP_CANNON_EXPLOSION_RADIUS = builder.defineInRange("yx_100_ap_cannon_explosion_radius", 4d, 1d, 10000000d);

        builder.comment("The cannon damage of YX-100");
        YX_100_HE_CANNON_DAMAGE = builder.defineInRange("yx_100_he_cannon_damage", 150, 1, 10000000);

        builder.comment("The cannon explosion damage of YX-100");
        YX_100_HE_CANNON_EXPLOSION_DAMAGE = builder.defineInRange("yx_100_he_cannon_explosion_damage", 150, 1, 10000000);

        builder.comment("The cannon explosion radius of YX-100");
        YX_100_HE_CANNON_EXPLOSION_RADIUS = builder.defineInRange("yx_100_he_cannon_explosion_radius", 10d, 1d, 10000000d);

        builder.comment("The swarm drone explosion damage of YX-100");
        YX_100_SWARM_EXPLOSION_DAMAGE = builder.defineInRange("yx_100_swarm_drone_explosion_damage", 80, 1, 10000000);

        builder.comment("The swarm drone explosion radius of YX-100");
        YX_100_SWARM_EXPLOSION_RADIUS = builder.defineInRange("yx_100_swarm_drone_explosion_radius", 5d, 1d, 10000000d);

        builder.pop();

        builder.push("Prism Tank");

        builder.comment("The health of Prism Tank");
        PRISM_TANK_HP = builder.defineInRange("prism_tank_hp", 400, 1, 10000000);

        builder.comment("The max energy storage of Prism Tank");
        PRISM_TANK_MAX_ENERGY = builder.defineInRange("prism_tank_energy", 20000000, 0, 2147483647);

        builder.comment("The energy cost of Prism Tank per tick");
        PRISM_TANK_ENERGY_COST= builder.defineInRange("prism_tank_energy_cost", 96, 0, 2147483647);

        builder.comment("The Laser Damage of Prism Tank Mode 1");
        PRISM_TANK_DAMAGE_MODE_1 = builder.defineInRange("prism_tank_damage_mode_1", 350, 0, 2147483647);

        builder.comment("The energy cost of Prism Tank Mode 1");
        PRISM_TANK_SHOOT_COST_MODE_1 = builder.defineInRange("prism_tank_shoot_cost_mode_1", 100000, 1, 10000000);

        builder.comment("The laser AOE damage of Prism Tank");
        PRISM_TANK_AOE_DAMAGE = builder.defineInRange("prism_tank_aoe_damage", 72, 1, 10000000);

        builder.comment("The laser AOE radius of Prism Tank");
        PRISM_TANK_AOE_RADIUS = builder.defineInRange("prism_tank_aoe_radius", 12, 1, 10000000);

        builder.comment("The Laser Damage of Prism Tank Mode 2 per tick");
        PRISM_TANK_DAMAGE_MODE_2 = builder.defineInRange("prism_tank_damage_mode_2", 15, 1, 10000000);

        builder.comment("The energy cost of Prism Tank Mode 2 per tick");
        PRISM_TANK_SHOOT_COST_MODE_2 = builder.defineInRange("prism_tank_shoot_cost_mode_2", 5000, 1, 10000000);

        builder.pop();

        builder.push("HPJ-11");

        builder.comment("The health of HPJ-11");
        HPJ11_HP = builder.defineInRange("hpj_11_hp", 350, 1, 10000000);

        builder.comment("The max energy storage of HPJ-11");
        HPJ11_MAX_ENERGY = builder.defineInRange("hpj_11_max_energy", 5000000, 0, 2147483647);

        builder.comment("The damage of HPJ-11");
        HPJ11_DAMAGE = builder.defineInRange("hpj_11_damage", 20d, 1, 10000000);

        builder.comment("The explosion damage of HPJ-11");
        HPJ11_EXPLOSION_DAMAGE = builder.defineInRange("hpj_11_explosion_damage", 7d, 1, 10000000);

        builder.comment("The explosion radius of HPJ-11");
        HPJ11_EXPLOSION_RADIUS = builder.defineInRange("hpj_11_explosion_radius", 4d, 1, 50);

        builder.comment("The energy cost of HPJ-11 per shoot");
        HPJ11_SHOOT_COST = builder.defineInRange("hpj_11_shoot_cost", 64, 0, 2147483647);

        builder.comment("The energy cost of HPJ-11 find a new target");
        HPJ11_SEEK_COST = builder.defineInRange("hpj_11_seek_cost", 1024, 0, 2147483647);

        builder.pop();

        builder.push("A-10");

        builder.comment("The health of A-10");
        A_10_HP = builder.defineInRange("A_10_hp", 350, 1, 10000000);

        builder.comment("The max energy cost of A-10 per tick");
        A_10_MAX_ENERGY_COST = builder.defineInRange("A_10_max_energy_cost", 256, 0, 2147483647);

        builder.comment("The max energy storage of A-10");
        A_10_MAX_ENERGY = builder.defineInRange("A_10_max_energy", 10000000, 0, 2147483647);

        builder.comment("The cannon damage of A-10");
        A_10_CANNON_DAMAGE = builder.defineInRange("A_10_cannon_damage", 30, 1, 10000000);

        builder.comment("The cannon explosion damage of A-10");
        A_10_CANNON_EXPLOSION_DAMAGE = builder.defineInRange("A_10_cannon_explosion_damage", 10, 1, 10000000);

        builder.comment("The cannon explosion radius of A-10");
        A_10_CANNON_EXPLOSION_RADIUS = builder.defineInRange("A_10_cannon_explosion_radius", 4d, 1, 10000000);

        builder.comment("The rocket damage of A-10");
        A_10_ROCKET_DAMAGE = builder.defineInRange("A_10_rocket_damage", 90, 1, 10000000);

        builder.comment("The rocket explosion damage of A-10");
        A_10_ROCKET_EXPLOSION_DAMAGE = builder.defineInRange("A_10_rocket_explosion_damage", 50, 1, 10000000);

        builder.comment("The rocket explosion radius of A-10");
        A_10_ROCKET_EXPLOSION_RADIUS = builder.defineInRange("A_10_rocket_explosion_radius", 6d, 1, 10000000);

        builder.pop();

        builder.pop();
    }
}
