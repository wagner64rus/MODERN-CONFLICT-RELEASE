package com.atsuishio.superbwarfare.item.common.ammo;

import com.atsuishio.superbwarfare.advancement.CriteriaRegister;
import com.atsuishio.superbwarfare.client.renderer.item.RocketItemRenderer;
import com.atsuishio.superbwarfare.entity.projectile.RpgRocketEntity;
import com.atsuishio.superbwarfare.init.ModEntities;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.item.DispenserLaunchable;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

public class Rocket extends Item implements GeoItem, DispenserLaunchable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static ItemDisplayContext transformType;

    public Rocket() {
        super(new Item.Properties().stacksTo(16));
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new RocketItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    public void getTransformType(ItemDisplayContext type) {
        transformType = type;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = super.getAttributeModifiers(slot, stack);
        if (slot == EquipmentSlot.MAINHAND) {
            map = HashMultimap.create(map);
            map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Item modifier", 6d, AttributeModifier.Operation.ADDITION));
            map.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Item modifier", -2.4, AttributeModifier.Operation.ADDITION));
        }
        return map;
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, LivingEntity entity, @NotNull LivingEntity source) {
        if (entity.level() instanceof ServerLevel level && Math.random() < 0.25) {

            level.explode(source, source.getX(), source.getY() + 1, source.getZ(), 6, Level.ExplosionInteraction.NONE);
            level.explode(null, source.getX(), source.getY() + 1, source.getZ(), 6, Level.ExplosionInteraction.NONE);

            if (!source.level().isClientSide() && source.getServer() != null) {
                ParticleTool.spawnMediumExplosionParticles(source.level(), source.getPosition(0));
            }

            if (source instanceof ServerPlayer player) {
                CriteriaRegister.RPG_MELEE_EXPLOSION.trigger(player);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
            } else {
                stack.shrink(1);
            }
        }

        return super.hurtEnemy(stack, entity, source);
    }

    @Override
    public AbstractProjectileDispenseBehavior getLaunchBehavior() {
        return new AbstractProjectileDispenseBehavior() {

            @Override
            protected float getPower() {
                return 1.5F;
            }

            @Override
            @ParametersAreNonnullByDefault
            protected @NotNull Projectile getProjectile(Level pLevel, Position pPosition, ItemStack pStack) {
                return new RpgRocketEntity(ModEntities.RPG_ROCKET.get(), pPosition.x(), pPosition.y(), pPosition.z(), pLevel);
            }

            @Override
            protected void playSound(BlockSource pSource) {
                pSource.getLevel().playSound(null, pSource.getPos(), ModSounds.RPG_FIRE_3P.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        };
    }
}