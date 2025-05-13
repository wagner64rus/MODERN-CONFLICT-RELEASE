package com.atsuishio.superbwarfare.block.entity;

import com.atsuishio.superbwarfare.block.VehicleDeployerBlock;
import com.atsuishio.superbwarfare.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.util.UUID;

public class VehicleDeployerBlockEntity extends BlockEntity {

    public CompoundTag entityData = new CompoundTag();

    public VehicleDeployerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.VEHICLE_DEPLOYER.get(), pPos, pBlockState);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);

        if (this.entityData.contains("EntityType")) {
            tag.putString("EntityType", this.entityData.getString("EntityType"));
        }
        if (this.entityData.contains("Entity")) {
            tag.put("Entity", this.entityData.getCompound("Entity"));
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);

        this.entityData = tag.copy();
    }

    public void deploy(BlockState state) {
        if (this.level == null) return;

        if (this.entityData.contains("EntityType")) {
            var entityType = EntityType.byString(entityData.getString("EntityType")).orElse(null);
            if (entityType == null) return;

            var entity = entityType.create(this.level);
            if (entity == null) return;

            if (entityData.contains("Entity")) {
                var entityTag = entityData.getCompound("Entity").copy();
                entityTag.remove("UUID");
                entity.load(entityTag);
            }

            var direction = state.getValue(VehicleDeployerBlock.FACING);

            entity.setUUID(UUID.randomUUID());
            entity.setPos(this.getBlockPos().getX() + 0.5 + (2 * Math.random() - 1) * 0.1f, this.getBlockPos().getY() + 1.5 + (2 * Math.random() - 1) * 0.1f, this.getBlockPos().getZ() + 0.5 + (2 * Math.random() - 1) * 0.1f);
            entity.setYRot(direction.toYRot());
            this.level.addFreshEntity(entity);
        }
    }

    public void writeEntityInfo(ItemStack stack) {
        var tag = BlockItem.getBlockEntityData(stack);
        if (tag == null) return;

        this.entityData = tag.copy();
    }
}
