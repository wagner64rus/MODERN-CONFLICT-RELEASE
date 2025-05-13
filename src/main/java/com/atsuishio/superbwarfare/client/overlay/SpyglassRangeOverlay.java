package com.atsuishio.superbwarfare.client.overlay;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.tools.FormatTool;
import com.atsuishio.superbwarfare.tools.TraceTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@OnlyIn(Dist.CLIENT)
public class SpyglassRangeOverlay implements IGuiOverlay {

    public static final String ID = Mod.MODID + "_spyglass_range";

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Player player = gui.getMinecraft().player;
        if (player == null) return;

        if ((player.getMainHandItem().getItem() == Items.SPYGLASS || player.getOffhandItem().getItem() == Items.SPYGLASS) && player.isUsingItem()) {
            boolean lookAtEntity = false;

            BlockHitResult result = player.level().clip(new ClipContext(player.getEyePosition(), player.getEyePosition().add(player.getViewVector(1).scale(512)),
                    ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
            Vec3 hitPos = result.getLocation();

            double blockRange = player.getEyePosition(1).distanceTo(hitPos);

            double entityRange = 0;
            Entity lookingEntity = TraceTool.findLookingEntity(player, 520);

            if (lookingEntity != null) {
                lookAtEntity = true;
                entityRange = player.distanceTo(lookingEntity);
            }

            if (lookAtEntity) {
                guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("tips.superbwarfare.drone.range")
                                .append(Component.literal(FormatTool.format1D(entityRange, "M ") + lookingEntity.getDisplayName().getString())),
                        screenWidth / 2 + 12, screenHeight / 2 - 28, -1, false);
            } else {
                if (blockRange > 500) {
                    guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("tips.superbwarfare.drone.range")
                            .append(Component.literal("---M")), screenWidth / 2 + 12, screenHeight / 2 - 28, -1, false);
                } else {
                    guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("tips.superbwarfare.drone.range")
                                    .append(Component.literal(FormatTool.format1D(blockRange, "M"))),
                            screenWidth / 2 + 12, screenHeight / 2 - 28, -1, false);
                }
            }
        }
    }
}
