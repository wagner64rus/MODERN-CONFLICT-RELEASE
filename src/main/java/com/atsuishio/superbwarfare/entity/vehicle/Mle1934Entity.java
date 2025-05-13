package com.atsuishio.superbwarfare.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.projectile.GunGrenadeEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.CannonEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.CannonShellWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.init.*;
import com.atsuishio.superbwarfare.item.common.ammo.CannonShellItem;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;

public class Mle1934Entity extends VehicleEntity implements GeoEntity, CannonEntity {

    public static final EntityDataAccessor<Integer> COOL_DOWN = SynchedEntityData.defineId(Mle1934Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(Mle1934Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> PITCH = SynchedEntityData.defineId(Mle1934Entity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> YAW = SynchedEntityData.defineId(Mle1934Entity.class, EntityDataSerializers.FLOAT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final float shellGravity = 0.1f;

    public Mle1934Entity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.MLE_1934.get(), world);
    }

    public Mle1934Entity(EntityType<Mle1934Entity> type, Level world) {
        super(type, world);
    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{
                new VehicleWeapon[]{
                        new CannonShellWeapon()
                                .hitDamage(VehicleConfig.MLE1934_AP_DAMAGE.get())
                                .explosionDamage(VehicleConfig.MLE1934_AP_EXPLOSION_DAMAGE.get())
                                .explosionRadius(VehicleConfig.MLE1934_AP_EXPLOSION_RADIUS.get().floatValue())
                                .durability(70)
                                .gravity(shellGravity)
                                .sound(ModSounds.CANNON_RELOAD.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/ap_shell.png")),
                        new CannonShellWeapon()
                                .hitDamage(VehicleConfig.MLE1934_HE_DAMAGE.get())
                                .explosionDamage(VehicleConfig.MLE1934_HE_EXPLOSION_DAMAGE.get())
                                .explosionRadius(VehicleConfig.MLE1934_HE_EXPLOSION_RADIUS.get().floatValue())
                                .durability(0)
                                .fireProbability(0.24F)
                                .fireTime(5)
                                .gravity(shellGravity)
                                .sound(ModSounds.CANNON_RELOAD.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/he_shell.png")),
                }
        };
    }

    @Override
    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition(10, 1.3, 0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(COOL_DOWN, 0);
        this.entityData.define(TYPE, 0);
        this.entityData.define(PITCH, 0f);
        this.entityData.define(YAW, 0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("CoolDown", this.entityData.get(COOL_DOWN));
        compound.putInt("Type", this.entityData.get(TYPE));
        compound.putFloat("Pitch", this.entityData.get(PITCH));
        compound.putFloat("Yaw", this.entityData.get(YAW));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(COOL_DOWN, compound.getInt("CoolDown"));
        this.entityData.set(TYPE, compound.getInt("Type"));
        this.entityData.set(PITCH, compound.getFloat("Pitch"));
        this.entityData.set(YAW, compound.getFloat("Yaw"));
    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getMainHandItem();

        if (player.getMainHandItem().getItem() == ModItems.FIRING_PARAMETERS.get() && player.isCrouching()) {
            setTarget(player.getMainHandItem());
            return InteractionResult.SUCCESS;
        }
        if (player.getOffhandItem().getItem() == ModItems.FIRING_PARAMETERS.get() && player.isCrouching()) {
            setTarget(player.getOffhandItem());
            return InteractionResult.SUCCESS;
        }

        if (stack.getItem() instanceof CannonShellItem) {
            if (this.entityData.get(COOL_DOWN) == 0) {
                var weaponType = stack.is(ModItems.AP_5_INCHES.get()) ? 0 : 1;
                setWeaponIndex(0, weaponType);
                vehicleShoot(player, 0);
            }
            return InteractionResult.SUCCESS;
        }
        return super.interact(player, hand);
    }

    public void setTarget(ItemStack stack) {
        int targetX = stack.getOrCreateTag().getInt("TargetX");
        int targetY = stack.getOrCreateTag().getInt("TargetY");
        int targetZ = stack.getOrCreateTag().getInt("TargetZ");
        var isDepressed = stack.getOrCreateTag().getBoolean("IsDepressed");

        this.look(new Vec3(targetX, targetY, targetZ));
        Matrix4f transform = getVehicleFlatTransform(1);
        Vector4f worldPosition = transformPosition(transform, 0, 1.4992625f, 1.52065f);
        Vec3 shootPos = new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);

        if (!RangeTool.canReach(15, shellGravity, shootPos, new Vec3(targetX, targetY, targetZ), -2.7, 30, isDepressed))
            return;

        this.look(new Vec3(targetX, targetY, targetZ));
        entityData.set(PITCH, (float) -RangeTool.calculateAngle(15, shellGravity, shootPos, new Vec3(targetX, targetY, targetZ), isDepressed));
    }

    private void look(Vec3 pTarget) {
        Matrix4f transform = getVehicleFlatTransform(1);
        Vector4f worldPosition = transformPosition(transform, 0, 1.4992625f, 1.52065f);
        Vec3 shootPos = new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);

        double d0 = pTarget.x - shootPos.x;
        double d2 = pTarget.z - shootPos.z;
        entityData.set(YAW, Mth.wrapDegrees((float) (Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F));
    }

   

    public void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        if (!this.hasPassenger(passenger)) {
            return;
        }

        Matrix4f transform = getVehicleFlatTransform(1);

        float x = 0f;
        float y = 2.0f;
        float z = 0.5f;

        Vector4f worldPosition = transformPosition(transform, x, y, z);
        passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
        callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .multiply(0.2f)
                .multiply(1.5f, DamageTypes.ARROW)
                .multiply(1.5f, DamageTypes.TRIDENT)
                .multiply(2.5f, DamageTypes.MOB_ATTACK)
                .multiply(2f, DamageTypes.MOB_ATTACK_NO_AGGRO)
                .multiply(1.5f, DamageTypes.MOB_PROJECTILE)
                .multiply(12.5f, DamageTypes.LAVA)
                .multiply(6f, DamageTypes.EXPLOSION)
                .multiply(6f, DamageTypes.PLAYER_EXPLOSION)
                .multiply(2.4f, ModDamageTypes.CUSTOM_EXPLOSION)
                .multiply(2f, ModDamageTypes.PROJECTILE_BOOM)
                .multiply(0.75f, ModDamageTypes.MINE)
                .multiply(1.5f, ModDamageTypes.CANNON_FIRE)
                .multiply(0.25f, ModTags.DamageTypes.PROJECTILE)
                .multiply(0.85f, ModTags.DamageTypes.PROJECTILE_ABSOLUTE)
                .multiply(10f, ModDamageTypes.VEHICLE_STRIKE)
                .custom((source, damage) -> getSourceAngle(source, 1f) * damage)
                .custom((source, damage) -> {
                    if (source.getDirectEntity() instanceof GunGrenadeEntity) {
                        return 1.5f * damage;
                    }
                    return damage;
                })
                .reduce(8);
    }

    @Override
    public Vec3 getDeltaMovement() {
        return new Vec3(0, Math.min(super.getDeltaMovement().y, 0), 0);
    }

    @Override
    public void baseTick() {
        super.baseTick();

        if (this.entityData.get(COOL_DOWN) > 0) {
            this.entityData.set(COOL_DOWN, this.entityData.get(COOL_DOWN) - 1);
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        if (this.onGround()) {
            this.setDeltaMovement(Vec3.ZERO);
        } else {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }

        lowHealthWarning();
    }

    @Override
    public void handleClientSync() {
        if (isControlledByLocalInstance()) {
            interpolationSteps = 0;
            syncPacketPositionCodec(getX(), getY(), getZ());
        }
        if (interpolationSteps <= 0) {
            return;
        }

        double interpolatedYaw = Mth.wrapDegrees(serverYRot - (double) getYRot());
        setYRot(getYRot() + (float) interpolatedYaw / (float) interpolationSteps);
        setXRot(getXRot() + (float) (serverXRot - (double) getXRot()) / (float) interpolationSteps);
        setRot(getYRot(), getXRot());

    }

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        serverYRot = yaw;
        serverXRot = pitch;
        this.interpolationSteps = 10;
    }

    @Override
    public void destroy() {
        if (level() instanceof ServerLevel) {
            CustomExplosion explosion = new CustomExplosion(this.level(), this,
                    ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), getAttacker(), getAttacker()), 120f,
                    this.getX(), this.getY(), this.getZ(), 6f, ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true).setDamageMultiplier(1);
            explosion.explode();
            net.minecraftforge.event.ForgeEventFactory.onExplosionStart(this.level(), explosion);
            explosion.finalizeExplosion(false);
            ParticleTool.spawnMediumExplosionParticles(this.level(), this.position());
        }

        explodePassengers();
        super.destroy();
    }

    @Override
    public Vec3 driverZoomPos(float ticks) {
        Matrix4f transform = getVehicleFlatTransform(1);
        Vector4f worldPosition = transformPosition(transform, 0, 1.4992625f + 1.4f, 1.52065f);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    @Override
    public void vehicleShoot(Player player, int type) {
        if (this.entityData.get(COOL_DOWN) > 0) return;

        Level level = player.level();
        if (level instanceof ServerLevel server) {
            int consumed;
            if (InventoryTool.hasCreativeAmmoBox(player)) {
                consumed = 2;
            } else {
                var ammo = getWeaponIndex(0) == 0 ? ModItems.AP_5_INCHES.get() : ModItems.HE_5_INCHES.get();
                var ammoCount = InventoryTool.countItem(player.getInventory().items, ammo);

                // 尝试消耗两发弹药
                if (ammoCount <= 0) return;
                consumed = InventoryTool.consumeItem(player.getInventory().items, ammo, 2);
            }

            boolean salvoShoot = consumed == 2;

            Matrix4f transform = getVehicleFlatTransform(1);
            Vector4f worldPositionL = transformPosition(transform, 0.486775f, 1.4992625f, 1.52065f);
            Vector4f worldPositionR = transformPosition(transform, -0.486775f, 1.4992625f, 1.52065f);

            // 左炮管
            var entityToSpawnLeft = ((CannonShellWeapon) getWeapon(0)).create(player);

            entityToSpawnLeft.setPos(worldPositionL.x, worldPositionL.y, worldPositionL.z);
            entityToSpawnLeft.shoot(this.getLookAngle().x, this.getLookAngle().y, this.getLookAngle().z, 15, 0.05f);
            level.addFreshEntity(entityToSpawnLeft);

            server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    this.getX() + 5 * this.getLookAngle().x,
                    this.getY(),
                    this.getZ() + 5 * this.getLookAngle().z,
                    100, 7, 0.02, 7, 0.005);

            double x = worldPositionL.x + 9 * this.getLookAngle().x;
            double y = worldPositionL.y + 9 * this.getLookAngle().y;
            double z = worldPositionL.z + 9 * this.getLookAngle().z;

            server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 10, 0.4, 0.4, 0.4, 0.0075);
            server.sendParticles(ParticleTypes.CLOUD, x, y, z, 10, 0.4, 0.4, 0.4, 0.0075);

            int count = 6;

            for (float i = 9.5f; i < 16; i += .5f) {
                server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        worldPositionL.x + i * this.getLookAngle().x,
                        worldPositionL.y + i * this.getLookAngle().y,
                        worldPositionL.z + i * this.getLookAngle().z,
                        Mth.clamp(count--, 1, 5), 0.15, 0.15, 0.15, 0.0025);
            }


            // 右炮管
            if (salvoShoot) {
                var entityToSpawnRight = ((CannonShellWeapon) getWeapon(0)).create(player);

                entityToSpawnRight.setPos(worldPositionR.x, worldPositionR.y, worldPositionR.z);
                entityToSpawnRight.shoot(this.getLookAngle().x, this.getLookAngle().y, this.getLookAngle().z, 15, 0.05f);
                level.addFreshEntity(entityToSpawnRight);

                server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        this.getX() + 5 * this.getLookAngle().x,
                        this.getY(),
                        this.getZ() + 5 * this.getLookAngle().z,
                        100, 7, 0.02, 7, 0.005);

                double xR = worldPositionR.x + 9 * this.getLookAngle().x;
                double yR = worldPositionR.y + 9 * this.getLookAngle().y;
                double zR = worldPositionR.z + 9 * this.getLookAngle().z;

                server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, xR, yR, zR, 10, 0.4, 0.4, 0.4, 0.0075);
                server.sendParticles(ParticleTypes.CLOUD, xR, yR, zR, 10, 0.4, 0.4, 0.4, 0.0075);

