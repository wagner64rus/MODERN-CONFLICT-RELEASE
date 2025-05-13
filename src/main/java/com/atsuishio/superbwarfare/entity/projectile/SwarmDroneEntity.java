package com.atsuishio.superbwarfare.entity.projectile;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.client.ClientSoundHandler;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.LoudlyEntity;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModEntities;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ClientIndicatorMessage;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.EntityFindUtil;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.atsuishio.superbwarfare.tools.SeekTool;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;

public class SwarmDroneEntity extends FastThrowableProjectile implements GeoEntity, DestroyableProjectileEntity, LoudlyEntity, ExplosiveProjectile {

    public static final EntityDataAccessor<String> TARGET_UUID = SynchedEntityData.defineId(SwarmDroneEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Float> TARGET_X = SynchedEntityData.defineId(SwarmDroneEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> TARGET_Y = SynchedEntityData.defineId(SwarmDroneEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> TARGET_Z = SynchedEntityData.defineId(SwarmDroneEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> HEALTH = SynchedEntityData.defineId(SwarmDroneEntity.class, EntityDataSerializers.FLOAT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean distracted = false;

    private float explosionDamage = 80f;
    private float explosionRadius = 5f;

    private float randomFloat;
    private int guideType = 0;

    public SwarmDroneEntity(EntityType<? extends SwarmDroneEntity> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    public SwarmDroneEntity(LivingEntity entity, Level level, float explosionDamage, float explosionRadius) {
        super(ModEntities.SWARM_DRONE.get(), entity, level);
        this.explosionDamage = explosionDamage;
        this.explosionRadius = explosionRadius;
    }

    public SwarmDroneEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ModEntities.SWARM_DRONE.get(), level);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSoundHandler.playClientSoundInstance(this));
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.DRONE.get();
    }

    public void setTargetUuid(String uuid) {
        this.entityData.set(TARGET_UUID, uuid);
    }

    public void setGuideType(int guideType) {
        this.guideType = guideType;
    }

    public void setTargetVec(Vec3 targetPos) {
        this.entityData.set(TARGET_X, (float) targetPos.x);
        this.entityData.set(TARGET_Y, (float) targetPos.y);
        this.entityData.set(TARGET_Z, (float) targetPos.z);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof ThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud)
            return false;
        if (source.is(DamageTypes.FALL))
            return false;
        if (source.is(DamageTypes.CACTUS))
            return false;
        if (source.is(DamageTypes.DROWN))
            return false;
        if (source.is(DamageTypes.DRAGON_BREATH))
            return false;
        if (source.is(DamageTypes.WITHER))
            return false;
        if (source.is(DamageTypes.WITHER_SKULL))
            return false;
        if (source.getDirectEntity() instanceof SwarmDroneEntity) {
            return false;
        }
        this.entityData.set(HEALTH, this.entityData.get(HEALTH) - amount);

        return super.hurt(source, amount);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(HEALTH, 10f);
        this.entityData.define(TARGET_UUID, "none");
        this.entityData.define(TARGET_X, 0f);
        this.entityData.define(TARGET_Y, 0f);
        this.entityData.define(TARGET_Z, 0f);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Health")) {
            this.entityData.set(HEALTH, compound.getFloat("Health"));
        }
        if (compound.contains("TargetUUID")) {
            this.entityData.set(TARGET_UUID, compound.getString("TargetUUID"));
        }
        if (compound.contains("TargetX")) {
            this.entityData.set(TARGET_X, compound.getFloat("TargetX"));
        }
        if (compound.contains("TargetY")) {
            this.entityData.set(TARGET_X, compound.getFloat("TargetY"));
        }
        if (compound.contains("TargetZ")) {
            this.entityData.set(TARGET_X, compound.getFloat("TargetZ"));
        }
        if (compound.contains("ExplosionDamage")) {
            this.explosionDamage = compound.getFloat("ExplosionDamage");
        }
        if (compound.contains("Radius")) {
            this.explosionRadius = compound.getFloat("Radius");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Health", this.entityData.get(HEALTH));
        compound.putString("TargetUUID", this.entityData.get(TARGET_UUID));
        compound.putFloat("TargetX", this.entityData.get(TARGET_X));
        compound.putFloat("TargetY", this.entityData.get(TARGET_Y));
        compound.putFloat("TargetZ", this.entityData.get(TARGET_Z));
        compound.putFloat("ExplosionDamage", this.explosionDamage);
        compound.putFloat("Radius", this.explosionRadius);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (result.getEntity() instanceof SwarmDroneEntity) {
            return;
        }
        if (this.getOwner() instanceof LivingEntity living) {
            if (!living.level().isClientSide() && living instanceof ServerPlayer player) {
                living.level().playSound(null, living.blockPosition(), ModSounds.INDICATION.get(), SoundSource.VOICE, 1, 1);

                Mod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new ClientIndicatorMessage(0, 5));
            }
        }
        if (this.level() instanceof ServerLevel) {
            causeMissileExplode(ModDamageTypes.causeProjectileBoomDamage(this.level().registryAccess(), this, this.getOwner()), this.explosionDamage, this.explosionRadius);
        }
    }

    @Override
    public void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (this.level() instanceof ServerLevel) {
            causeMissileExplode(ModDamageTypes.causeProjectileBoomDamage(this.level().registryAccess(), this, this.getOwner()), this.explosionDamage, this.explosionRadius);
        }
    }

