package com.atsuishio.superbwarfare.network.message.send;

import com.atsuishio.superbwarfare.entity.vehicle.DroneEntity;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.item.gun.GunItem;
import com.atsuishio.superbwarfare.tools.EntityFindUtil;
import com.atsuishio.superbwarfare.tools.TraceTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class InteractMessage {

    private final int type;

    public InteractMessage(int type) {
        this.type = type;
    }

    public static InteractMessage decode(FriendlyByteBuf buffer) {
        return new InteractMessage(buffer.readInt());
    }

    public static void encode(InteractMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.type);
    }

    public static void handler(InteractMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getSender() != null) {
                handleInteract(context.getSender(), message.type);
            }
        });
        context.setPacketHandled(true);
    }

    public static void handleInteract(Player player, int type) {
        Level level = player.level();

        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof GunItem) {
            double blockRange = player.getBlockReach();
            double entityRange = player.getBlockReach();

            Vec3 looking = Vec3.atLowerCornerOf(player.level().clip(new ClipContext(player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().scale(blockRange)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player)).getBlockPos());
            BlockPos blockPos = BlockPos.containing(looking.x(), looking.y(), looking.z());
            level.getBlockState(blockPos).use(player.level(), player, InteractionHand.MAIN_HAND, BlockHitResult.miss(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), Direction.UP, blockPos));

            if ((level.getBlockState(BlockPos.containing(looking.x(), looking.y(), looking.z()))).getBlock() instanceof BellBlock bell) {
                bell.attemptToRing(level, blockPos, player.getDirection().getOpposite());
            }

            Entity lookingEntity = TraceTool.findLookingEntity(player, entityRange);
            if (lookingEntity == null) return;

            player.interactOn(lookingEntity, InteractionHand.MAIN_HAND);
        } else if (stack.is(ModItems.MONITOR.get()) && stack.getOrCreateTag().getBoolean("Using") && stack.getOrCreateTag().getBoolean("Linked") && !player.getCooldowns().isOnCooldown(stack.getItem())) {
            DroneEntity drone = EntityFindUtil.findDrone(player.level(), stack.getOrCreateTag().getString("LinkedDrone"));

            if (drone != null) {
                Vec3 looking = Vec3.atLowerCornerOf(player.level().clip(new ClipContext(drone.getEyePosition(), drone.getEyePosition().add(drone.getLookAngle().scale(2)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player)).getBlockPos());
                BlockPos blockPos = BlockPos.containing(looking.x(), looking.y(), looking.z());
                player.level().getBlockState(blockPos).use(player.level(), player, InteractionHand.MAIN_HAND, BlockHitResult.miss(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), Direction.UP, blockPos));

                Entity lookingEntity = TraceTool.findLookingEntity(drone, 2);
                if (lookingEntity == null) return;

                player.attack(lookingEntity);
                player.getCooldowns().addCooldown(stack.getItem(), 13);
            }
        }
    }
}