                int countR = 6;

                for (float i = 9.5f; i < 16; i += .5f) {
                    server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            worldPositionR.x + i * this.getLookAngle().x,
                            worldPositionR.y + i * this.getLookAngle().y,
                            worldPositionR.z + i * this.getLookAngle().z,
                            Mth.clamp(countR--, 1, 5), 0.15, 0.15, 0.15, 0.0025);
                }

                this.entityData.set(TYPE, 1);
            } else {
                this.entityData.set(TYPE, -1);
            }

            if (player instanceof ServerPlayer serverPlayer) {
                SoundTool.playLocalSound(serverPlayer, ModSounds.MK_42_FIRE_1P.get(), 2, 1);
                Mod.queueServerWork(44, () -> SoundTool.playLocalSound(serverPlayer, ModSounds.CANNON_RELOAD.get(), 2, 1));
                serverPlayer.level().playSound(null, serverPlayer.getOnPos(), ModSounds.MK_42_FIRE_3P.get(), SoundSource.PLAYERS, 6, 1);
                serverPlayer.level().playSound(null, serverPlayer.getOnPos(), ModSounds.MK_42_FAR.get(), SoundSource.PLAYERS, 16, 1);
                serverPlayer.level().playSound(null, serverPlayer.getOnPos(), ModSounds.MK_42_VERYFAR.get(), SoundSource.PLAYERS, 32, 1);
            }

            this.entityData.set(COOL_DOWN, 74);

            server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    this.getX() + 5 * this.getLookAngle().x,
                    this.getY(),
                    this.getZ() + 5 * this.getLookAngle().z,
                    100, 7, 0.02, 7, 0.005);

            final Vec3 center = new Vec3(this.getX(), this.getEyeY(), this.getZ());

            for (Entity target : level.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(20), e -> true).stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(center))).toList()) {
                if (target instanceof ServerPlayer serverPlayer) {
                    Mod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ShakeClientMessage(15, 15, 45, this.getX(), this.getEyeY(), this.getZ()));
                }
            }
        }
    }

    @Override
    public void travel() {
        Entity passenger = this.getFirstPassenger();
        if (passenger != null) {
            entityData.set(YAW, passenger.getYHeadRot());
            entityData.set(PITCH, passenger.getXRot() - 2f);
        }

        float diffY = Mth.wrapDegrees(entityData.get(YAW) - this.getYRot());
        float diffX = Mth.wrapDegrees(entityData.get(PITCH) - this.getXRot());

        turretTurnSound(diffX, diffY, 0.95f);

        this.setYRot(this.getYRot() + Mth.clamp(0.5f * diffY, -1.25f, 1.25f));
        this.setXRot(Mth.clamp(this.getXRot() + Mth.clamp(0.5f * diffX, -2f, 2f), -30, 5f));
    }

    protected void clampRotation(Entity entity) {
        float f = Mth.wrapDegrees(entity.getXRot());
        float f1 = Mth.clamp(f, -30.0F, 7.0F);
        entity.xRotO += f1 - f;
        entity.setXRot(entity.getXRot() + f1 - f);
    }

    @Override
    public void onPassengerTurned(Entity entity) {
        this.clampRotation(entity);
    }

    private PlayState movementPredicate(AnimationState<Mle1934Entity> event) {
        if (this.entityData.get(COOL_DOWN) > 64) {
            if (this.entityData.get(TYPE) == 1) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.mle1934.salvo_fire"));
            } else {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.mle1934.fire"));
            }
        }
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.mle1934.idle"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public float getMaxHealth() {
        return VehicleConfig.MLE1934_HP.get();
    }

    @Override
    public int mainGunRpm(Player player) {
        return 0;
    }

    @Override
    public boolean canShoot(Player player) {
        return true;
    }

    @Override
    public int getAmmoCount(Player player) {
        var ammo = getWeaponIndex(0) == 0 ? ModItems.AP_5_INCHES.get() : ModItems.HE_5_INCHES.get();
        return InventoryTool.countItem(player.getInventory().items, ammo);
    }

    @Override
    public boolean banHand(Player player) {
        return true;
    }

    @Override
    public boolean hidePassenger(Entity entity) {
        return true;
    }

    @Override
    public int zoomFov() {
        return 5;
    }

    @Override
    public int getWeaponHeat(Player player) {
        return 0;
    }

    @Override
    public Vec3 getBarrelVector(float pPartialTicks) {
        if (getFirstPassenger() != null) {
            return getFirstPassenger().getViewVector(pPartialTicks);
        }
        return super.getBarrelVector(pPartialTicks);
    }

    @Override
    public ResourceLocation getVehicleIcon() {
        return Mod.loc("textures/vehicle_icon/mle1934_icon.png");
    }
}
