package com.atsuishio.superbwarfare.item;

import com.atsuishio.superbwarfare.entity.projectile.HeliRocketEntity;
import com.atsuishio.superbwarfare.init.ModEntities;
import com.atsuishio.superbwarfare.init.ModSounds;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class Rocket70 extends Item implements DispenserLaunchable {

    public Rocket70() {
        super(new Properties());
    }

    @Override
    public AbstractProjectileDispenseBehavior getLaunchBehavior() {
        return new AbstractProjectileDispenseBehavior() {

            @Override
            public ItemStack execute(BlockSource pSource, ItemStack pStack) {
                Level $$2 = pSource.getLevel();
                Position $$3 = DispenserBlock.getDispensePosition(pSource);
                Direction $$4 = pSource.getBlockState().getValue(DispenserBlock.FACING);
                Projectile $$5 = this.getProjectile($$2, $$3, pStack);
                $$5.shoot($$4.getStepX(), (float)$$4.getStepY(), $$4.getStepZ(), this.getPower(), this.getUncertainty());
                $$2.addFreshEntity($$5);
                pStack.shrink(1);
                return pStack;
            }

            @Override
            protected float getPower() {
                return 4F;
            }

            @Override
            protected float getUncertainty() {
                return 1F;
            }

            @Override
            @ParametersAreNonnullByDefault
            protected @NotNull Projectile getProjectile(Level pLevel, Position pPosition, ItemStack pStack) {
                return new HeliRocketEntity(ModEntities.HELI_ROCKET.get(), pPosition.x(), pPosition.y(), pPosition.z(), pLevel);
            }

            @Override
            protected void playSound(BlockSource pSource) {
                pSource.getLevel().playSound(null, pSource.getPos(), ModSounds.HELICOPTER_ROCKET_FIRE_3P.get(), SoundSource.BLOCKS, 2.0F, 1.0F);
            }
        };
    }
}