package com.atsuishio.superbwarfare.entity;

import com.atsuishio.superbwarfare.init.ModEntities;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.FormatTool;
import com.atsuishio.superbwarfare.tools.SoundTool;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PlayMessages;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

@Mod.EventBusSubscriber
public class TargetEntity extends LivingEntity implements GeoEntity {

    public static final EntityDataAccessor<Integer> DOWN_TIME = SynchedEntityData.defineId(TargetEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public TargetEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.TARGET.get(), world);
    }

    public TargetEntity(EntityType<TargetEntity> type, Level world) {
        super(type, world);
        this.noCulling = true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DOWN_TIME, 0);
    }

    @Override
    public MobType getMobType() {
        return super.getMobType();
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return NonNullList.withSize(1, ItemStack.EMPTY);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot pSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {
    }

    @Override
    public boolean causeFallDamage(float l, float d, DamageSource source) {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_FIRE)
                || source.getDirectEntity() instanceof ThrownPotion
                || source.getDirectEntity() instanceof AreaEffectCloud
                || source.is(DamageTypes.FALL)
                || source.is(DamageTypes.CACTUS)
                || source.is(DamageTypes.DROWN)
                || source.is(DamageTypes.LIGHTNING_BOLT)
                || source.is(DamageTypes.FALLING_ANVIL)
                || source.is(DamageTypes.DRAGON_BREATH)
                || source.is(DamageTypes.WITHER)
                || source.is(DamageTypes.WITHER_SKULL)
                || source.is(DamageTypes.MAGIC)
                || this.entityData.get(DOWN_TIME) > 0) {
            return false;
        }

        if (!this.level().isClientSide()) {
            this.level().playSound(null, BlockPos.containing(this.getX(), this.getY(), this.getZ()), ModSounds.HIT.get(), SoundSource.BLOCKS, 1, 1);
        } else {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), ModSounds.HIT.get(), SoundSource.BLOCKS, 1, 1, false);
        }
        return super.hurt(source, amount);
    }

    @SubscribeEvent
    public static void onTargetDown(LivingDeathEvent event) {
        var entity = event.getEntity();
        var sourceEntity = event.getSource().getEntity();

        if (entity == null) return;

        if (entity instanceof TargetEntity targetEntity) {
            event.setCanceled(true);
            targetEntity.setHealth(targetEntity.getMaxHealth());

            if (sourceEntity == null) return;

            if (sourceEntity instanceof Player player) {
                player.displayClientMessage(Component.translatable("tips.superbwarfare.target.down",
                        FormatTool.format1D((entity.position()).distanceTo((sourceEntity.position()))), "m"), true);
                SoundTool.playLocalSound(player, ModSounds.TARGET_DOWN.get(), 1, 1);
                targetEntity.entityData.set(DOWN_TIME, 40);
            }
        }
    }

    @Override
    public boolean isPickable() {
        return this.entityData.get(DOWN_TIME) == 0;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            if (!this.level().isClientSide()) {
                this.discard();
            }

            if (!player.getAbilities().instabuild) {
                player.addItem(new ItemStack(ModItems.TARGET_DEPLOYER.get()));
            }
        } else {
            this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((player.getX()), this.getY(), (player.getZ())));
            this.setXRot(0);
            this.xRotO = this.getXRot();
            this.entityData.set(DOWN_TIME, 0);
        }

        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.entityData.get(DOWN_TIME) > 0) {
            this.entityData.set(DOWN_TIME, this.entityData.get(DOWN_TIME) - 1);
        }
    }

    @Override
    public Vec3 getDeltaMovement() {
        return new Vec3(0, 0, 0);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    protected void doPush(Entity entityIn) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public void setNoGravity(boolean ignored) {
        super.setNoGravity(true);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0)
                .add(Attributes.MAX_HEALTH, 40)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 0)
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.KNOCKBACK_RESISTANCE, 10)
                .add(Attributes.FLYING_SPEED, 0);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= 100) {
            this.spawnAtLocation(new ItemStack(ModItems.TARGET_DEPLOYER.get()));
            this.remove(TargetEntity.RemovalReason.KILLED);
        }
    }

    private PlayState movementPredicate(AnimationState<TargetEntity> event) {
        if (this.entityData.get(DOWN_TIME) > 0) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.target.down"));
        }
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.target.idle"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}
