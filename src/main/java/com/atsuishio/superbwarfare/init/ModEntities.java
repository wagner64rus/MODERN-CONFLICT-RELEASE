package com.atsuishio.superbwarfare.init;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.SpawnConfig;
import com.atsuishio.superbwarfare.entity.*;
import com.atsuishio.superbwarfare.entity.projectile.*;
import com.atsuishio.superbwarfare.entity.vehicle.*;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {

    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Mod.MODID);

    // Living Entities
    public static final RegistryObject<EntityType<TargetEntity>> TARGET = register("target",
            EntityType.Builder.<TargetEntity>of(TargetEntity::new, MobCategory.CREATURE).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(TargetEntity::new).fireImmune().sized(0.875f, 2f));
    public static final RegistryObject<EntityType<DPSGeneratorEntity>> DPS_GENERATOR = register("dps_generator",
            EntityType.Builder.<DPSGeneratorEntity>of(DPSGeneratorEntity::new, MobCategory.CREATURE).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(DPSGeneratorEntity::new).fireImmune().sized(0.875f, 2f));
    public static final RegistryObject<EntityType<SenpaiEntity>> SENPAI = register("senpai",
            EntityType.Builder.<SenpaiEntity>of(SenpaiEntity::new, MobCategory.MONSTER).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(SenpaiEntity::new)
                    .sized(0.6f, 2f));

    // Misc Entities
    public static final RegistryObject<EntityType<MortarEntity>> MORTAR = register("mortar",
            EntityType.Builder.<MortarEntity>of(MortarEntity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(MortarEntity::new).fireImmune().sized(0.8f, 1.4f));
    public static final RegistryObject<EntityType<LaserEntity>> LASER = register("laser",
            EntityType.Builder.<LaserEntity>of(LaserEntity::new, MobCategory.MISC).sized(0.1f, 0.1f).fireImmune().setUpdateInterval(1));
    public static final RegistryObject<EntityType<FlareDecoyEntity>> FLARE_DECOY = register("flare_decoy",
            EntityType.Builder.<FlareDecoyEntity>of(FlareDecoyEntity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).noSave().setCustomClientFactory(FlareDecoyEntity::new).sized(1f, 1f));
    public static final RegistryObject<EntityType<SmokeDecoyEntity>> SMOKE_DECOY = register("smoke_decoy",
            EntityType.Builder.<SmokeDecoyEntity>of(SmokeDecoyEntity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).noSave().setCustomClientFactory(SmokeDecoyEntity::new).sized(3f, 3f));
    public static final RegistryObject<EntityType<ClaymoreEntity>> CLAYMORE = register("claymore",
            EntityType.Builder.<ClaymoreEntity>of(ClaymoreEntity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
    public static final RegistryObject<EntityType<C4Entity>> C_4 = register("c4",
            EntityType.Builder.<C4Entity>of(C4Entity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
    public static final RegistryObject<EntityType<WaterMaskEntity>> WATER_MASK = register("water_mask",
            EntityType.Builder.of(WaterMaskEntity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(3).sized(1f, 1f).noSave().noSummon().fireImmune());

    // Projectiles
    public static final RegistryObject<EntityType<TaserBulletEntity>> TASER_BULLET = register("taser_bullet",
            EntityType.Builder.<TaserBulletEntity>of(TaserBulletEntity::new, MobCategory.MISC).setCustomClientFactory(TaserBulletEntity::new).setTrackingRange(64).noSave()
                    .setUpdateInterval(1).sized(0.25f, 0.25f));

    // Fast Projectiles
    public static final RegistryObject<EntityType<SmallCannonShellEntity>> SMALL_CANNON_SHELL = register("small_cannon_shell",
            EntityType.Builder.<SmallCannonShellEntity>of(SmallCannonShellEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(SmallCannonShellEntity::new).noSave().sized(0.25f, 0.25f));
    public static final RegistryObject<EntityType<RpgRocketEntity>> RPG_ROCKET = register("rpg_rocket",
            EntityType.Builder.<RpgRocketEntity>of(RpgRocketEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(RpgRocketEntity::new).noSave().sized(0.5f, 0.5f));
    public static final RegistryObject<EntityType<MortarShellEntity>> MORTAR_SHELL = register("mortar_shell",
            EntityType.Builder.<MortarShellEntity>of(MortarShellEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(MortarShellEntity::new).noSave().sized(0.5f, 0.5f));
    public static final RegistryObject<EntityType<ProjectileEntity>> PROJECTILE = register("projectile",
            EntityType.Builder.<ProjectileEntity>of(ProjectileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setCustomClientFactory(ProjectileEntity::new).setTrackingRange(64).noSave().noSummon().sized(0.25f, 0.25f));
    public static final RegistryObject<EntityType<CannonShellEntity>> CANNON_SHELL = register("cannon_shell",
            EntityType.Builder.<CannonShellEntity>of(CannonShellEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(CannonShellEntity::new).noSave().sized(0.75f, 0.75f));
    public static final RegistryObject<EntityType<GunGrenadeEntity>> GUN_GRENADE = register("gun_grenade",
            EntityType.Builder.<GunGrenadeEntity>of(GunGrenadeEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(GunGrenadeEntity::new).noSave().sized(0.5f, 0.5f));
    public static final RegistryObject<EntityType<MelonBombEntity>> MELON_BOMB = register("melon_bomb",
            EntityType.Builder.<MelonBombEntity>of(MelonBombEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(MelonBombEntity::new).noSave().sized(1f, 1f));
    public static final RegistryObject<EntityType<HandGrenadeEntity>> HAND_GRENADE = register("hand_grenade",
            EntityType.Builder.<HandGrenadeEntity>of(HandGrenadeEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(HandGrenadeEntity::new).noSave().sized(0.3f, 0.3f));
    public static final RegistryObject<EntityType<RgoGrenadeEntity>> RGO_GRENADE = register("rgo_grenade",
            EntityType.Builder.<RgoGrenadeEntity>of(RgoGrenadeEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(RgoGrenadeEntity::new).noSave().sized(0.3f, 0.3f));
    public static final RegistryObject<EntityType<JavelinMissileEntity>> JAVELIN_MISSILE = register("javelin_missile",
            EntityType.Builder.<JavelinMissileEntity>of(JavelinMissileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(JavelinMissileEntity::new).noSave().sized(0.5f, 0.5f));
    public static final RegistryObject<EntityType<Agm65Entity>> AGM_65 = register("agm_65",
            EntityType.Builder.<Agm65Entity>of(Agm65Entity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(Agm65Entity::new).noSave().sized(0.75f, 0.75f));
    public static final RegistryObject<EntityType<HeliRocketEntity>> HELI_ROCKET = register("heli_rocket",
            EntityType.Builder.<HeliRocketEntity>of(HeliRocketEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(HeliRocketEntity::new).noSave().sized(0.5f, 0.5f));
    public static final RegistryObject<EntityType<WgMissileEntity>> WG_MISSILE = register("wg_missile",
            EntityType.Builder.<WgMissileEntity>of(WgMissileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(WgMissileEntity::new).noSave().fireImmune().sized(0.5f, 0.5f));
    public static final RegistryObject<EntityType<SwarmDroneEntity>> SWARM_DRONE = register("swarm_drone",
            EntityType.Builder.<SwarmDroneEntity>of(SwarmDroneEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(SwarmDroneEntity::new).noSave().fireImmune().sized(0.5f, 0.5f));
    public static final RegistryObject<EntityType<Mk82Entity>> MK_82 = register("mk_82",
            EntityType.Builder.<Mk82Entity>of(Mk82Entity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(Mk82Entity::new).noSave().sized(0.8f, 0.8f));

    // Vehicles
    public static final RegistryObject<EntityType<Mk42Entity>> MK_42 = register("mk_42",
            EntityType.Builder.<Mk42Entity>of(Mk42Entity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(Mk42Entity::new).fireImmune().sized(3.4f, 3.5f));
    public static final RegistryObject<EntityType<Hpj11Entity>> HPJ_11 = register("hpj_11",
            EntityType.Builder.<Hpj11Entity>of(Hpj11Entity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(Hpj11Entity::new).fireImmune().sized(2.8f, 2.4f));
    public static final RegistryObject<EntityType<Mle1934Entity>> MLE_1934 = register("mle_1934",
            EntityType.Builder.<Mle1934Entity>of(Mle1934Entity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(Mle1934Entity::new).fireImmune().sized(4.5f, 2.8f));
    public static final RegistryObject<EntityType<AnnihilatorEntity>> ANNIHILATOR = register("annihilator",
            EntityType.Builder.<AnnihilatorEntity>of(AnnihilatorEntity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(AnnihilatorEntity::new).fireImmune().sized(13f, 4.2f));

    public static final RegistryObject<EntityType<SpeedboatEntity>> SPEEDBOAT = register("speedboat",
            EntityType.Builder.<SpeedboatEntity>of(SpeedboatEntity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(SpeedboatEntity::new).fireImmune().sized(3.0f, 2.0f));
    public static final RegistryObject<EntityType<WheelChairEntity>> WHEEL_CHAIR = register("wheel_chair",
            EntityType.Builder.<WheelChairEntity>of(WheelChairEntity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(WheelChairEntity::new).fireImmune().sized(1.0f, 1.0f));
    public static final RegistryObject<EntityType<Ah6Entity>> AH_6 = register("ah_6",
            EntityType.Builder.<Ah6Entity>of(Ah6Entity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(Ah6Entity::new).fireImmune().sized(2.8f, 2.9f));
    public static final RegistryObject<EntityType<Lav150Entity>> LAV_150 = register("lav_150",
            EntityType.Builder.<Lav150Entity>of(Lav150Entity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(Lav150Entity::new).fireImmune().sized(2.8f, 3.1f));
    public static final RegistryObject<EntityType<Tom6Entity>> TOM_6 = register("tom_6",
            EntityType.Builder.<Tom6Entity>of(Tom6Entity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(Tom6Entity::new).fireImmune().sized(1.05f, 1.0f));
    public static final RegistryObject<EntityType<Bmp2Entity>> BMP_2 = register("bmp_2",
            EntityType.Builder.<Bmp2Entity>of(Bmp2Entity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(Bmp2Entity::new).fireImmune().sized(4f, 3f));
    public static final RegistryObject<EntityType<Bmd4Entity>> BMD_4 = register("bmd_4",
            EntityType.Builder.<Bmd4Entity>of(Bmd4Entity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(Bmd4Entity::new).fireImmune().sized(4f, 3f));
    public static final RegistryObject<EntityType<Yx100Entity>> YX_100 = register("yx_100",
            EntityType.Builder.<Yx100Entity>of(Yx100Entity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(Yx100Entity::new).fireImmune().sized(4.6f, 3.25f));

    public static final RegistryObject<EntityType<DroneEntity>> DRONE = register("drone",
            EntityType.Builder.<DroneEntity>of(DroneEntity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(DroneEntity::new).sized(0.6f, 0.2f));
    public static final RegistryObject<EntityType<LaserTowerEntity>> LASER_TOWER = register("laser_tower",
            EntityType.Builder.<LaserTowerEntity>of(LaserTowerEntity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(LaserTowerEntity::new).fireImmune().sized(0.9f, 1.65f));
    public static final RegistryObject<EntityType<PrismTankEntity>> PRISM_TANK = register("prism_tank",
            EntityType.Builder.<PrismTankEntity>of(PrismTankEntity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(PrismTankEntity::new).fireImmune().sized(5f, 2.6f));
    public static final RegistryObject<EntityType<A10Entity>> A_10A = register("a_10a",
            EntityType.Builder.<A10Entity>of(A10Entity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).setCustomClientFactory(A10Entity::new).fireImmune().sized(6f, 3.5f));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
        return REGISTRY.register(name, () -> entityTypeBuilder.build(name));
    }

    @SubscribeEvent
    public static void onRegisterSpawnPlacement(SpawnPlacementRegisterEvent event) {
        event.register(ModEntities.SENPAI.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL && SpawnConfig.SPAWN_SENPAI.get()
                        && Monster.isDarkEnoughToSpawn(world, pos, random) && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)),
                SpawnPlacementRegisterEvent.Operation.OR);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(TARGET.get(), TargetEntity.createAttributes().build());
        event.put(DPS_GENERATOR.get(), DPSGeneratorEntity.createAttributes().build());
        event.put(SENPAI.get(), SenpaiEntity.createAttributes().build());
    }
}