    @Override
    public void tick() {
        super.tick();
        Entity entity = EntityFindUtil.findEntity(this.level(), entityData.get(TARGET_UUID));
        List<Entity> decoy = SeekTool.seekLivingEntities(this, this.level(), 32, 90);

        for (var e : decoy) {
            if (e instanceof DecoyEntity decoyEntity && !distracted) {
                this.entityData.set(TARGET_UUID, decoyEntity.getDecoyUUID());
                distracted = true;
            }
        }

        if (this.tickCount == 1) {
            if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CLOUD, this.xo, this.yo, this.zo, 15, 0.8, 0.8, 0.8, 0.01, true);
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, this.xo, this.yo, this.zo, 10, 0.8, 0.8, 0.8, 0.01, true);
            }
        }

        if (tickCount > 10 && this.getOwner() != null) {
            Entity shooter = this.getOwner();
            Vec3 targetPos;

            if (guideType == 0 && entity!= null) {
                targetPos = entity.getEyePosition();
                this.entityData.set(TARGET_X, (float) targetPos.x);
                this.entityData.set(TARGET_Y, (float) targetPos.y);
                this.entityData.set(TARGET_Z, (float) targetPos.z);
            } else {
                targetPos = new Vec3(this.entityData.get(TARGET_X), this.entityData.get(TARGET_Y), this.entityData.get(TARGET_Z));
            }

            if (tickCount %5 == 0) {
                randomFloat = random.nextFloat();
            }

            double dis = position().distanceTo(targetPos);
            double disShooter = shooter.position().distanceTo(targetPos);
            double randomPos = Mth.sin(0.25f * (tickCount + randomFloat)) * randomFloat * Mth.clamp(Mth.sin((float) (Mth.PI * (dis / disShooter))), 0 , 0.8);

            Vec3 toVec = this.position().vectorTo(targetPos).normalize().add(new Vec3(randomPos,0.1 * randomPos,randomPos));
            setDeltaMovement(getDeltaMovement().add(toVec.scale(0.5)));
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.85, 0.85, 0.85));

            if (dis < 0.5) {
                if (this.level() instanceof ServerLevel) {
                    causeMissileExplode(ModDamageTypes.causeProjectileBoomDamage(this.level().registryAccess(), this, this.getOwner()), this.explosionDamage, this.explosionRadius);
                }
                this.discard();
            }
        }

        if (this.tickCount > 300 || this.isInWater() || this.entityData.get(HEALTH) <= 0) {
            if (this.level() instanceof ServerLevel) {
                causeMissileExplode(ModDamageTypes.causeProjectileBoomDamage(this.level().registryAccess(), this, this.getOwner()), this.explosionDamage, this.explosionRadius);
            }
            this.discard();
        }
    }

    @Override
    protected void updateRotation() {
    }

    public void causeMissileExplode(@Nullable DamageSource source, float damage, float radius) {
        CustomExplosion explosion = new CustomExplosion(level(), this, source, damage,
                this.getX(), this.getY(), this.getZ(), radius, ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true).setDamageMultiplier(1.25f);
        explosion.explode();
        net.minecraftforge.event.ForgeEventFactory.onExplosionStart(level(), explosion);
        explosion.finalizeExplosion(false);
        ParticleTool.spawnMediumExplosionParticles(level(), position());
        discard();
    }

    private PlayState movementPredicate(AnimationState<SwarmDroneEntity> event) {
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.sd.fly"));
    }

    @Override
    protected float getGravity() {
        return tickCount > 10 ? 0 : 0.1f;
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
    public boolean shouldSyncMotion() {
        return true;
    }

    @Override
    public @NotNull SoundEvent getSound() {
        return ModSounds.DRONE_SOUND.get();
    }

    @Override
    public float getVolume() {
        return 0.07f;
    }

    @Override
    public void setDamage(float damage) {
    }

    @Override
    public void setExplosionDamage(float explosionDamage) {
        this.explosionDamage = explosionDamage;
    }

    @Override
    public void setExplosionRadius(float radius) {
        this.explosionRadius = radius;
    }
}
