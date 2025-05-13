package com.atsuishio.superbwarfare.client;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.atsuishio.superbwarfare.item.gun.data.GunData;
import com.atsuishio.superbwarfare.item.gun.data.value.AttachmentType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.util.RenderUtils;

public class AnimationHelper {

    public static void renderPartOverBone(ModelPart model, GeoBone bone, PoseStack stack, VertexConsumer buffer, int packedLightIn, int packedOverlayIn, float alpha) {
        renderPartOverBone(model, bone, stack, buffer, packedLightIn, packedOverlayIn, 1.0f, 1.0f, 1.0f, alpha);
    }

    public static void renderPartOverBone(ModelPart model, GeoBone bone, PoseStack stack, VertexConsumer buffer, int packedLightIn, int packedOverlayIn, float r, float g, float b, float a) {
        setupModelFromBone(model, bone);
        model.render(stack, buffer, packedLightIn, packedOverlayIn, r, g, b, a);
    }

    public static void setupModelFromBone(ModelPart model, GeoBone bone) {
        model.setPos(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
        model.xRot = 0.0f;
        model.yRot = 0.0f;
        model.zRot = 0.0f;
    }

    public static void renderPartOverBone2(ModelPart model, GeoBone bone, PoseStack stack, VertexConsumer buffer, int packedLightIn, int packedOverlayIn, float alpha) {
        renderPartOverBone2(model, bone, stack, buffer, packedLightIn, packedOverlayIn, 1.0f, 1.0f, 1.0f, alpha);
    }

    public static void renderPartOverBone2(ModelPart model, GeoBone bone, PoseStack stack, VertexConsumer buffer, int packedLightIn, int packedOverlayIn, float r, float g, float b, float a) {
        setupModelFromBone2(model, bone);
        model.render(stack, buffer, packedLightIn, packedOverlayIn, r, g, b, a);
    }

    public static void setupModelFromBone2(ModelPart model, GeoBone bone) {
        model.setPos(bone.getPivotX(), bone.getPivotY() + 7, bone.getPivotZ());
        model.xRot = 0.0f;
        model.yRot = 180 * Mth.DEG_TO_RAD;
        model.zRot = 180 * Mth.DEG_TO_RAD;
    }

    public static void handleShellsAnimation(AnimationProcessor<?> animationProcessor, float x, float y) {
        CoreGeoBone shell1 = animationProcessor.getBone("shell1");
        CoreGeoBone shell2 = animationProcessor.getBone("shell2");
        CoreGeoBone shell3 = animationProcessor.getBone("shell3");
        CoreGeoBone shell4 = animationProcessor.getBone("shell4");
        CoreGeoBone shell5 = animationProcessor.getBone("shell5");

        ClientEventHandler.handleShells(x, y, shell1, shell2, shell3, shell4, shell5);
    }

    public static void handleReloadShakeAnimation(ItemStack stack, CoreGeoBone main, CoreGeoBone camera, float roll, float pitch) {
        if (GunData.from(stack).reload.time() > 0) {
            main.setRotX(roll * main.getRotX());
            main.setRotY(roll * main.getRotY());
            main.setRotZ(roll * main.getRotZ());
            main.setPosX(pitch * main.getPosX());
            main.setPosY(pitch * main.getPosY());
            main.setPosZ(pitch * main.getPosZ());
            camera.setRotX(roll * camera.getRotX());
            camera.setRotY(roll * camera.getRotY());
            camera.setRotZ(roll * camera.getRotZ());
        }
    }

    public static void handleShootFlare(String name, PoseStack stack, ItemStack itemStack, GeoBone bone, MultiBufferSource buffer, int packedLightIn, double x, double y, double z, double size) {
        if (name.equals("flare") && ClientEventHandler.firePosTimer > 0 && ClientEventHandler.firePosTimer < 0.5 && GunData.from(itemStack).attachment.get(AttachmentType.BARREL) != 2) {
            bone.setScaleX((float) (size + 0.8 * size * (Math.random() - 0.5)));
            bone.setScaleY((float) (size + 0.8 * size * (Math.random() - 0.5)));
            bone.setRotZ((float) (0.5 * (Math.random() - 0.5)));

            float height = 0f;

            if ((GunData.from(itemStack).attachment.get(AttachmentType.SCOPE) == 2 || GunData.from(itemStack).attachment.get(AttachmentType.SCOPE) == 3) && ClientEventHandler.zoom) {
                height = -0.07f;
            }

            stack.pushPose();
            stack.translate(x, y + 0.02 + height, -z);
            RenderUtils.translateMatrixToBone(stack, bone);
            RenderUtils.translateToPivotPoint(stack, bone);
            RenderUtils.rotateMatrixAroundBone(stack, bone);
            RenderUtils.scaleMatrixForBone(stack, bone);
            RenderUtils.translateAwayFromPivotPoint(stack, bone);
            PoseStack.Pose $$6 = stack.last();
            Matrix4f $$7 = $$6.pose();
            Matrix3f $$8 = $$6.normal();
            VertexConsumer $$9 = buffer.getBuffer(ModRenderTypes.MUZZLE_FLASH_TYPE.apply(Mod.loc("textures/particle/flare.png")));
            vertex($$9, $$7, $$8, packedLightIn, 0.0F, 0, 0, 1);
            vertex($$9, $$7, $$8, packedLightIn, 1.0F, 0, 1, 1);
            vertex($$9, $$7, $$8, packedLightIn, 1.0F, 1, 1, 0);
            vertex($$9, $$7, $$8, packedLightIn, 0.0F, 1, 0, 0);
            stack.popPose();
        }
    }

    private static void vertex(VertexConsumer pConsumer, Matrix4f pPose, Matrix3f pNormal, int pLightmapUV, float pX, float pY, int pU, int pV) {
        pConsumer.vertex(pPose, pX - 0.5F, pY - 0.5F, 0.0F).color(255, 255, 255, 255).uv((float)pU, (float)pV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(pLightmapUV).normal(pNormal, 0.0F, 1.0F, 0.0F).endVertex();
    }

    public static void handleZoomCrossHair(MultiBufferSource currentBuffer, RenderType renderType, String boneName, PoseStack stack, GeoBone bone, MultiBufferSource buffer, double x, double y, double z, float size, int r, int g, int b, int a ,String name, boolean hasBlackPart) {
        if (boneName.equals("cross") && ClientEventHandler.zoomPos > 0.8) {
            stack.pushPose();
            stack.translate(x, y, -z);
            RenderUtils.translateMatrixToBone(stack, bone);
            RenderUtils.translateToPivotPoint(stack, bone);
            RenderUtils.rotateMatrixAroundBone(stack, bone);
            RenderUtils.scaleMatrixForBone(stack, bone);
            RenderUtils.translateAwayFromPivotPoint(stack, bone);
            PoseStack.Pose $$6 = stack.last();
            Matrix4f $$7 = $$6.pose();
            Matrix3f $$8 = $$6.normal();
            ResourceLocation tex = Mod.loc("textures/crosshair/" + name + ".png");

            int alpha = hasBlackPart ? a : (int) (0.12 * a);

            VertexConsumer blackPart = buffer.getBuffer(RenderType.entityTranslucent(tex));
            vertexRGB(blackPart, $$7, $$8, 255, 0.0F, 0, 0, 1, r, g, b, alpha, size);
            vertexRGB(blackPart, $$7, $$8, 255, size, 0, 1, 1, r, g, b, alpha, size);
            vertexRGB(blackPart, $$7, $$8, 255, size, size, 1, 0, r, g, b, alpha, size);
            vertexRGB(blackPart, $$7, $$8, 255, 0.0F, size, 0, 0, r, g, b, alpha, size);

            VertexConsumer $$9 = buffer.getBuffer(ModRenderTypes.MUZZLE_FLASH_TYPE.apply(tex));
            vertexRGB($$9, $$7, $$8, 255, 0.0F, 0, 0, 1, r, g, b, a, size);
            vertexRGB($$9, $$7, $$8, 255, size, 0, 1, 1, r, g, b, a, size);
            vertexRGB($$9, $$7, $$8, 255, size, size, 1, 0, r, g, b, a, size);
            vertexRGB($$9, $$7, $$8, 255, 0.0F, size, 0, 0, r, g, b, a, size);
            stack.popPose();
        }
        currentBuffer.getBuffer(renderType);
    }

    private static void vertexRGB(VertexConsumer pConsumer, Matrix4f pPose, Matrix3f pNormal, int pLightmapUV, float pX, float pY, int pU, int pV, int r, int g, int b, int a, float size) {
        pConsumer.vertex(pPose, pX - 0.5F * size, pY - 0.5F * size, 0.0F).color(r, g, b, a).uv((float)pU, (float)pV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(pLightmapUV).normal(pNormal, 0.0F, 1.0F, 0.0F).endVertex();
    }
}
