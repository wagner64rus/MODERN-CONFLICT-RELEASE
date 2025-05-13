package com.atsuishio.superbwarfare.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.client.ClientSoundHandler;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.projectile.GunGrenadeEntity;
import com.atsuishio.superbwarfare.entity.projectile.MortarShellEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.*;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.*;
import com.atsuishio.superbwarfare.init.*;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.*;
import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.List;

import static com.atsuishio.superbwarfare.tools.ParticleTool.sendParticle;

public class A10Entity extends ContainerMobileVehicleEntity implements GeoEntity, WeaponVehicleEntity, AircraftEntity {

    public static final EntityDataAccessor<Integer> LOADED_ROCKET = SynchedEntityData.defineId(A10Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_BOMB = SynchedEntityData.defineId(A10Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_MISSILE = SynchedEntityData.defineId(A10Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> FIRE_TIME = SynchedEntityData.defineId(A10Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<String> TARGET_UUID = SynchedEntityData.defineId(A10Entity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private float yRotSync;
    private boolean fly;
    private int flyTime;
    public int fireIndex;
    public int reloadCoolDownBomb;
    public int reloadCoolDownMissile;
    public String lockingTargetO = "none";
    public String lockingTarget = "none";

    public float destroyRot;

    public int lockTime;
    public boolean locked;

    public A10Entity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.A_10A.get(), world);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSoundHandler.playClientSoundInstance(this));
    }

    public A10Entity(EntityType<A10Entity> type, Level world) {
        super(type, world);
        this.setMaxUpStep(1f);
    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{
                new VehicleWeapon[]{
                        new SmallCannonShellWeapon()
                                .damage(VehicleConfig.A_10_CANNON_DAMAGE.get())
                                .explosionDamage(VehicleConfig.A_10_CANNON_EXPLOSION_DAMAGE.get())
                                .explosionRadius(VehicleConfig.A_10_CANNON_EXPLOSION_RADIUS.get().floatValue())
                                .sound(ModSounds.INTO_CANNON.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/cannon_30mm.png")),
                        new HeliRocketWeapon()
                                .damage(VehicleConfig.A_10_ROCKET_DAMAGE.get())
                                .explosionDamage(VehicleConfig.A_10_ROCKET_EXPLOSION_DAMAGE.get())
                                .explosionRadius(VehicleConfig.A_10_ROCKET_EXPLOSION_RADIUS.get().floatValue())
                                .sound(ModSounds.INTO_MISSILE.get()),
                        new Mk82Weapon()
                                .sound(ModSounds.INTO_MISSILE.get()),
                        new Agm65Weapon()
                                .sound(ModSounds.INTO_MISSILE.get()),
                }
        };
    }

    @Override
    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition(17, 3, 0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LOADED_ROCKET, 0);
        this.entityData.define(LOADED_BOMB, 0);
        this.entityData.define(LOADED_MISSILE, 0);
        this.entityData.define(FIRE_TIME, 0);
        this.entityData.define(TARGET_UUID, "none");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LoadedRocket", this.entityData.get(LOADED_ROCKET));
        compound.putInt("LoadedBomb", this.entityData.get(LOADED_BOMB));
        compound.putInt("LoadedMissile", this.entityData.get(LOADED_MISSILE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(LOADED_ROCKET, compound.getInt("LoadedRocket"));
        this.entityData.set(LOADED_BOMB, compound.getInt("LoadedBomb"));
        this.entityData.set(LOADED_MISSILE, compound.getInt("LoadedMissile"));
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound(ModSounds.WHEEL_STEP.get(), (float) (getDeltaMovement().length() * 0.2), random.nextFloat() * 0.1f + 1f);
    }

    @Override
    public boolean sendFireStarParticleOnHurt() {
        return false;
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .multiply(getHealth() > 0.1f ? 0.4f : 0.05f)
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
                .multiply(15f, ModDamageTypes.VEHICLE_STRIKE)
                .custom((source, damage) -> getSourceAngle(source, 0.25f) * damage)
                .custom((source, damage) -> {
                    if (source.getDirectEntity() instanceof MortarShellEntity) {
                        return 1.25f * damage;
                    }
                    if (source.getDirectEntity() instanceof GunGrenadeEntity) {
                        return 1.5f * damage;
                    }
                    return damage;
                })
                .reduce(7);
    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == ModItems.ROCKET_70.get() && this.entityData.get(LOADED_ROCKET) < 28) {
            // 装载火箭
            this.entityData.set(LOADED_ROCKET, this.entityData.get(LOADED_ROCKET) + 1);
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            this.level().playSound(null, this, ModSounds.MISSILE_RELOAD.get(), this.getSoundSource(), 2, 1);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        if (stack.getItem() == ModItems.MEDIUM_AERIAL_BOMB.get() && this.entityData.get(LOADED_BOMB) < 3) {
            // 装载航弹
            this.entityData.set(LOADED_BOMB, this.entityData.get(LOADED_BOMB) + 1);
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            this.level().playSound(null, this, ModSounds.BOMB_RELOAD.get(), this.getSoundSource(), 2, 1);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        if (stack.getItem() == ModItems.AGM.get() && this.entityData.get(LOADED_MISSILE) < 4) {
            // 装载导弹
            this.entityData.set(LOADED_MISSILE, this.entityData.get(LOADED_MISSILE) + 1);
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            this.level().playSound(null, this, ModSounds.BOMB_RELOAD.get(), this.getSoundSource(), 2, 1);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        return super.interact(player, hand);
    }

    @Override
    public void baseTick() {
        lockingTargetO = getTargetUuid();

        super.baseTick();
        float f = (float) Mth.clamp(Math.max((onGround() ? 0.815f : 0.82f) - 0.003 * getDeltaMovement().length(), 0.5) + 0.001f * Mth.abs(90 - (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1))) / 90, 0.01, 0.99);

        boolean forward = getDeltaMovement().dot(getViewVector(1)) > 0;
        this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1).scale((forward ? 0.23 : 0.1) * getDeltaMovement().dot(getViewVector(1)))));
        this.setDeltaMovement(this.getDeltaMovement().multiply(f, f, f));

        if (this.isInWater() && this.tickCount % 4 == 0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 0.6, 0.6));
            if (lastTickSpeed > 0.4) {
                this.hurt(ModDamageTypes.causeVehicleStrikeDamage(this.level().registryAccess(), this, this.getFirstPassenger() == null ? this : this.getFirstPassenger()), (float) (20 * ((lastTickSpeed - 0.4) * (lastTickSpeed - 0.4))));
            }
        }

        if (this.level() instanceof ServerLevel) {
            if (reloadCoolDown > 0) {
                reloadCoolDown--;
            }
            if (reloadCoolDownBomb > 0) {
                reloadCoolDownBomb--;
            }
            if (reloadCoolDownMissile > 0) {
                reloadCoolDownMissile--;
            }
            handleAmmo();
        }

        if (this.getFirstPassenger() instanceof Player player && fireInputDown) {
            if (this.getWeaponIndex(0) == 0) {
                if ((this.entityData.get(AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(player)) && !cannotFire) {
                    vehicleShoot(player, 0);
                }
            } else if (this.getWeaponIndex(0) == 1) {
                if (this.entityData.get(AMMO) > 0) {
                    vehicleShoot(player, 0);
                }
            }
        }
        if (onGround()) {
            terrainCompactA10();
        } else {
            this.setZRot(this.roll * 0.99f);
        }

        if (entityData.get(FIRE_TIME) > 0) {
            entityData.set(FIRE_TIME, entityData.get(FIRE_TIME) - 1);
        }

        if (this.getWeaponIndex(0) == 3) {
            seekTarget();
        }

        lowHealthWarning();

        releaseDecoy();
        this.refreshDimensions();
    }

    @Override
    public void lowHealthWarning() {
        Matrix4f transform = this.getVehicleTransform(1);
        if (this.getHealth() <= 0.4 * this.getMaxHealth()) {
            List<Entity> entities = getPlayer(level());
            for (var e : entities) {
                if (e instanceof ServerPlayer player) {
                    if (player.level() instanceof ServerLevel serverLevel) {
                        Vector4f position = transformPosition(transform, -1.603125f, 0.875f, -5.0625f);
                        sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, position.x, position.y, position.z, 5, 0.25, 0.25, 0.25, 0, true);
                    }
                }
            }
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.getHealth() <= 0.25 * this.getMaxHealth()) {
                playLowHealthParticle(serverLevel);
            }
            if (this.getHealth() <= 0.15 * this.getMaxHealth()) {
                playLowHealthParticle(serverLevel);
            }
        }

