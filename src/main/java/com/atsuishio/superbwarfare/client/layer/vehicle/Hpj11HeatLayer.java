package com.atsuishio.superbwarfare.client.layer.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.entity.vehicle.Hpj11Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import static com.atsuishio.superbwarfare.entity.vehicle.Hpj11Entity.HEAT;

public class Hpj11HeatLayer extends GeoRenderLayer<Hpj11Entity> {
    private static final ResourceLocation LAYER = Mod.loc("textures/entity/1130_heat.png");

    public Hpj11HeatLayer(GeoRenderer<Hpj11Entity> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(PoseStack poseStack, Hpj11Entity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType glowRenderType = RenderType.eyes(LAYER);
        float heat = animatable.getEntityData().get(HEAT) < 20 ? 0 : animatable.getEntityData().get(HEAT) - 20;
        getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, glowRenderType, bufferSource.getBuffer(glowRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY, heat / 80, heat / 80, heat / 80, 1);
    }
}

