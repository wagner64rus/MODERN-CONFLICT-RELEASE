package com.atsuishio.superbwarfare.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.client.ClientSoundHandler;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.MortarEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.MobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModEntities;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
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

import java.util.List;

public class WheelChairEntity extends MobileVehicleEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int jumpCoolDown;
    public int handBusyTime;

    public WheelChairEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.WHEEL_CHAIR.get(), world);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSoundHandler.playClientSoundInstance(this));
    }

    public WheelChairEntity(EntityType<WheelChairEntity> type, Level world) {
        super(type, world);
        this.setMaxUpStep(1.1f);
    }

    @Override
    public void playerTouch(Player pPlayer) {
        if (this.position().distanceTo(pPlayer.position()) > 1.4 || pPlayer == this.getFirstPassenger()) return;
        if (!this.level().isClientSide) {
            double entitySize = pPlayer.getBbWidth() * pPlayer.getBbHeight();
            double thisSize = this.getBbWidth() * this.getBbHeight();
            double f = Math.min(entitySize / thisSize, 2);
            this.setDeltaMovement(this.getDeltaMovement().add(new Vec3(pPlayer.position().vectorTo(this.position()).toVector3f()).scale(0.5 * f * pPlayer.getDeltaMovement().length())));
            this.setYRot(pPlayer.getYHeadRot());
        }
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .multiply(2, ModDamageTypes.VEHICLE_STRIKE);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound(ModSounds.WHEEL_STEP.get(), (float) (getDeltaMovement().length() * 0.3), random.nextFloat() * 0.15f + 1);
    }


    @Override
    public boolean sendFireStarParticleOnHurt() {
        return false;
    }

    @Override
    public void baseTick() {
        if (jumpCoolDown > 0 && onGround()) {
            jumpCoolDown--;
        }

        if (handBusyTime > 0) {
            handBusyTime--;
        }

        super.baseTick();
        if (this.onGround()) {
            float f = (float) Mth.clamp(0.85f + 0.05f * Mth.abs(90 - (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1))) / 90, 0.01, 0.99);
            this.setDeltaMovement(this.getDeltaMovement().multiply(f, 0.95, f));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.99, 0.95, 0.99));
        }
        this.setSprinting(this.getDeltaMovement().horizontalDistance() > 0.15);
        attractEntity();
        this.terrainCompact(0.9f, 1.2f);
        inertiaRotate(10f);

        this.refreshDimensions();
    }

    public boolean hasEnoughSpaceFor(Entity pEntity) {
        return pEntity.getBbWidth() < this.getBbWidth();
    }

    public void attractEntity() {
        List<Entity> list = this.level().getEntities(this, this.getBoundingBox().inflate(0.2F, -0.01F, 0.2F));
        if (!list.isEmpty()) {
            boolean flag = !this.level().isClientSide && !(this.getControllingPassenger() instanceof Player);

            for (Entity entity : list) {
                if (!entity.hasPassenger(this) && flag && !entity.isPassenger() && this.hasEnoughSpaceFor(entity) && (entity instanceof LivingEntity || entity instanceof MortarEntity) && !(entity instanceof WaterAnimal) && !(entity instanceof Player)) {
                    entity.startRiding(this);
                }
            }
        }
    }

    @Override
    public void travel() {
        Entity passenger = this.getFirstPassenger();

        float diffY = 0;

        if (passenger == null) {
            this.leftInputDown = false;
            this.rightInputDown = false;
            this.forwardInputDown = false;
            this.backInputDown = false;
        } else if (passenger instanceof Player) {
            diffY = Math.clamp(-90f, 90f, Mth.wrapDegrees(passenger.getYHeadRot() - this.getYRot()));
            this.setYRot(this.getYRot() + Mth.clamp(0.4f * diffY, -5f, 5f));

            float direct = (90 - (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1))) / 90;
            this.setZRot((float) (this.getRoll() + direct * diffY * 0.2 * this.getDeltaMovement().length()));
        }

        if (this.forwardInputDown) {
            this.entityData.set(POWER, this.entityData.get(POWER) + 0.01f);
            if (this.getEnergy() <= 0 && passenger instanceof Player player) {
                moveWithOutPower(player, true);
            }
        }

        if (this.backInputDown) {
            this.entityData.set(POWER, this.entityData.get(POWER) - 0.01f);
            if (this.getEnergy() <= 0 && passenger instanceof Player player) {
                moveWithOutPower(player, false);
            }
        }

        if (this.upInputDown && this.onGround() && this.getEnergy() > 400 && jumpCoolDown == 0) {
            if (passenger instanceof ServerPlayer serverPlayer) {
                serverPlayer.level().playSound(null, serverPlayer.getOnPos(), ModSounds.WHEEL_CHAIR_JUMP.get(), SoundSource.PLAYERS, 1, 1);
            }
            this.consumeEnergy(VehicleConfig.WHEELCHAIR_JUMP_ENERGY_COST.get());
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.6, 0));
            jumpCoolDown = 3;
        }

        if (this.forwardInputDown || this.backInputDown) {
            this.consumeEnergy(VehicleConfig.WHEELCHAIR_MOVE_ENERGY_COST.get());
        }

        if (passenger instanceof Player player && player.level().isClientSide && this.handBusyTime > 0) {
            var localPlayer = Minecraft.getInstance().player;
            if (localPlayer != null && player.getUUID().equals(localPlayer.getUUID())) {
                localPlayer.handsBusy = true;
            }
        }

        this.entityData.set(POWER, this.entityData.get(POWER) * 0.87f);

        float angle = (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1));
        double s0;

        if (Mth.abs(angle) < 90) {
            s0 = this.getDeltaMovement().horizontalDistance();
        } else {
            s0 = -this.getDeltaMovement().horizontalDistance();
        }

        this.setLeftWheelRot((float) (this.getLeftWheelRot() - 1.25 * s0) - 0.015f * Mth.clamp(0.4f * diffY, -5f, 5f));
        this.setRightWheelRot((float) (this.getRightWheelRot() - 1.25 * s0) + 0.015f * Mth.clamp(0.4f * diffY, -5f, 5f));

        this.setDeltaMovement(this.getDeltaMovement().add(getViewVector(1).scale((this.onGround() ? 1 : 0.1) * this.entityData.get(POWER))));
    }

    public void moveWithOutPower(Player player, boolean forward) {
        this.entityData.set(POWER, this.entityData.get(POWER) + (forward ? 0.015f : -0.015f));
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.level().playSound(null, serverPlayer.getOnPos(), SoundEvents.BOAT_PADDLE_LAND, SoundSource.PLAYERS, 1, 1);
        }
        player.causeFoodExhaustion(0.03F);

        this.handBusyTime = 4;
        this.forwardInputDown = false;
        this.backInputDown = false;
    }

    @Override
    public SoundEvent getEngineSound() {
        return ModSounds.WHEEL_CHAIR_ENGINE.get();
    }

    @Override
    public float getEngineSoundVolume() {
        return entityData.get(POWER);
    }

    protected void clampRotation(Entity entity) {
        entity.setYBodyRot(this.getYRot());
        float f2 = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
        float f3 = Mth.clamp(f2, -90F, 90.0F);
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
        float y = 0.3f;
        float z = 0f;
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
    }

    @Override
    public Matrix4f getVehicleTransform(float ticks) {
        Matrix4f transform = new Matrix4f();
        transform.translate((float) Mth.lerp(ticks, xo, getX()), (float) Mth.lerp(ticks, yo + 0.4f, getY() + 0.4f), (float) Mth.lerp(ticks, zo, getZ()));
        transform.rotate(Axis.YP.rotationDegrees(-Mth.lerp(ticks, yRotO, getYRot())));
        transform.rotate(Axis.XP.rotationDegrees(Mth.lerp(ticks, xRotO, getXRot())));
        transform.rotate(Axis.ZP.rotationDegrees(Mth.lerp(ticks, prevRoll, getRoll())));
        return transform;
    }

    @Override
    public void destroy() {
        if (level() instanceof ServerLevel) {
            CustomExplosion explosion = new CustomExplosion(this.level(), this,
                    ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), getAttacker(), getAttacker()), 10f,
                    this.getX(), this.getY(), this.getZ(), 2f, ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP).setDamageMultiplier(1);
            explosion.explode();
            net.minecraftforge.event.ForgeEventFactory.onExplosionStart(this.level(), explosion);
            explosion.finalizeExplosion(false);
            ParticleTool.spawnSmallExplosionParticles(this.level(), this.position());
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
        return VehicleConfig.WHEELCHAIR_HP.get();
    }

    @Override
    public int getMaxEnergy() {
        return VehicleConfig.WHEELCHAIR_MAX_ENERGY.get();
    }

    @Override
    public ResourceLocation getVehicleIcon() {
        return Mod.loc("textures/vehicle_icon/wheel_chair_icon.png");
    }
}
