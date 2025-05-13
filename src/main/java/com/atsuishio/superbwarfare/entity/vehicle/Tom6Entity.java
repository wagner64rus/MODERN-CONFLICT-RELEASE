package com.atsuishio.superbwarfare.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.client.ClientSoundHandler;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.projectile.MelonBombEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.MobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModEntities;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Tom6Entity extends MobileVehicleEntity implements GeoEntity {

    public static final EntityDataAccessor<Boolean> MELON = SynchedEntityData.defineId(Tom6Entity.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private float yRotSync;

    public Tom6Entity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.TOM_6.get(), world);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSoundHandler.playClientSoundInstance(this));
    }

    public Tom6Entity(EntityType<Tom6Entity> type, Level world) {
        super(type, world);
        this.setMaxUpStep(0.5f);
    }

    @Override
    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition(4, 1, 0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MELON, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Melon", this.entityData.get(MELON));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(MELON, compound.getBoolean("Melon"));
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound(ModSounds.WHEEL_STEP.get(), (float) (getDeltaMovement().length() * 0.3), random.nextFloat() * 0.1f + 1f);
    }


    @Override
    public boolean sendFireStarParticleOnHurt() {
        return false;
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .multiply(2, ModDamageTypes.VEHICLE_STRIKE);
    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        if (player.getMainHandItem().is(Items.MELON) && !entityData.get(MELON)) {
            entityData.set(MELON, true);
            player.getMainHandItem().shrink(1);
            player.level().playSound(player, this.getOnPos(), SoundEvents.WOOD_PLACE, SoundSource.PLAYERS, 1, 1);
            return InteractionResult.SUCCESS;
        }
        return super.interact(player, hand);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        float f;

        f = (float) Mth.clamp(0.69f + 0.101f * Mth.abs(90 - (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1))) / 90, 0.01, 0.99);

        boolean forward = Mth.abs((float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1))) < 90;

        this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1).scale((forward ? 0.24 : -0.24) * this.getDeltaMovement().length())));
        this.setDeltaMovement(this.getDeltaMovement().multiply(f, f, f));

        if (this.isInWater() && this.tickCount % 4 == 0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 0.6, 0.6));
            if (lastTickSpeed > 0.4) {
                this.hurt(ModDamageTypes.causeVehicleStrikeDamage(this.level().registryAccess(), this, this.getFirstPassenger() == null ? this : this.getFirstPassenger()), (float) (20 * ((lastTickSpeed - 0.4) * (lastTickSpeed - 0.4))));
            }
        }

        this.terrainCompact(1f, 1.2f);
        this.refreshDimensions();
    }

    @Override
    public void travel() {
        Entity passenger = this.getFirstPassenger();

        float diffX;
        float diffY;

        if (passenger == null || isInWater()) {
            this.leftInputDown = false;
            this.rightInputDown = false;
            this.forwardInputDown = false;
            this.backInputDown = false;
            this.entityData.set(POWER, this.entityData.get(POWER) * 0.95f);
            if (onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.96, 1, 0.96));
            } else {
                this.setXRot(Mth.clamp(this.getXRot() + 0.1f, -89, 89));
            }
        } else if (passenger instanceof Player player) {
            if (forwardInputDown && getEnergy() > 0) {
                this.consumeEnergy(VehicleConfig.TOM_6_ENERGY_COST.get());
                this.entityData.set(POWER, Math.min(this.entityData.get(POWER) + 0.1f, 1f));
            }

            if (backInputDown || downInputDown) {
                this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - (this.entityData.get(POWER) > 0 ? 0.1f : 0.01f), onGround() ? -0.2f : 0.2f));
            }

            if (!onGround()) {
                if (rightInputDown) {
                    this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) - 0.4f);
                } else if (this.leftInputDown) {
                    this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) + 0.4f);
                }
            }

            diffY = Math.clamp(-90f, 90f, Mth.wrapDegrees(passenger.getYHeadRot() - this.getYRot()));
            diffX = Math.clamp(-60f, 60f, Mth.wrapDegrees(passenger.getXRot() - this.getXRot()));

            float roll = Mth.abs(Mth.clamp(getRoll() / 60, -1.5f, 1.5f));

            float addY = Mth.clamp(Math.min((this.onGround() ? 1.5f : 0.9f) * (float) Math.max(getDeltaMovement().length() - 0.06, 0.1), 0.9f) * diffY - 0.5f * this.entityData.get(DELTA_ROT), -3 * (roll + 1), 3 * (roll + 1));
            float addX = Mth.clamp(Math.min((float) Math.max(getDeltaMovement().length() - 0.1, 0.01), 0.9f) * diffX, -4, 4);
            float addZ = this.entityData.get(DELTA_ROT) - (this.onGround() ? 0 : 0.01f) * diffY * (float) getDeltaMovement().length();

            float i = getXRot() / 90;

            yRotSync = addY * (1 - Mth.abs(i)) + addZ * i;

            this.setYRot(this.getYRot() + yRotSync);
            this.setXRot(Mth.clamp(this.getXRot() + addX, onGround() ? -12 : -120, onGround() ? 3 : 120));
            this.setZRot(this.getRoll() - addZ * (1 - Mth.abs(i)));

            // 空格投掷西瓜炸弹
            if (upInputDown && !onGround() && entityData.get(MELON)) {
                entityData.set(MELON, false);

                Matrix4f transform = getVehicleTransform(1);
                Vector4f worldPosition;
                worldPosition = transformPosition(transform, 0, -0.2f, 0);

                MelonBombEntity melonBomb = new MelonBombEntity(player, player.level());
                melonBomb.setExplosionDamage(VehicleConfig.TOM_6_BOMB_EXPLOSION_DAMAGE.get());
                melonBomb.setExplosionRadius(VehicleConfig.TOM_6_BOMB_EXPLOSION_RADIUS.get().floatValue());
                melonBomb.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
                melonBomb.shoot(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z, (float) getDeltaMovement().length(), 0);
                passenger.level().addFreshEntity(melonBomb);

                this.level().playSound(null, getOnPos(), SoundEvents.IRON_DOOR_OPEN, SoundSource.PLAYERS, 1, 1);
                upInputDown = false;
            }
        }

        this.entityData.set(POWER, this.entityData.get(POWER) * 0.995f);
        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) * 0.95f);

        this.setDeltaMovement(this.getDeltaMovement().add(getViewVector(1).scale(0.03 * this.entityData.get(POWER))));

        setDeltaMovement(getDeltaMovement().add(0.0f, Mth.clamp(Math.sin((onGround() ? 45 : -(getXRot() - 20)) * Mth.DEG_TO_RAD) * Math.sin((90 - this.getXRot()) * Mth.DEG_TO_RAD) * getDeltaMovement().dot(getViewVector(1)) * 0.04, -0.04, 0.09), 0.0f));
    }

    @Override
    public float getEngineSoundVolume() {
        return entityData.get(POWER);
    }

    protected void clampRotation(Entity entity) {
        float f = Mth.wrapDegrees(entity.getXRot() - this.getXRot());
        float f1 = Mth.clamp(f, -85.0F, 60F);
        entity.xRotO += f1 - f;
        entity.setXRot(entity.getXRot() + f1 - f);

        entity.setYBodyRot(this.getYRot());
        float f2 = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
        float f3 = Mth.clamp(f2, -45.0F, 45.0F);
        entity.yRotO += f3 - f2;
        entity.setYRot(entity.getYRot() + f3 - f2);
        entity.setYBodyRot(this.getYRot());
    }

    @Override
    public void onPassengerTurned(Entity entity) {
        this.clampRotation(entity);
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        // From Immersive_Aircraft
        if (!this.hasPassenger(passenger)) {
            return;
        }

        Matrix4f transform = getVehicleTransform(1);

        float x = 0f;
        float y = 0.45f;
        float z = -0.4f;
        y += (float) passenger.getMyRidingOffset();

        int i = this.getSeatIndex(passenger);

        if (i == 0) {
            Vector4f worldPosition = transformPosition(transform, x, y, z);
            passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);
        }

        if (passenger != this.getFirstPassenger()) {
            passenger.setXRot(passenger.getXRot() + (getXRot() - xRotO));
        }

        copyEntityData(passenger);
    }

    public void copyEntityData(Entity entity) {
        float i = getXRot() / 90;

        float f = Mth.wrapDegrees(entity.getYRot() - getYRot());
        float g = Mth.clamp(f, -105.0f, 105.0f);
        entity.yRotO += g - f;
        entity.setYRot(entity.getYRot() + g - f + yRotSync * Mth.abs(i));
        entity.setYHeadRot(entity.getYRot());
        entity.setYBodyRot(getYRot());
    }

    @Override
    public Matrix4f getVehicleTransform(float ticks) {
        Matrix4f transform = new Matrix4f();
        transform.translate((float) Mth.lerp(ticks, xo, getX()), (float) Mth.lerp(ticks, yo + 0.5f, getY() + 0.5f), (float) Mth.lerp(ticks, zo, getZ()));
        transform.rotate(Axis.YP.rotationDegrees(-Mth.lerp(ticks, yRotO, getYRot())));
        transform.rotate(Axis.XP.rotationDegrees(Mth.lerp(ticks, xRotO, getXRot())));
        transform.rotate(Axis.ZP.rotationDegrees(Mth.lerp(ticks, prevRoll, getRoll())));
        return transform;
    }

    @Override
    public void destroy() {
        if (this.crash) {
            crashPassengers();
        } else {
            explodePassengers();
        }

        if (level() instanceof ServerLevel) {
            if (entityData.get(MELON)) {
                CustomExplosion explosion = new CustomExplosion(this.level(), this,
                        ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), this, getAttacker()), VehicleConfig.TOM_6_BOMB_EXPLOSION_DAMAGE.get(),
                        this.getX(), this.getY(), this.getZ(), VehicleConfig.TOM_6_BOMB_EXPLOSION_RADIUS.get().floatValue(), ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP).setDamageMultiplier(1);
                explosion.explode();
                ForgeEventFactory.onExplosionStart(this.level(), explosion);
                explosion.finalizeExplosion(false);
                ParticleTool.spawnHugeExplosionParticles(this.level(), this.position());
            } else {
                CustomExplosion explosion = new CustomExplosion(this.level(), this,
                        ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), this, getAttacker()), 15.0f,
                        this.getX(), this.getY(), this.getZ(), 2f, ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP).setDamageMultiplier(1);
                explosion.explode();
                ForgeEventFactory.onExplosionStart(this.level(), explosion);
                explosion.finalizeExplosion(false);
                ParticleTool.spawnMediumExplosionParticles(this.level(), this.position());
            }
        }

        super.destroy();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public float getMaxHealth() {
        return VehicleConfig.TOM_6_HP.get();
    }

    @Override
    public int getMaxEnergy() {
        return VehicleConfig.TOM_6_MAX_ENERGY.get();
    }

    @Override
    public ResourceLocation getVehicleIcon() {
        return Mod.loc("textures/vehicle_icon/tom_6_icon.png");
    }

    @Override
    public boolean allowFreeCam() {
        return true;
    }
}
