package com.atsuishio.superbwarfare.item.common.ammo;

import com.atsuishio.superbwarfare.entity.projectile.MortarShellEntity;
import com.atsuishio.superbwarfare.init.ModEntities;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.item.DispenserLaunchable;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class MortarShell extends Item implements DispenserLaunchable {

    public MortarShell() {
        super(new Properties());
    }

    public MortarShellEntity createShell(LivingEntity entity, Level level, ItemStack stack) {
        MortarShellEntity shellEntity = new MortarShellEntity(entity, level);
        shellEntity.setEffectsFromItem(stack);
        return shellEntity;
    }

    @Override
    public DispenseItemBehavior getLaunchBehavior() {
        return new AbstractProjectileDispenseBehavior() {
            @Override
            protected float getPower() {
                return 0.5F;
            }

            @Override
            @ParametersAreNonnullByDefault
            protected @NotNull Projectile getProjectile(Level pLevel, Position pPosition, ItemStack pStack) {
                return new MortarShellEntity(ModEntities.MORTAR_SHELL.get(), pPosition.x(), pPosition.y(), pPosition.z(), pLevel);
            }

            @Override
            protected void playSound(BlockSource pSource) {
                pSource.getLevel().playSound(null, pSource.getPos(), ModSounds.MORTAR_FIRE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        };
    }
}
