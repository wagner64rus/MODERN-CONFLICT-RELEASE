package com.atsuishio.superbwarfare.client.layer.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.Bmd4Renderer;
import com.atsuishio.superbwarfare.entity.vehicle.Bmd4Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class Bmd4Layer extends GeoRenderLayer<Bmd4Entity> {
    private final ResourceLocation texture;

    public Bmd4Layer(GeoRenderer<Bmd4Entity> entityRendererIn) {
        super(entityRendererIn);
        this.texture = new ResourceLocation("superbwarfare", "textures/entity/bmd4.png");
    }

    public void render(PoseStack poseStack, Bmd4Entity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (bone.getName().equals("flare")) {
            RenderType flareRenderType = RenderType.entityTranslucent(this.texture);
            this.getRenderer().reRender(this.getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, flareRenderType, bufferSource.getBuffer(flareRenderType), partialTick, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
} 