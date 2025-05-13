package com.atsuishio.superbwarfare.client.layer.gun;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.client.ModRenderTypes;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.atsuishio.superbwarfare.item.gun.special.BocekItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class BocekPowerLightLayer extends GeoRenderLayer<BocekItem> {
    private static final ResourceLocation LAYER = new ResourceLocation(Mod.MODID, "textures/item/bocek_power_e.png");

    public BocekPowerLightLayer(GeoRenderer<BocekItem> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(PoseStack poseStack, BocekItem animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType glowRenderType = ModRenderTypes.ILLUMINATED.apply(LAYER);
        getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, glowRenderType, bufferSource.getBuffer(glowRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY, (float) ClientEventHandler.bowPower, (float) ClientEventHandler.bowPower, (float) ClientEventHandler.bowPower, 1);
    }
}
