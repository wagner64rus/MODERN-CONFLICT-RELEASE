package com.atsuishio.superbwarfare.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.C4Entity;
import com.atsuishio.superbwarfare.entity.projectile.AerialBombEntity;
import com.atsuishio.superbwarfare.entity.projectile.CannonShellEntity;
import com.atsuishio.superbwarfare.entity.projectile.GunGrenadeEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.CannonEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.EnergyVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.LaserWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.init.*;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.*;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3d;
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

public class AnnihilatorEntity extends EnergyVehicleEntity implements GeoEntity, CannonEntity {

    public static final EntityDataAccessor<Integer> COOL_DOWN = SynchedEntityData.defineId(AnnihilatorEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> LASER_LEFT_LENGTH = SynchedEntityData.defineId(AnnihilatorEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> LASER_MIDDLE_LENGTH = SynchedEntityData.defineId(AnnihilatorEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> LASER_RIGHT_LENGTH = SynchedEntityData.defineId(AnnihilatorEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> PITCH = SynchedEntityData.defineId(AnnihilatorEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> YAW = SynchedEntityData.defineId(AnnihilatorEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<String> SHOOTER_UUID = SynchedEntityData.defineId(AnnihilatorEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public Vec3 barrelLookAt;

    public AnnihilatorEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.ANNIHILATOR.get(), world);
    }

    public AnnihilatorEntity(EntityType<AnnihilatorEntity> type, Level world) {
        super(type, world);
        this.noCulling = true;
    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{
                new VehicleWeapon[]{
                        new LaserWeapon()
                }
        };
    }

    @Override
    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition(16, 1.3, 0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(COOL_DOWN, 0);
        this.entityData.define(SHOOTER_UUID, "none");
        this.entityData.define(LASER_LEFT_LENGTH, 0f);
        this.entityData.define(LASER_MIDDLE_LENGTH, 0f);
        this.entityData.define(LASER_RIGHT_LENGTH, 0f);
        this.entityData.define(PITCH, 0f);
        this.entityData.define(YAW, 0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("CoolDown", this.entityData.get(COOL_DOWN));
        compound.putFloat("Pitch", this.entityData.get(PITCH));
        compound.putFloat("Yaw", this.entityData.get(YAW));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(COOL_DOWN, compound.getInt("CoolDown"));
        this.entityData.set(PITCH, compound.getFloat("Pitch"));
        this.entityData.set(YAW, compound.getFloat("Yaw"));
    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getMainHandItem();

        if (player.getMainHandItem().getItem() == ModItems.FIRING_PARAMETERS.get() && player.isCrouching()) {
            setTarget(player.getOffhandItem());
            return InteractionResult.SUCCESS;
        }
        if (player.getOffhandItem().getItem() == ModItems.FIRING_PARAMETERS.get() && player.isCrouching()) {
            setTarget(player.getOffhandItem());
            return InteractionResult.SUCCESS;
        }

        if (stack.is(ModItems.CROWBAR.get()) && !player.isCrouching()) {
            if (this.entityData.get(COOL_DOWN) == 0) {
                vehicleShoot(player, 0);
                entityData.set(SHOOTER_UUID, player.getStringUUID());
            }
            return InteractionResult.SUCCESS;
        }
        return super.interact(player, hand);
    }

    public void setTarget(ItemStack stack) {
        int targetX = stack.getOrCreateTag().getInt("TargetX");
        int targetY = stack.getOrCreateTag().getInt("TargetY");
        int targetZ = stack.getOrCreateTag().getInt("TargetZ");
        this.look(new Vec3(targetX, targetY, targetZ));
    }

    private void look(Vec3 pTarget) {
        float yRot = this.getYRot();
        if (yRot < 0) {
            yRot += 360;
        }
        yRot = yRot + 90 % 360;

        var BarrelRoot = new Vector3d(4.95, 2.25, 0);
        BarrelRoot.rotateY(-yRot * Mth.DEG_TO_RAD);

        Vec3 vec3 = new Vec3(this.getX() + BarrelRoot.x, this.getY() + BarrelRoot.y, this.getZ() + BarrelRoot.z);

        double d0 = pTarget.x - vec3.x;
        double d1 = pTarget.y - vec3.y;
        double d2 = pTarget.z - vec3.z;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        entityData.set(YAW, Mth.wrapDegrees((float) (Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F));
        entityData.set(PITCH, Mth.wrapDegrees((float) (-(Mth.atan2(d1, d3) * 57.2957763671875))));
    }


    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        if (!this.hasPassenger(passenger)) {
            return;
        }

        Matrix4f transform = getVehicleFlatTransform(1);

        float x = 0f;
        float y = 3.3f;
        float z = 1.5f;

        Vector4f worldPosition = transformPosition(transform, x, y, z);
        passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
        callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .multiply(0.1f)
                .immuneTo(DamageTypes.ARROW)
                .immuneTo(DamageTypes.TRIDENT)
                .immuneTo(DamageTypes.MOB_ATTACK)
                .immuneTo(DamageTypes.MOB_ATTACK_NO_AGGRO)
                .immuneTo(DamageTypes.MOB_PROJECTILE)
                .immuneTo(DamageTypes.PLAYER_ATTACK)
                .immuneTo(ModTags.DamageTypes.PROJECTILE)
                .immuneTo(ModDamageTypes.VEHICLE_STRIKE)
                .multiply(0.7f, DamageTypes.EXPLOSION)
                .multiply(0.2f, ModDamageTypes.CUSTOM_EXPLOSION)
                .multiply(0.2f, ModDamageTypes.PROJECTILE_BOOM)
                .multiply(0.2f, ModDamageTypes.MINE)
                .multiply(0.24f, ModDamageTypes.LUNGE_MINE)
                .multiply(0.3f, ModDamageTypes.CANNON_FIRE)
                .multiply(0.04f, ModTags.DamageTypes.PROJECTILE_ABSOLUTE)
                .custom((source, damage) -> getSourceAngle(source, 3) * damage)
                .custom((source, damage) -> {
                    if (source.getDirectEntity() instanceof C4Entity) {
                        return 10f * damage;
                    }
                    if (source.getDirectEntity() instanceof AerialBombEntity) {
                        return 8f * damage;
                    }
                    if (source.getDirectEntity() instanceof GunGrenadeEntity) {
                        return 3f * damage;
                    }
                    if (source.getDirectEntity() instanceof CannonShellEntity) {
                        return 3f * damage;
                    }
                    return damage;
                })
                .reduce(12);
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

        float delta = Math.abs(getYRot() - yRotO);
        while (getYRot() > 180F) {
            setYRot(getYRot() - 360F);
            yRotO = getYRot() - delta;
        }
        while (getYRot() <= -180F) {
            setYRot(getYRot() + 360F);
            yRotO = delta + getYRot();
        }

        float yRot = this.getYRot();
        if (yRot < 0) {
            yRot += 360;
        }
        yRot = yRot + 90 % 360;

        var BarrelRoot = new Vector3d(4.95, 2.25, 0);
        BarrelRoot.rotateY(-yRot * Mth.DEG_TO_RAD);

        // 中间炮管transform origin（？）世界坐标
        Vec3 BarrelRootPos = new Vec3(this.getX() + BarrelRoot.x, this.getY() + BarrelRoot.y, this.getZ() + BarrelRoot.z);

        var leftPos = new Vector3d(16, 0, -2.703125);
        leftPos.rotateZ(-this.getXRot() * Mth.DEG_TO_RAD);
        leftPos.rotateY(-yRot * Mth.DEG_TO_RAD);

        Vec3 BarrelLeftPos = new Vec3(BarrelRootPos.x + leftPos.x, BarrelRootPos.y + leftPos.y, BarrelRootPos.z + leftPos.z);

        var middlePos = new Vector3d(16, 0, 0);
        middlePos.rotateZ(-this.getXRot() * Mth.DEG_TO_RAD);
        middlePos.rotateY(-yRot * Mth.DEG_TO_RAD);

        Vec3 BarrelMiddlePos = new Vec3(BarrelRootPos.x + middlePos.x, BarrelRootPos.y + middlePos.y, BarrelRootPos.z + middlePos.z);

        var rightPos = new Vector3d(16, 0, 2.703125);
        rightPos.rotateZ(-this.getXRot() * Mth.DEG_TO_RAD);
        rightPos.rotateY(-yRot * Mth.DEG_TO_RAD);

        Vec3 BarrelRightPos = new Vec3(BarrelRootPos.x + rightPos.x, BarrelRootPos.y + rightPos.y, BarrelRootPos.z + rightPos.z);

        if (this.entityData.get(COOL_DOWN) > 88) {
            this.entityData.set(LASER_LEFT_LENGTH, Math.min(laserLength(BarrelLeftPos, this), laserLengthEntity(BarrelLeftPos, this)));
            this.entityData.set(LASER_MIDDLE_LENGTH, Math.min(laserLength(BarrelMiddlePos, this), laserLengthEntity(BarrelMiddlePos, this)));
            this.entityData.set(LASER_RIGHT_LENGTH, Math.min(laserLength(BarrelRightPos, this), laserLengthEntity(BarrelRightPos, this)));
        }

        if (this.entityData.get(COOL_DOWN) == 20) {
            this.level().playSound(null, this.getOnPos(), ModSounds.ANNIHILATOR_RELOAD.get(), SoundSource.PLAYERS, 1, 1);
        }
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

    private float laserLength(Vec3 pos, Entity cannon) {
        if (this.level() instanceof ServerLevel) {
            BlockHitResult result = cannon.level().clip(new ClipContext(pos, pos.add(cannon.getViewVector(1).scale(512)),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, cannon));

            Vec3 looking = Vec3.atLowerCornerOf(result.getBlockPos());
            Vec3 hitPos = result.getLocation();
            BlockPos _pos = BlockPos.containing(looking.x, looking.y, looking.z);

            float hardness = this.level().getBlockState(_pos).getBlock().defaultDestroyTime();

            if (ExplosionConfig.EXPLOSION_DESTROY.get() && hardness != -1) {
                Block.dropResources(this.level().getBlockState(_pos), this.level(), _pos, null);
                this.level().destroyBlock(_pos, true);
            }

            if (this.entityData.get(COOL_DOWN) > 98) {
                laserExplosion(hitPos);
            }

            if (this.getFirstPassenger() != null) {
                this.level().explode(this.getFirstPassenger(), hitPos.x, hitPos.y, hitPos.z, 5, ExplosionConfig.EXPLOSION_DESTROY.get() ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE);
            } else {
                Entity shooter = EntityFindUtil.findEntity(this.level(), this.entityData.get(SHOOTER_UUID));
                this.level().explode(shooter, hitPos.x, hitPos.y, hitPos.z, 5, ExplosionConfig.EXPLOSION_DESTROY.get() ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE);
            }
        }

        return (float) pos.distanceTo((Vec3.atLowerCornerOf(cannon.level().clip(
                new ClipContext(pos, pos.add(cannon.getViewVector(1).scale(512)),
                        ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, cannon)).getBlockPos())));
    }

    private float laserLengthEntity(Vec3 pos, Entity cannon) {
        if (this.level() instanceof ServerLevel) {
            double distance = 512 * 512;
            HitResult hitResult = cannon.pick(512, 1.0f, false);
            if (hitResult.getType() != HitResult.Type.MISS) {
                distance = hitResult.getLocation().distanceToSqr(pos);
                double blockReach = 5;
                if (distance > blockReach * blockReach) {
                    Vec3 posB = hitResult.getLocation();
                    hitResult = BlockHitResult.miss(posB, Direction.getNearest(pos.x, pos.y, pos.z), BlockPos.containing(posB));
                }
            }
            Vec3 viewVec = cannon.getViewVector(1.0F);
            Vec3 toVec = pos.add(viewVec.x * 512, viewVec.y * 512, viewVec.z * 512);
            AABB aabb = cannon.getBoundingBox().expandTowards(viewVec.scale(512)).inflate(1.0D, 1.0D, 1.0D);
            EntityHitResult entityhitresult = ProjectileUtil.getEntityHitResult(cannon, pos, toVec, aabb, p -> !p.isSpectator(), distance);
            if (entityhitresult != null) {
                Vec3 targetPos = entityhitresult.getLocation();
                double distanceToTarget = pos.distanceToSqr(targetPos);
                if (distanceToTarget > distance || distanceToTarget > 512 * 512) {
                    hitResult = BlockHitResult.miss(targetPos, Direction.getNearest(viewVec.x, viewVec.y, viewVec.z), BlockPos.containing(targetPos));
                } else if (distanceToTarget < distance) {
                    hitResult = entityhitresult;
                }
                if (hitResult.getType() == HitResult.Type.ENTITY) {
                    Entity passenger = this.getFirstPassenger();
                    Entity target = ((EntityHitResult) hitResult).getEntity();

                    if (passenger != null) {
                        target.hurt(ModDamageTypes.causeLaserDamage(this.level().registryAccess(), this, passenger), (float) 200);
                    } else {
                        Entity shooter = EntityFindUtil.findEntity(this.level(), this.entityData.get(SHOOTER_UUID));
                        target.hurt(ModDamageTypes.causeLaserDamage(this.level().registryAccess(), this, shooter), (float) 200);

                    }

                    target.invulnerableTime = 0;
                    if (this.entityData.get(COOL_DOWN) > 98) {
                        laserExplosion(targetPos);
                    }
                    return (float) pos.distanceTo(target.position());
                }
            }
        }
        return 512;
    }

    private void laserExplosion(Vec3 pos) {
        Entity passenger = this.getFirstPassenger();

        if (passenger != null) {
            CustomExplosion explosion = new CustomExplosion(this.level(), passenger,
                    ModDamageTypes.causeProjectileBoomDamage(this.level().registryAccess(), this, passenger), 300f,
                    pos.x, pos.y, pos.z, 15f, ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true).setDamageMultiplier(1);
            explosion.explode();
            ForgeEventFactory.onExplosionStart(this.level(), explosion);
            explosion.finalizeExplosion(false);
            ParticleTool.spawnHugeExplosionParticles(this.level(), pos);
        } else {
            Entity shooter = EntityFindUtil.findEntity(this.level(), this.entityData.get(SHOOTER_UUID));
            CustomExplosion explosion = new CustomExplosion(this.level(), shooter,
                    ModDamageTypes.causeProjectileBoomDamage(this.level().registryAccess(), this, shooter), 300f,
                    pos.x, pos.y, pos.z, 15f, ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true).setDamageMultiplier(1);
            explosion.explode();
            ForgeEventFactory.onExplosionStart(this.level(), explosion);
            explosion.finalizeExplosion(false);
            ParticleTool.spawnHugeExplosionParticles(this.level(), pos);
        }
    }

    @Override
    public void destroy() {
        if (level() instanceof ServerLevel) {
            CustomExplosion explosion = new CustomExplosion(this.level(), this,
                    ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), getAttacker(), getAttacker()), 600f,
                    this.getX(), this.getY(), this.getZ(), 15f, ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true).setDamageMultiplier(1);
            explosion.explode();
            ForgeEventFactory.onExplosionStart(this.level(), explosion);
            explosion.finalizeExplosion(false);
            ParticleTool.spawnHugeExplosionParticles(this.level(), this.position());
        }

        explodePassengers();
        super.destroy();
    }

    @Override
    public void vehicleShoot(Player player, int type) {
        if (this.entityData.get(COOL_DOWN) > 0) {
            return;
        }

        if (!this.canConsume(VehicleConfig.ANNIHILATOR_SHOOT_COST.get())) {
            player.displayClientMessage(Component.translatable("tips.superbwarfare.annihilator.energy_not_enough").withStyle(ChatFormatting.RED), true);
            return;
        }

        Level level = player.level();
        if (level instanceof ServerLevel) {
            if (player instanceof ServerPlayer serverPlayer) {
                SoundTool.playLocalSound(serverPlayer, ModSounds.ANNIHILATOR_FIRE_1P.get(), 1, 1);
                serverPlayer.level().playSound(null, serverPlayer.getOnPos(), ModSounds.ANNIHILATOR_FIRE_3P.get(), SoundSource.PLAYERS, 6, 1);
                serverPlayer.level().playSound(null, serverPlayer.getOnPos(), ModSounds.ANNIHILATOR_FAR.get(), SoundSource.PLAYERS, 16, 1);
                serverPlayer.level().playSound(null, serverPlayer.getOnPos(), ModSounds.ANNIHILATOR_VERYFAR.get(), SoundSource.PLAYERS, 32, 1);
            }

            this.entityData.set(COOL_DOWN, 100);
            this.consumeEnergy(VehicleConfig.ANNIHILATOR_SHOOT_COST.get());
            final Vec3 center = new Vec3(this.getX(), this.getEyeY(), this.getZ());
            for (Entity target : level.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(20), e -> true).stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(center))).toList()) {
                if (target instanceof ServerPlayer serverPlayer) {
                    Mod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ShakeClientMessage(15, 15, 25, this.getX(), this.getEyeY(), this.getZ()));
                }
            }
        }
    }

    @Override
    public void travel() {
        Entity passenger = this.getFirstPassenger();
        if (this.getEnergy() <= 0) return;

        if (passenger instanceof LivingEntity entity) {
            float yRot = this.getYRot();
            if (yRot < 0) {
                yRot += 360;
            }
            yRot = yRot + 90 % 360;

            var BarrelRoot = new Vector3d(4.95, 2.25, 0);
            BarrelRoot.rotateY(-yRot * Mth.DEG_TO_RAD);

            Vec3 barrelRootPos = new Vec3(this.getX() + BarrelRoot.x, this.getY() + BarrelRoot.y, this.getZ() + BarrelRoot.z);

            Vec3 passengersEyePos = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());

            Entity lookingAt = TraceTool.findLookingEntity(entity, 512);

            if (lookingAt == null) {
                HitResult result = entity.level().clip(new ClipContext(passengersEyePos, passengersEyePos.add(entity.getViewVector(1).scale(512)),
                        ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity));
                Vec3 blockHitPos = result.getLocation();
                barrelLookAt = new Vec3(blockHitPos.x - barrelRootPos.x, blockHitPos.y - barrelRootPos.y, blockHitPos.z - barrelRootPos.z);
            } else {
                barrelLookAt = new Vec3(lookingAt.getX() - barrelRootPos.x, lookingAt.getEyeY() - barrelRootPos.y, lookingAt.getZ() - barrelRootPos.z);
            }

            float offset = (float) VectorTool.calculateAngle(entity.getViewVector(1), barrelLookAt);

            entityData.set(YAW, passenger.getYHeadRot());
            entityData.set(PITCH, Mth.clamp(passenger.getXRot() - offset, -45f, 5f));
        }

        float diffY = Mth.wrapDegrees(entityData.get(YAW) - this.getYRot());
        float diffX = Mth.wrapDegrees(entityData.get(PITCH) - this.getXRot());

        turretTurnSound(diffX, diffY, 0.8f);

        this.setYRot(this.getYRot() + Mth.clamp(0.5f * diffY, -0.6f, 0.6f));
        this.setXRot(Mth.clamp(this.getXRot() + Mth.clamp(diffX, -0.8f, 0.8f), -45, 5f));
    }

