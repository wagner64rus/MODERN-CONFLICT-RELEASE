package com.atsuishio.superbwarfare.client.renderer.item;

import com.atsuishio.superbwarfare.client.AnimationHelper;
import com.atsuishio.superbwarfare.client.ItemModelHelper;
import com.atsuishio.superbwarfare.client.model.item.AK47ItemModel;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.atsuishio.superbwarfare.item.gun.GunItem;
import com.atsuishio.superbwarfare.item.gun.data.GunData;
import com.atsuishio.superbwarfare.item.gun.data.value.AttachmentType;
import com.atsuishio.superbwarfare.item.gun.rifle.AK47Item;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.RenderUtils;

import java.util.HashSet;
import java.util.Set;

public class AK47ItemRenderer extends GeoItemRenderer<AK47Item> {
    public AK47ItemRenderer() {
        super(new AK47ItemModel());
    }

    @Override
    public RenderType getRenderType(AK47Item animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    private static final float SCALE_RECIPROCAL = 1.0f / 16.0f;
    protected boolean renderArms = false;
    protected MultiBufferSource currentBuffer;
    protected RenderType renderType;
    public ItemDisplayContext transformType;
    protected AK47Item animatable;
    private final Set<String> hiddenBones = new HashSet<>();

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int p_239207_6_) {
        this.transformType = transformType;
        if (this.animatable != null)
            this.animatable.getTransformType(transformType);
        super.renderByItem(stack, transformType, matrixStack, bufferIn, combinedLightIn, p_239207_6_);
    }

    @Override
    public void actuallyRender(PoseStack matrixStackIn, AK47Item animatable, BakedGeoModel model, RenderType type, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, boolean isRenderer, float partialTicks, int packedLightIn,
                               int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.currentBuffer = renderTypeBuffer;
        this.renderType = type;
        this.animatable = animatable;
        super.actuallyRender(matrixStackIn, animatable, model, type, renderTypeBuffer, vertexBuilder, isRenderer, partialTicks, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        if (this.renderArms) {
            this.renderArms = false;
        }
    }

    @Override
    public void renderRecursively(PoseStack stack, AK47Item animatable, GeoBone bone, RenderType type, MultiBufferSource buffer, VertexConsumer bufferIn, boolean isReRender, float partialTick, int packedLightIn, int packedOverlayIn, float red,
                                  float green, float blue, float alpha) {
        Minecraft mc = Minecraft.getInstance();
        String name = bone.getName();
        boolean renderingArms = false;
        if (name.equals("Lefthand") || name.equals("Righthand")) {
            bone.setHidden(true);
            renderingArms = true;
        } else {
            bone.setHidden(this.hiddenBones.contains(name));
        }

        Player player = mc.player;
        if (player != null) {
            ItemStack itemStack = player.getMainHandItem();
            if (!(itemStack.getItem() instanceof GunItem)) return;

            if (name.equals("humu1")) {
                bone.setHidden(GunData.from(itemStack).attachment.get(AttachmentType.GRIP) != 0);
            }

            if (name.equals("humu2")) {
                bone.setHidden(GunData.from(itemStack).attachment.get(AttachmentType.GRIP) == 0);
            }

            if (GunData.from(itemStack).attachment.get(AttachmentType.SCOPE) == 2
                    && (name.equals("Hidden") || name.equals("gun") || name.equals("Lefthand")) && ClientEventHandler.zoom && ClientEventHandler.zoomPos > 0.7 ) {
                bone.setHidden(true);
                renderingArms = false;
            }

            if (GunData.from(itemStack).attachment.get(AttachmentType.SCOPE) == 3
                    && (name.equals("jing") || name.equals("Barrel") || name.equals("humu") || name.equals("qiangguan") || name.equals("houzhunxing"))) {
                bone.setHidden(ClientEventHandler.zoomPos > 0.7 && ClientEventHandler.zoom);
            }

            int scopeType = GunData.from(itemStack).attachment.get(AttachmentType.SCOPE);

            switch (scopeType) {
                case 1 -> AnimationHelper.handleZoomCrossHair(currentBuffer, renderType, name, stack, bone, buffer, -0.03, 0.27363125, 20, 1, 255, 0, 0, 255, "kobra", false);
                case 2 -> AnimationHelper.handleZoomCrossHair(currentBuffer, renderType, name, stack, bone, buffer, -0.04, 0.28, 18, 1, 255, 0, 0, 255, "pso_1", true);
                case 3 -> AnimationHelper.handleZoomCrossHair(currentBuffer, renderType, name, stack, bone, buffer, -0.03, 0.28, 36, (float) ClientEventHandler.customZoom, 255, 0, 0, 255, "lpvo", true);
            }

            AnimationHelper.handleShootFlare(name, stack, itemStack, bone, buffer, packedLightIn, 0, 0, 1.06875, 0.3);
            ItemModelHelper.handleGunAttachments(bone, itemStack, name);
        }


        if (this.transformType.firstPerson() && renderingArms) {
            AbstractClientPlayer localPlayer = mc.player;

            if (localPlayer == null) {
                return;
            }

            PlayerRenderer playerRenderer = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(localPlayer);
            PlayerModel<AbstractClientPlayer> model = playerRenderer.getModel();
            stack.pushPose();
            RenderUtils.translateMatrixToBone(stack, bone);
            RenderUtils.translateToPivotPoint(stack, bone);
            RenderUtils.rotateMatrixAroundBone(stack, bone);
            RenderUtils.scaleMatrixForBone(stack, bone);
            RenderUtils.translateAwayFromPivotPoint(stack, bone);
            ResourceLocation loc = localPlayer.getSkinTextureLocation();
            VertexConsumer armBuilder = this.currentBuffer.getBuffer(RenderType.entitySolid(loc));
            VertexConsumer sleeveBuilder = this.currentBuffer.getBuffer(RenderType.entityTranslucent(loc));
            if (name.equals("Lefthand")) {
                stack.translate(-1.0f * SCALE_RECIPROCAL, 2.0f * SCALE_RECIPROCAL, 0.0f);
                AnimationHelper.renderPartOverBone2(model.leftArm, bone, stack, armBuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1);
                AnimationHelper.renderPartOverBone2(model.leftSleeve, bone, stack, sleeveBuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1);
            } else {
                stack.translate(SCALE_RECIPROCAL, 2.0f * SCALE_RECIPROCAL, 0.0f);
                AnimationHelper.renderPartOverBone2(model.rightArm, bone, stack, armBuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1);
                AnimationHelper.renderPartOverBone2(model.rightSleeve, bone, stack, sleeveBuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1);
            }

            this.currentBuffer.getBuffer(this.renderType);
            stack.popPose();
        }
        super.renderRecursively(stack, animatable, bone, type, buffer, bufferIn, isReRender, partialTick, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public ResourceLocation getTextureLocation(AK47Item instance) {
        return super.getTextureLocation(instance);
    }
}
