package com.atsuishio.superbwarfare.client.overlay;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.client.ClickHandler;
import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.entity.vehicle.base.ArmedVehicleEntity;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.atsuishio.superbwarfare.init.ModPerks;
import com.atsuishio.superbwarfare.item.gun.GunItem;
import com.atsuishio.superbwarfare.item.gun.data.GunData;
import com.atsuishio.superbwarfare.tools.SeekTool;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class HandsomeFrameOverlay implements IGuiOverlay {

    public static final String ID = Mod.MODID + "_handsome_frame";

    private static final ResourceLocation FRAME = Mod.loc("textures/screens/frame/frame.png");
    private static final ResourceLocation FRAME_WEAK = Mod.loc("textures/screens/frame/frame_weak.png");
    private static final ResourceLocation FRAME_TARGET = Mod.loc("textures/screens/frame/frame_target.png");
    private static final ResourceLocation FRAME_LOCK = Mod.loc("textures/screens/frame/frame_lock.png");

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Player player = gui.getMinecraft().player;
        PoseStack poseStack = guiGraphics.pose();

        if (player == null) return;
        ItemStack stack = player.getMainHandItem();

        if (ClickHandler.isEditing)
            return;
        if (player.getVehicle() instanceof ArmedVehicleEntity iArmedVehicle && iArmedVehicle.banHand(player))
            return;

        if (stack.getItem() instanceof GunItem && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            int level = GunData.from(stack).perk.getLevel(ModPerks.INTELLIGENT_CHIP);
            if (level == 0) return;

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.setShaderColor(1, 1, 1, 1);

            List<Entity> allEntities = SeekTool.seekLivingEntitiesThroughWall(player, player.level(), 32 + 8 * (level - 1), 30);
            List<Entity> visibleEntities = SeekTool.seekLivingEntities(player, player.level(), 32 + 8 * (level - 1), 30);

            Entity naerestEntity = SeekTool.seekLivingEntity(player, player.level(), 32 + 8 * (level - 1), 30);
            Entity targetEntity = ClientEventHandler.entity;

            float fovAdjust2 = (float) (Minecraft.getInstance().options.fov().get() / 30) - 1;

            double zoom = 1;

            if (ClientEventHandler.zoom) {
                zoom = Minecraft.getInstance().options.fov().get() / ClientEventHandler.fov + 0.05 * fovAdjust2;
            }

            for (var e : allEntities) {
                Vec3 playerVec = new Vec3(Mth.lerp(partialTick, player.xo, player.getX()), Mth.lerp(partialTick, player.yo + player.getEyeHeight(), player.getEyeY()), Mth.lerp(partialTick, player.zo, player.getZ()));
                Vec3 pos = new Vec3(Mth.lerp(partialTick, e.xo, e.getX()), Mth.lerp(partialTick, e.yo + e.getEyeHeight(), e.getEyeY()), Mth.lerp(partialTick, e.zo, e.getZ()));
                Vec3 lookAngle = player.getLookAngle().normalize().scale(pos.distanceTo(playerVec) * (1 - 1.0 / zoom));

                var cPos = playerVec.add(lookAngle);
                Vec3 point = RenderHelper.worldToScreen(pos, cPos);
                if (point == null) return;

                boolean lockOn = e == targetEntity;
                boolean isNearestEntity = e == naerestEntity;

                poseStack.pushPose();
                float x = (float) point.x;
                float y = (float) point.y;

                var canBeSeen = visibleEntities.contains(e);

                ResourceLocation icon;
                if (lockOn) {
                    icon = FRAME_LOCK;
                } else if (canBeSeen) {
                    if (isNearestEntity) {
                        icon = FRAME_TARGET;
                    } else {
                        icon = FRAME;
                    }
                } else {
                    icon = FRAME_WEAK;
                }

                RenderHelper.blit(poseStack, icon, x - 12, y - 12, 0, 0, 24, 24, 24, 24, 1f);
                poseStack.popPose();
            }
        }
    }
}