    public void autoAim() {
        if (this.getEnergy() <= 0) return;

        Entity target = SeekTool.seekLivingEntity(this, this.level(), 64, 30);

        if (target == null) return;

        float yRot = this.getYRot();
        if (yRot < 0) {
            yRot += 360;
        }
        yRot = yRot + 90 % 360;

        var BarrelRoot = new Vector3d(4.95, 2.25, 0);
        BarrelRoot.rotateY(-yRot * Mth.DEG_TO_RAD);

        Vec3 barrelRootPos = new Vec3(this.getX() + BarrelRoot.x, this.getY() + BarrelRoot.y, this.getZ() + BarrelRoot.z);
        Vec3 targetVec = new Vec3(target.getX() - barrelRootPos.x, target.getEyeY() - barrelRootPos.y, target.getZ() - barrelRootPos.z).normalize();

        double d0 = targetVec.x;
        double d1 = targetVec.y;
        double d2 = targetVec.z;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        this.setXRot(Mth.wrapDegrees((float) (-(Mth.atan2(d1, d3) * 57.2957763671875))));
        float targetY = Mth.wrapDegrees((float) (Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F);

        float diffY = Math.clamp(-90f, 90f, Mth.wrapDegrees(targetY - this.getYRot()));

        this.setYRot(this.getYRot() + Mth.clamp(0.5f * diffY, -1f, 1f));
        this.setRot(this.getYRot(), this.getXRot());
    }

    protected void clampRotation(Entity entity) {
        float f = Mth.wrapDegrees(entity.getXRot());
        float f1 = Mth.clamp(f, -45.0F, 5);
        entity.xRotO += f1 - f;
        entity.setXRot(entity.getXRot() + f1 - f);
    }

    @Override
    public void onPassengerTurned(Entity entity) {
        this.clampRotation(entity);
    }

    private PlayState movementPredicate(AnimationState<AnnihilatorEntity> event) {
        if (this.entityData.get(COOL_DOWN) > 85) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.annihilator.fire"));
        }
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.annihilator.idle"));
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
    public int getMaxEnergy() {
        return VehicleConfig.ANNIHILATOR_MAX_ENERGY.get();
    }

    @Override
    public float getMaxHealth() {
        return VehicleConfig.ANNIHILATOR_HP.get();
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
        return (int) (this.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0) * 100f / (float) this.getMaxEnergy());
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
    public ResourceLocation getVehicleIcon() {
        return Mod.loc("textures/vehicle_icon/annihilator_icon.png");
    }
}