        if (this.getHealth() <= 0.1 * this.getMaxHealth()) {
            List<Entity> entities = getPlayer(level());
            for (var e : entities) {
                if (e instanceof ServerPlayer player) {
                    if (player.level() instanceof ServerLevel serverLevel) {
                        Vector4f position = transformPosition(transform, -1.603125f, 0.875f, -5.0625f);
                        sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, position.x, position.y, position.z, 5, 0.25, 0.25, 0.25, 0, true);
                        sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, position.x, position.y, position.z, 5, 0.25, 0.25, 0.25, 0, true);
                        sendParticle(serverLevel, ParticleTypes.FLAME, position.x, position.y, position.z, 5, 0.25, 0.25, 0.25, 0, true);
                        sendParticle(serverLevel, ModParticleTypes.FIRE_STAR.get(), position.x, position.y, position.z, 5, 0.25, 0.25, 0.25, 0, true);
                        Vector4f position2 = transformPosition(transform, 1.603125f, 0.875f, -5.0625f);
                        sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, position2.x, position2.y, position2.z, 5, 0.25, 0.25, 0.25, 0, true);
                        sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, position2.x, position2.y, position2.z, 5, 0.25, 0.25, 0.25, 0, true);
                        sendParticle(serverLevel, ParticleTypes.FLAME, position2.x, position2.y, position2.z, 5, 0.25, 0.25, 0.25, 0, true);
                        sendParticle(serverLevel, ModParticleTypes.FIRE_STAR.get(), position2.x, position2.y, position2.z, 5, 0.25, 0.25, 0.25, 0, true);
                    }
                }
            }
            if (this.level() instanceof ServerLevel serverLevel) {
                ParticleTool.sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.7f * getBbHeight(), this.getZ(), 2, 0.35 * this.getBbWidth(), 0.15 * this.getBbHeight(), 0.35 * this.getBbWidth(), 0.01, true);
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY() + 0.7f * getBbHeight(), this.getZ(), 2, 0.35 * this.getBbWidth(), 0.15 * this.getBbHeight(), 0.35 * this.getBbWidth(), 0.01, true);
                ParticleTool.sendParticle(serverLevel, ParticleTypes.FLAME, this.getX(), this.getY() + 0.85f * getBbHeight(), this.getZ(), 4, 0.35 * this.getBbWidth(), 0.12 * this.getBbHeight(), 0.35 * this.getBbWidth(), 0.05, true);
                ParticleTool.sendParticle(serverLevel, ModParticleTypes.FIRE_STAR.get(), this.getX(), this.getY() + 0.85f * getBbHeight(), this.getZ(), 4, 0.1 * this.getBbWidth(), 0.05 * this.getBbHeight(), 0.1 * this.getBbWidth(), 0.4, true);
            }
            if (this.tickCount % 15 == 0) {
                this.level().playSound(null, this.getOnPos(), SoundEvents.FIRE_AMBIENT, SoundSource.PLAYERS, 1, 1);
            }
        }

        if (this.getHealth() < 0.1f * this.getMaxHealth() && tickCount % 13 == 0) {
            this.level().playSound(null, this.getOnPos(), ModSounds.NO_HEALTH.get(), SoundSource.PLAYERS, 1, 1);
        } else if (this.getHealth() >= 0.1f && this.getHealth() < 0.4f * this.getMaxHealth() && tickCount % 10 == 0) {
            this.level().playSound(null, this.getOnPos(), ModSounds.LOW_HEALTH.get(), SoundSource.PLAYERS, 1, 1);
        }
    }

    public void terrainCompactA10() {
        if (onGround()) {
            Matrix4f transform = this.getWheelsTransform(1);

            // 前
            Vector4f positionF = transformPosition(transform, 0.141675f, 0, 4.6315125f);
            // 左后
            Vector4f positionLB = transformPosition(transform, 2.5752f, 0, -0.7516125f);
            // 右后
            Vector4f positionRB = transformPosition(transform, -2.5752f, 0, -0.7516125f);

            Vec3 p1 = new Vec3(positionF.x, positionF.y, positionF.z);
            Vec3 p2 = new Vec3(positionLB.x, positionLB.y, positionLB.z);
            Vec3 p3 = new Vec3(positionRB.x, positionRB.y, positionRB.z);

            // 确定点位是否在墙里来调整点位高度
            float p1y = (float) this.traceBlockY(p1, 3);
            float p2y = (float) this.traceBlockY(p2, 3);
            float p3y = (float) this.traceBlockY(p3, 3);

            p1 = new Vec3(positionF.x, p1y, positionF.z);
            p2 = new Vec3(positionLB.x, p2y, positionLB.z);
            p3 = new Vec3(positionRB.x, p3y, positionRB.z);
            Vec3 p4 = p2.add(p3).scale(0.5);

//            // 测试用粒子效果，用于确定点位位置
//
//            List<Entity> entities = getPlayer(level());
//            for (var e : entities) {
//                if (e instanceof ServerPlayer player) {
//                    if (player.level() instanceof ServerLevel serverLevel) {
//                        sendParticle(serverLevel, ParticleTypes.END_ROD, p1.x, p1.y, p1.z, 1, 0, 0, 0, 0, true);
//                        sendParticle(serverLevel, ParticleTypes.END_ROD, p2.x, p2.y, p2.z, 1, 0, 0, 0, 0, true);
//                        sendParticle(serverLevel, ParticleTypes.END_ROD, p3.x, p3.y, p3.z, 1, 0, 0, 0, 0, true);
//                        sendParticle(serverLevel, ParticleTypes.END_ROD, p4.x, p4.y, p4.z, 1, 0, 0, 0, 0, true);
//                    }
//                }
//            }

            // 通过点位位置获取角度

            // 左后-右后
            Vec3 v1 = p2.vectorTo(p3);
            // 后-前
            Vec3 v2 = p4.vectorTo(p1);

            double x = getXRotFromVector(v2);
            double z = getXRotFromVector(v1);

            float diffX = Math.clamp(-5f, 5f, Mth.wrapDegrees((float) (-2 * x) - getXRot()));
            setXRot(Mth.clamp(getXRot() + 0.05f * diffX, -45f, 45f));

            float diffZ = Math.clamp(-5f, 5f, Mth.wrapDegrees((float) (-2 * z) - getRoll()));
            setZRot(Mth.clamp(getRoll() + 0.05f * diffZ, -45f, 45f));
        } else if (isInWater()) {
            setXRot(getXRot() * 0.9f);
            setZRot(getRoll() * 0.9f);
        }
    }

    private void handleAmmo() {
        boolean hasCreativeAmmoBox = this.getFirstPassenger() instanceof Player player && InventoryTool.hasCreativeAmmoBox(player);

        int ammoCount = countItem(ModItems.SMALL_SHELL.get());

        if ((hasItem(ModItems.ROCKET_70.get()) || hasCreativeAmmoBox) && reloadCoolDown == 0 && this.getEntityData().get(LOADED_ROCKET) < 28) {
            this.entityData.set(LOADED_ROCKET, this.getEntityData().get(LOADED_ROCKET) + 1);
            reloadCoolDown = 15;
            if (!hasCreativeAmmoBox) {
                this.getItemStacks().stream().filter(stack -> stack.is(ModItems.ROCKET_70.get())).findFirst().ifPresent(stack -> stack.shrink(1));
            }
            this.level().playSound(null, this, ModSounds.MISSILE_RELOAD.get(), this.getSoundSource(), 2, 1);
        }

        if ((hasItem(ModItems.MEDIUM_AERIAL_BOMB.get()) || hasCreativeAmmoBox) && reloadCoolDownBomb == 0 && this.getEntityData().get(LOADED_BOMB) < 3) {
            this.entityData.set(LOADED_BOMB, this.getEntityData().get(LOADED_BOMB) + 1);
            reloadCoolDownBomb = 300;
            if (!hasCreativeAmmoBox) {
                this.getItemStacks().stream().filter(stack -> stack.is(ModItems.MEDIUM_AERIAL_BOMB.get())).findFirst().ifPresent(stack -> stack.shrink(1));
            }
            this.level().playSound(null, this, ModSounds.BOMB_RELOAD.get(), this.getSoundSource(), 2, 1);
        }

        if ((hasItem(ModItems.AGM.get()) || hasCreativeAmmoBox) && reloadCoolDownMissile == 0 && this.getEntityData().get(LOADED_MISSILE) < 4) {
            this.entityData.set(LOADED_MISSILE, this.getEntityData().get(LOADED_MISSILE) + 1);
            reloadCoolDownMissile = 400;
            if (!hasCreativeAmmoBox) {
                this.getItemStacks().stream().filter(stack -> stack.is(ModItems.AGM.get())).findFirst().ifPresent(stack -> stack.shrink(1));
            }
            this.level().playSound(null, this, ModSounds.BOMB_RELOAD.get(), this.getSoundSource(), 2, 1);
        }

        if (this.getWeaponIndex(0) == 0) {
            this.entityData.set(AMMO, ammoCount);
        } else if (this.getWeaponIndex(0) == 1) {
            this.entityData.set(AMMO, this.getEntityData().get(LOADED_ROCKET));
        } else if (this.getWeaponIndex(0) == 2) {
            this.entityData.set(AMMO, this.getEntityData().get(LOADED_BOMB));
        } else if (this.getWeaponIndex(0) == 3) {
            this.entityData.set(AMMO, this.getEntityData().get(LOADED_MISSILE));
        }
    }

    public void seekTarget() {
        if (!(this.getFirstPassenger() instanceof Player player)) return;

        if (getTargetUuid().equals(lockingTargetO) && !getTargetUuid().equals("none")) {
            lockTime++;
        } else {
            resetSeek(player);
        }

        Entity entity = SeekTool.seekCustomSizeEntity(this, this.level(), 384, 20, 0.9, true);
        if (entity != null) {
            if (lockTime == 0) {
                setTargetUuid(String.valueOf(entity.getUUID()));
            }
            if (!String.valueOf(entity.getUUID()).equals(getTargetUuid())) {
                resetSeek(player);
                setTargetUuid(String.valueOf(entity.getUUID()));
            }
        } else {
            setTargetUuid("none");
        }

        if (lockTime == 1) {
            if (player instanceof ServerPlayer serverPlayer) {
                SoundTool.playLocalSound(serverPlayer, ModSounds.JET_LOCK.get(), 2, 1);
            }
        }

        if (lockTime > 20) {
            if (player instanceof ServerPlayer serverPlayer) {
                SoundTool.playLocalSound(serverPlayer, ModSounds.JET_LOCKON.get(), 2, 1);
            }
            locked = true;
        }
    }

    public void resetSeek(Player player) {
        lockTime = 0;
        locked = false;
        if (player instanceof ServerPlayer serverPlayer) {
            var clientboundstopsoundpacket = new ClientboundStopSoundPacket(new ResourceLocation(Mod.MODID, "jet_lock"), SoundSource.PLAYERS);
            serverPlayer.connection.send(clientboundstopsoundpacket);
        }
    }

    public void setTargetUuid(String uuid) {
        this.lockingTarget = uuid;
    }

    public String getTargetUuid() {
        return this.lockingTarget;
    }

    @Override
    public void travel() {
        Entity passenger = this.getFirstPassenger();
        float diffX;
        float diffY;

        if (getHealth() > 0.1f * getMaxHealth()) {

            if (passenger == null || isInWater()) {
                this.leftInputDown = false;
                this.rightInputDown = false;
                this.forwardInputDown = false;
                this.backInputDown = false;
                this.entityData.set(POWER, this.entityData.get(POWER) * 0.95f);
                if (onGround()) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.94, 1, 0.94));
                } else {
                    this.setXRot(Mth.clamp(this.getXRot() + 0.1f, -89, 89));
                }
            } else if (passenger instanceof Player) {
                if (getEnergy() > 0) {
                    if (forwardInputDown) {
                        this.entityData.set(POWER, Math.min(this.entityData.get(POWER) + 0.004f, 1f));
                    }

                    if (backInputDown) {
                        this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - 0.004f, -0.6f));
                    }
                }

                if (!onGround()) {
                    if (rightInputDown) {
                        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) - 0.4f);
                    } else if (this.leftInputDown) {
                        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) + 0.4f);
                    }
                } else {
                    // 刹车
                    if (upInputDown) {
                        this.entityData.set(POWER, this.entityData.get(POWER) * 0.8f);
                        this.setDeltaMovement(this.getDeltaMovement().multiply(0.97, 1, 0.97));
                    }
                }

                diffY = Mth.clamp(Mth.wrapDegrees(passenger.getYHeadRot() - this.getYRot()), -90f, 90f);
                diffX = Mth.clamp(Mth.wrapDegrees(passenger.getXRot() - this.getXRot()), -90f, 90f);

                float roll = Mth.abs(Mth.clamp(getRoll() / 60, -1.5f, 1.5f));

                float addY = Mth.clamp(Math.max((this.onGround() ? 0.1f : 0.2f) * (float) getDeltaMovement().length(), 0f) * diffY - 0.5f * this.entityData.get(DELTA_ROT), -1.5f * (roll + 1), 1.5f * (roll + 1));
                float addX = Mth.clamp(Math.min((float) Math.max(getDeltaMovement().dot(getViewVector(1)) - 0.17, 0.01), 0.7f) * diffX, -3.5f, 3.5f);
                float addZ = this.entityData.get(DELTA_ROT) - (this.onGround() ? 0 : 0.01f) * diffY * (float) getDeltaMovement().dot(getViewVector(1));

                float i = getXRot() / 90;
                yRotSync = addY * (1 - Mth.abs(i)) + addZ * i;

                this.setYRot(this.getYRot() + yRotSync);
                if (!onGround()) {
                    this.setXRot(Mth.clamp(this.getXRot() + addX, -80, 80));
                    this.setZRot(this.getRoll() - addZ * (1 - Mth.abs(i)));
                }

                setFlap1LRot(Mth.clamp(-Mth.clamp(diffX, -22.5f, 22.5f) - 8 * addZ * (1 - Mth.abs(i)), -22.5f, 22.5f));
                setFlap1RRot(Mth.clamp(-Mth.clamp(diffX, -22.5f, 22.5f) + 8 * addZ * (1 - Mth.abs(i)), -22.5f, 22.5f));

                setFlap2LRot(Mth.clamp(Mth.clamp(diffX, -22.5f, 22.5f) - 8 * addZ * (1 - Mth.abs(i)), -22.5f, 22.5f));
                setFlap2RRot(Mth.clamp(Mth.clamp(diffX, -22.5f, 22.5f) + 8 * addZ * (1 - Mth.abs(i)), -22.5f, 22.5f));

                setFlap3Rot(diffY * 0.7f);

                this.setPropellerRot(this.getPropellerRot() + 30 * this.entityData.get(POWER));

                // 起落架
                if (!SeekTool.isOnGround(this, 15)) {
                    flyTime = Math.min(flyTime + 1, 20);
                }

                if (SeekTool.isOnGround(this, 15) && fly) {
                    flyTime = Math.max(flyTime - 1, 0);
                }

                if (!fly && flyTime == 10) {
                    fly = true;
                }

                if (fly && flyTime == 0) {
                    fly = false;
                }

                if (fly) {
                    entityData.set(GEAR_ROT, Math.min(entityData.get(GEAR_ROT) + 5, 85));
                } else {
                    entityData.set(GEAR_ROT, Math.max(entityData.get(GEAR_ROT) - 5, 0));
                }
            }
        } else if (!onGround()) {
            this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - 0.0003f, 0.02f));
            destroyRot += 0.1f;
            diffX = 90 - this.getXRot();
            this.setXRot(this.getXRot() + diffX * 0.0015f * destroyRot);
            this.setZRot(this.getRoll() - destroyRot);
            setDeltaMovement(getDeltaMovement().add(0, -0.03, 0));
            setDeltaMovement(getDeltaMovement().add(0, -destroyRot * 0.005, 0));
        }

        this.entityData.set(POWER, this.entityData.get(POWER) * 0.99f);
        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) * 0.95f);

        if (getEnergy() > 0 && !this.level().isClientSide) {
            this.consumeEnergy((int) (Mth.abs(this.entityData.get(POWER)) * VehicleConfig.A_10_MAX_ENERGY_COST.get()));
        }

        Matrix4f transform = getVehicleTransform(1);
        double flapAngle = (getFlap1LRot() + getFlap1RRot()) / 2;

        Vector4f force0 = transformPosition(transform, 0, 0, 0);
        Vector4f force1 = transformPosition(transform, 0, 1, 0);

        Vec3 force = new Vec3(force0.x, force0.y, force0.z).vectorTo(new Vec3(force1.x, force1.y, force1.z));

        setDeltaMovement(getDeltaMovement().add(force.scale(getDeltaMovement().dot(getViewVector(1)) * 0.022 * (1 + Math.sin((onGround() ? 25 : flapAngle + 25) * Mth.DEG_TO_RAD)))));

        this.setDeltaMovement(this.getDeltaMovement().add(getViewVector(1).scale(0.05 * this.entityData.get(POWER))));
    }

    @Override
    public void move(@NotNull MoverType movementType, @NotNull Vec3 movement) {
        if (!this.level().isClientSide()) {
            MobileVehicleEntity.IGNORE_ENTITY_GROUND_CHECK_STEPPING = true;
        }
        if (level() instanceof ServerLevel && canCollideBlockBeastly()) {
            collideBlockBeastly();
        }

        super.move(movementType, movement);
        if (level() instanceof ServerLevel) {
            if (this.horizontalCollision) {
                collideBlock();
                if (canCollideHardBlock()) {
                    collideHardBlock();
                }
            }

            if (lastTickSpeed < 0.3 || collisionCoolDown > 0) return;
            Entity driver = EntityFindUtil.findEntity(this.level(), this.entityData.get(LAST_DRIVER_UUID));

            if ((verticalCollision)) {
                if (entityData.get(GEAR_ROT) > 10 || (Mth.abs(getRoll()) > 20)) {
                    this.hurt(ModDamageTypes.causeVehicleStrikeDamage(this.level().registryAccess(), this, driver == null ? this : driver), (float) ((8 + Mth.abs(getRoll() * 0.2f)) * (lastTickSpeed - 0.3) * (lastTickSpeed - 0.3)));
                    if (!this.level().isClientSide) {
                        this.level().playSound(null, this, ModSounds.VEHICLE_STRIKE.get(), this.getSoundSource(), 1, 1);
                    }
                    this.bounceVertical(Direction.getNearest(this.getDeltaMovement().x(), this.getDeltaMovement().y(), this.getDeltaMovement().z()).getOpposite());
                } else {
                    if (Mth.abs((float) lastTickVerticalSpeed) > 0.4) {
                        this.hurt(ModDamageTypes.causeVehicleStrikeDamage(this.level().registryAccess(), this, driver == null ? this : driver), (float) (96 * ((Mth.abs((float) lastTickVerticalSpeed) - 0.4) * (lastTickSpeed - 0.3) * (lastTickSpeed - 0.3))));
                        if (!this.level().isClientSide) {
                            this.level().playSound(null, this, ModSounds.VEHICLE_STRIKE.get(), this.getSoundSource(), 1, 1);
                        }
                        this.bounceVertical(Direction.getNearest(this.getDeltaMovement().x(), this.getDeltaMovement().y(), this.getDeltaMovement().z()).getOpposite());
                    }
                }

            }

            if (this.horizontalCollision) {
                this.hurt(ModDamageTypes.causeVehicleStrikeDamage(this.level().registryAccess(), this, driver == null ? this : driver), (float) (126 * ((lastTickSpeed - 0.4) * (lastTickSpeed - 0.4))));
                this.bounceHorizontal(Direction.getNearest(this.getDeltaMovement().x(), this.getDeltaMovement().y(), this.getDeltaMovement().z()).getOpposite());
                if (!this.level().isClientSide) {
                    this.level().playSound(null, this, ModSounds.VEHICLE_STRIKE.get(), this.getSoundSource(), 1, 1);
                }
                collisionCoolDown = 4;
                crash = true;
                this.entityData.set(POWER, 0.8f * entityData.get(POWER));
            }
        }
    }

    @Override
    public SoundEvent getEngineSound() {
        return ModSounds.A_10_ENGINE.get();
    }

    @Override
    public float getEngineSoundVolume() {
        return entityData.get(POWER) * 1.5f;
    }

    protected void clampRotation(Entity entity) {
        float f = Mth.wrapDegrees(entity.getXRot() - this.getXRot());
        float f1 = Mth.clamp(f, -85.0F, 60F);
        entity.xRotO += f1 - f;
        entity.setXRot(entity.getXRot() + f1 - f);

        entity.setYBodyRot(this.getYRot());
        float f2 = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
        float f3 = Mth.clamp(f2, -90.0F, 90.0F);
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
        float y = 0.1f;
        float z = 3.95f;
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

    public Vec3 driverPos(float ticks) {
        Matrix4f transform = getVehicleTransform(ticks);
        Vector4f worldPosition = transformPosition(transform, 0, 1.35f, 4f);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    @Override
    public Vec3 driverZoomPos(float ticks) {
        Matrix4f transform = getVehicleTransform(ticks);
        Vector4f worldPosition = transformPosition(transform, 0, 1.35f, 4.15f);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
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
        transform.translate((float) Mth.lerp(ticks, xo, getX()), (float) Mth.lerp(ticks, yo + 2.375f, getY() + 2.375f), (float) Mth.lerp(ticks, zo, getZ()));
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
            CustomExplosion explosion = new CustomExplosion(this.level(), this,
                    ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), this, getAttacker()), 300.0f,
                    this.getX(), this.getY(), this.getZ(), 8f, ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true).setDamageMultiplier(1);
            explosion.explode();
            ForgeEventFactory.onExplosionStart(this.level(), explosion);
            explosion.finalizeExplosion(false);
            ParticleTool.spawnHugeExplosionParticles(this.level(), this.position());
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
        return VehicleConfig.A_10_HP.get();
    }

    @Override
    public int getMaxEnergy() {
        return VehicleConfig.A_10_MAX_ENERGY.get();
    }

    @Override
    public ResourceLocation getVehicleIcon() {
        return Mod.loc("textures/vehicle_icon/a10_icon.png");
    }

    @Override
    public boolean allowFreeCam() {
        return true;
    }

    @Override
    public Vec3 shootPos(float tickDelta) {
        Matrix4f transform = getVehicleTransform(tickDelta);
        Vector4f worldPosition;
        if (getWeaponIndex(0) == 0) {
            worldPosition = transformPosition(transform, 0.1321625f, -0.56446875f, 7.85210625f);
        } else if (getWeaponIndex(0) == 1) {
            worldPosition = transformPosition(transform, 0f, -1.76f, 1.87f);
        } else {
            worldPosition = transformPosition(transform, 0f, -1.203125f, 0.0625f);
        }
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    @Override
    public Vec3 shootVec(float tickDelta) {
        return new Vec3(getViewVector(tickDelta).x, getViewVector(tickDelta).y - 0.08, getViewVector(tickDelta).z);
    }

    @Override
    public void vehicleShoot(Player player, int type) {
        Matrix4f transform = getVehicleTransform(1);

        if (getWeaponIndex(0) == 0) {
            if (this.cannotFire) return;

            boolean hasCreativeAmmo = getFirstPassenger() instanceof Player pPlayer && InventoryTool.hasCreativeAmmoBox(pPlayer);

            Vector4f worldPosition = transformPosition(transform, 0.1321625f, -0.56446875f, 7.85210625f);

            if (this.entityData.get(AMMO) > 0 || hasCreativeAmmo) {
                entityData.set(FIRE_TIME, Math.min(entityData.get(FIRE_TIME) + 6, 6));

                var entityToSpawn = ((SmallCannonShellWeapon) getWeapon(0)).create(player);

                entityToSpawn.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
                entityToSpawn.shoot(getLookAngle().x, getLookAngle().y - 0.07, getLookAngle().z, 30, 0.5f);
                level().addFreshEntity(entityToSpawn);

                sendParticle((ServerLevel) this.level(), ParticleTypes.LARGE_SMOKE, worldPosition.x, worldPosition.y, worldPosition.z, 1, 0, 0, 0, 0, false);

                if (!hasCreativeAmmo) {
                    this.getItemStacks().stream().filter(stack -> stack.is(ModItems.SMALL_SHELL.get())).findFirst().ifPresent(stack -> stack.shrink(1));
                }

            }

            Level level = player.level();
            final Vec3 center = new Vec3(this.getX(), this.getEyeY(), this.getZ());

            for (Entity target : level.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(5), e -> true).stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(center))).toList()) {
                if (target instanceof ServerPlayer serverPlayer) {
                    Mod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ShakeClientMessage(6, 5, 12, this.getX(), this.getEyeY(), this.getZ()));
                }
            }

            this.entityData.set(HEAT, this.entityData.get(HEAT) + 2);
        } else if (getWeaponIndex(0) == 1 && this.getEntityData().get(LOADED_ROCKET) > 0) {
            var heliRocketEntity = ((HeliRocketWeapon) getWeapon(0)).create(player);

            Vector4f worldPosition;
            Vector4f worldPosition2;
            Vec3 shootAngle;

            if (fireIndex == 0) {
                worldPosition = transformPosition(transform, -6.63f, -0.55f, 1.83f);
                worldPosition2 = transformPosition(transform, -6.61f, -0.55f, 2.83f);
                fireIndex = 1;
            } else if (fireIndex == 1) {
                worldPosition = transformPosition(transform, -5.28f, -1.76f, 1.87f);
                worldPosition2 = transformPosition(transform, -5.27f, -1.76f, 2.87f);
                fireIndex = 2;
            } else if (fireIndex == 2) {
                worldPosition = transformPosition(transform, 5.28f, -1.76f, 1.87f);
                worldPosition2 = transformPosition(transform, 5.27f, -1.76f, 2.87f);
                fireIndex = 3;
            } else {
                worldPosition = transformPosition(transform, 6.63f, -0.55f, 1.83f);
                worldPosition2 = transformPosition(transform, 6.61f, -0.55f, 2.83f);
                fireIndex = 0;
            }

            shootAngle = new Vec3(worldPosition.x, worldPosition.y, worldPosition.z).vectorTo(new Vec3(worldPosition2.x, worldPosition2.y, worldPosition2.z)).normalize();

            heliRocketEntity.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            heliRocketEntity.shoot(shootAngle.x, shootAngle.y - 0.075, shootAngle.z, 8, 0.5f);
            player.level().addFreshEntity(heliRocketEntity);

            BlockPos pos = BlockPos.containing(new Vec3(worldPosition.x, worldPosition.y, worldPosition.z));

            this.level().playSound(null, pos, ModSounds.HELICOPTER_ROCKET_FIRE_3P.get(), SoundSource.PLAYERS, 4, 1);

            this.entityData.set(LOADED_ROCKET, this.getEntityData().get(LOADED_ROCKET) - 1);

            Level level = player.level();
            final Vec3 center = new Vec3(this.getX(), this.getEyeY(), this.getZ());

            for (Entity target : level.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(5), e -> true).stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(center))).toList()) {
                if (target instanceof ServerPlayer serverPlayer) {
                    Mod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ShakeClientMessage(6, 5, 12, this.getX(), this.getEyeY(), this.getZ()));
                }
            }

            reloadCoolDown = 15;
        } else if (getWeaponIndex(0) == 2 && this.getEntityData().get(LOADED_BOMB) > 0) {
            var Mk82Entity = ((Mk82Weapon) getWeapon(0)).create(player);

            Vector4f worldPosition;

            if (this.getEntityData().get(LOADED_BOMB) == 3) {
                worldPosition = transformPosition(transform, 0.55625f, -1.203125f, 0.0625f);
            } else if (this.getEntityData().get(LOADED_BOMB) == 2) {
                worldPosition = transformPosition(transform, 0f, -1.203125f, 0.0625f);
            } else {
                worldPosition = transformPosition(transform, -0.55625f, -1.203125f, 0.0625f);
            }

            Mk82Entity.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            Mk82Entity.shoot(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z, (float) getDeltaMovement().length(), 10);
            player.level().addFreshEntity(Mk82Entity);

            BlockPos pos = BlockPos.containing(new Vec3(worldPosition.x, worldPosition.y, worldPosition.z));

            this.level().playSound(null, pos, ModSounds.BOMB_RELEASE.get(), SoundSource.PLAYERS, 3, 1);

            if (this.getEntityData().get(LOADED_BOMB) == 3) {
                reloadCoolDownBomb = 300;
            }
            this.entityData.set(LOADED_BOMB, this.getEntityData().get(LOADED_BOMB) - 1);
        } else if (getWeaponIndex(0) == 3 && this.getEntityData().get(LOADED_MISSILE) > 0) {
            var Agm65Entity = ((Agm65Weapon) getWeapon(0)).create(player);

            Vector4f worldPosition;

            if (this.getEntityData().get(LOADED_MISSILE) == 4) {
                worldPosition = transformPosition(transform, 1.56875f, -0.943f - 0.5f, 0.1272f);
            } else if (this.getEntityData().get(LOADED_MISSILE) == 3) {
                worldPosition = transformPosition(transform, -1.56875f, -0.943f - 0.5f, 0.1272f);
            } else if (this.getEntityData().get(LOADED_MISSILE) == 2) {
                worldPosition = transformPosition(transform, 3.9321875f, -0.88680625f - 0.5f, 0.12965f);
            } else {
                worldPosition = transformPosition(transform, -3.9321875f, -0.88680625f - 0.5f, 0.12965f);
            }

            if (locked) {
                Agm65Entity.setTargetUuid(getTargetUuid());
            }
            Agm65Entity.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            Agm65Entity.shoot(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z, (float) getDeltaMovement().length() + 1, 1);
            player.level().addFreshEntity(Agm65Entity);

            BlockPos pos = BlockPos.containing(new Vec3(worldPosition.x, worldPosition.y, worldPosition.z));

            this.level().playSound(null, pos, ModSounds.BOMB_RELEASE.get(), SoundSource.PLAYERS, 3, 1);

            if (this.getEntityData().get(LOADED_MISSILE) == 3) {
                reloadCoolDownMissile = 400;
            }

            this.entityData.set(LOADED_MISSILE, this.getEntityData().get(LOADED_MISSILE) - 1);
        }
    }

    public float shootingVolume() {
        return entityData.get(FIRE_TIME) * 0.3f;
    }

    public float shootingPitch() {
        return 0.7f + entityData.get(FIRE_TIME) * 0.05f;
    }

    @Override
    public int mainGunRpm(Player player) {
        if (getWeaponIndex(0) == 2) {
            return 600;
        }
        if (getWeaponIndex(0) == 3) {
            return 120;
        }
        return 0;
    }

    @Override
    public boolean canShoot(Player player) {
        if (getWeaponIndex(0) == 2 || getWeaponIndex(0) == 3) {
            return this.entityData.get(AMMO) > 0;
        }
        return false;
    }

    @Override
    public int getAmmoCount(Player player) {
        return this.entityData.get(AMMO);
    }

    @Override
    public boolean banHand(Player player) {
        return true;
    }

    @Override
    public boolean hidePassenger(Entity entity) {
        return false;
    }

    @Override
    public int zoomFov() {
        return 3;
    }

    @Override
    public int getWeaponHeat(Player player) {
        return entityData.get(HEAT);
    }

    @Override
    public float getRotX(float tickDelta) {
        return this.getPitch(tickDelta);
    }

    @Override
    public float getRotY(float tickDelta) {
        return this.getYaw(tickDelta);
    }

    @Override
    public float getRotZ(float tickDelta) {
        return this.getRoll(tickDelta);
    }

    @Override
    public float getPower() {
        return this.entityData.get(POWER);
    }

    @Override
    public int getDecoy() {
        return this.entityData.get(DECOY_COUNT);
    }
}
