package com.atsuishio.superbwarfare.client.renderer.special;

import com.atsuishio.superbwarfare.block.ContainerBlock;
import com.atsuishio.superbwarfare.block.entity.ContainerBlockEntity;
import com.atsuishio.superbwarfare.client.renderer.CustomRenderType;
import com.atsuishio.superbwarfare.item.Crowbar;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ContainerBlockPreview {
    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        var player = Minecraft.getInstance().player;
        assert player != null;

        // 仅在手持撬棍时检测
        var item = player.getMainHandItem();
        if (!(item.getItem() instanceof Crowbar)) return;

        var level = player.level();
        var look = player.getLookAngle();

        // 查找玩家看向方块
        int distance = 32;
        var start = player.position().add(0, player.getEyeHeight(), 0);
        var end = player.position().add(look.x * distance, look.y * distance + player.getEyeHeight(), look.z * distance);
        var context = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player);
        var result = player.level().clip(context);

        if (result.getType().equals(BlockHitResult.Type.MISS)) return;

        // 获取集装箱
        var blockEntity = level.getBlockEntity(result.getBlockPos());
        if (!(blockEntity instanceof ContainerBlockEntity container)) return;

        // 获取实体信息
        var entityType = container.entityType;
        var entity = container.entity;

        int w = 0, h = 0;
        if (entityType != null) {
            w = (int) (entityType.getDimensions().width / 2 + 1);
            h = (int) (entityType.getDimensions().height + 1);
        }
        if (entity != null) {
            w = (int) (entity.getType().getDimensions().width / 2 + 1);
            h = (int) (entity.getType().getDimensions().height + 1);
        }
        if (w == 0 || h == 0) return;

        var poseStack = event.getPoseStack();
        poseStack.pushPose();
        var pos = container.getBlockPos();
        var view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        poseStack.translate(pos.getX() - view.x, pos.getY() - view.y + 1, pos.getZ() - view.z);

        // 什么b位置
        var aabb = new AABB(pos)
                .inflate(w, 0, w)
                .expandTowards(0, h - 1, 0)
                .move(0, -1, 0);

        float startX = (float) aabb.minX - 0.001f - pos.getX();
        float startY = (float) aabb.minY - 0.001f - pos.getY();
        float startZ = (float) aabb.minZ - 0.001f - pos.getZ();
        float endX = (float) aabb.maxX + 0.001f - pos.getX();
        float endY = (float) aabb.maxY + 0.001f - pos.getY();
        float endZ = (float) aabb.maxZ + 0.001f - pos.getZ();

        var hasEnoughSpace = ContainerBlock.canOpen(level, pos, entityType, entity);

        var red = hasEnoughSpace ? 0 : 1;
        var green = 1 - red;
        var blue = 0.0f;
        var alpha = 0.2f;

        var builder = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(CustomRenderType.BLOCK_OVERLAY);
        var m4f = poseStack.last().pose();

        // east
        builder.vertex(m4f, startX, startY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(m4f, startX, endY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(m4f, endX, endY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(m4f, endX, startY, startZ).color(red, green, blue, alpha).endVertex();

        // west
        builder.vertex(m4f, startX, startY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(m4f, endX, startY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(m4f, endX, endY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(m4f, startX, endY, endZ).color(red, green, blue, alpha).endVertex();

        // south
        builder.vertex(m4f, endX, startY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(m4f, endX, endY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(m4f, endX, endY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(m4f, endX, startY, endZ).color(red, green, blue, alpha).endVertex();

        // north
        builder.vertex(m4f, startX, startY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(m4f, startX, startY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(m4f, startX, endY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(m4f, startX, endY, startZ).color(red, green, blue, alpha).endVertex();

        // top
        builder.vertex(m4f, startX, endY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(m4f, endX, endY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(m4f, endX, endY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(m4f, startX, endY, endZ).color(red, green, blue, alpha).endVertex();

        // bottom
        builder.vertex(m4f, startX, startY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(m4f, endX, startY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(m4f, endX, startY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(m4f, startX, startY, endZ).color(red, green, blue, alpha).endVertex();

        poseStack.popPose();
    }
}
