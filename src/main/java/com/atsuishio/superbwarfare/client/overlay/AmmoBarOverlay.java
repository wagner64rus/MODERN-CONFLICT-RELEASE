package com.atsuishio.superbwarfare.client.overlay;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.client.DisplayConfig;
import com.atsuishio.superbwarfare.entity.vehicle.base.ArmedVehicleEntity;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModKeyMappings;
import com.atsuishio.superbwarfare.item.gun.GunItem;
import com.atsuishio.superbwarfare.item.gun.data.GunData;
import com.atsuishio.superbwarfare.tools.InventoryTool;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@OnlyIn(Dist.CLIENT)
public class AmmoBarOverlay implements IGuiOverlay {

    public static final String ID = Mod.MODID + "_ammo_bar";

    private static final ResourceLocation LINE = Mod.loc("textures/gun_icon/fire_mode/line.png");
    private static final ResourceLocation SEMI = Mod.loc("textures/gun_icon/fire_mode/semi.png");
    private static final ResourceLocation BURST = Mod.loc("textures/gun_icon/fire_mode/burst.png");
    private static final ResourceLocation AUTO = Mod.loc("textures/gun_icon/fire_mode/auto.png");
    private static final ResourceLocation TOP = Mod.loc("textures/gun_icon/fire_mode/top.png");
    private static final ResourceLocation DIR = Mod.loc("textures/gun_icon/fire_mode/dir.png");
    private static final ResourceLocation MOUSE = Mod.loc("textures/gun_icon/fire_mode/mouse.png");

    private static boolean hasCreativeAmmo() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return false;
        return player.isCreative() || InventoryTool.hasCreativeAmmoBox(player);
    }

    private static ResourceLocation getFireMode(GunData data) {
        return switch (data.fireMode.get()) {
            case SEMI -> SEMI;
            case BURST -> BURST;
            case AUTO -> AUTO;
        };
    }

    private static String getGunAmmoString(GunData data, Player player) {
        if (data.useBackpackAmmo() && hasCreativeAmmo()) return "∞";
        return data.useBackpackAmmo() ? data.countBackupAmmo(player) + "" : data.ammo.get() + "";
    }

    private static String getBackupAmmoString(GunData data, Player player) {
        if (data.useBackpackAmmo()) return "";

        return hasCreativeAmmo() ? "∞" : data.countBackupAmmo(player) + "";
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (!DisplayConfig.AMMO_HUD.get()) return;

        Player player = gui.getMinecraft().player;

        if (player == null) return;
        if (player.isSpectator()) return;

        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof GunItem gunItem && !(player.getVehicle() instanceof ArmedVehicleEntity vehicle && vehicle.banHand(player))) {
            PoseStack poseStack = guiGraphics.pose();
            var data = GunData.from(stack);

            // 渲染图标
            guiGraphics.blit(gunItem.getGunIcon(),
                    screenWidth - 135,
                    screenHeight - 40,
                    0,
                    0,
                    64,
                    16,
                    64,
                    16);

            // 渲染开火模式切换按键
            if (stack.getItem() != ModItems.MINIGUN.get()) {
                guiGraphics.drawString(
                        Minecraft.getInstance().font,
                        "[" + ModKeyMappings.FIRE_MODE.getKey().getDisplayName().getString() + "]",
                        screenWidth - 111.5f,
                        screenHeight - 20,
                        0xFFFFFF,
                        false
                );
            }

            // 渲染开火模式
            ResourceLocation fireMode = getFireMode(data);

            if (stack.getItem() == ModItems.JAVELIN.get()) {
                fireMode = stack.getOrCreateTag().getBoolean("TopMode") ? TOP : DIR;
            }

            if (stack.getItem() == ModItems.MINIGUN.get()) {
                fireMode = MOUSE;
                // 渲染加特林射速
                guiGraphics.drawString(
                        Minecraft.getInstance().font,
                        data.rpm() + " RPM",
                        screenWidth - 111f,
                        screenHeight - 20,
                        0xFFFFFF,
                        false
                );

                guiGraphics.blit(fireMode,
                        screenWidth - 126,
                        screenHeight - 22,
                        0,
                        0,
                        12,
                        12,
                        12,
                        12);
            } else {
                guiGraphics.blit(fireMode,
                        screenWidth - 95,
                        screenHeight - 21,
                        0,
                        0,
                        8,
                        8,
                        8,
                        8);
            }

            if (stack.getItem() != ModItems.MINIGUN.get() && stack.getItem() != ModItems.TRACHELIUM.get()) {
                guiGraphics.blit(LINE,
                        screenWidth - 95,
                        screenHeight - 16,
                        0,
                        0,
                        8,
                        8,
                        8,
                        8);
            }

            // 渲染当前弹药量
            poseStack.pushPose();
            poseStack.scale(1.5f, 1.5f, 1f);

            guiGraphics.drawString(
                    Minecraft.getInstance().font,
                    getGunAmmoString(data, player),
                    screenWidth / 1.5f - 64 / 1.5f,
                    screenHeight / 1.5f - 48 / 1.5f,
                    0xFFFFFF,
                    true
            );

            poseStack.popPose();

            // 渲染备弹量
            guiGraphics.drawString(
                    Minecraft.getInstance().font,
                    getBackupAmmoString(data, player),
                    screenWidth - 64,
                    screenHeight - 35,
                    0xCCCCCC,
                    true
            );

            poseStack.pushPose();
            poseStack.scale(0.9f, 0.9f, 1f);

            // 渲染物品名称
            String gunName = gunItem.getGunDisplayName();
            guiGraphics.drawString(
                    Minecraft.getInstance().font,
                    gunName,
                    screenWidth / 0.9f - (100 + Minecraft.getInstance().font.width(gunName) / 2f) / 0.9f,
                    screenHeight / 0.9f - 60 / 0.9f,
                    0xFFFFFF,
                    true
            );

            // 渲染弹药类型
            String ammoName = gunItem.getAmmoDisplayName(data);
            guiGraphics.drawString(
                    Minecraft.getInstance().font,
                    ammoName,
                    screenWidth / 0.9f - (100 + Minecraft.getInstance().font.width(ammoName) / 2f) / 0.9f,
                    screenHeight / 0.9f - 51 / 0.9f,
                    0xC8A679,
                    true
            );

            poseStack.popPose();
        }
    }
}
