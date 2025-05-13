package com.atsuishio.superbwarfare.client.renderer.entity;

import com.atsuishio.superbwarfare.entity.projectile.SmokeDecoyEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SmokeDecoyEntityRenderer extends EntityRenderer<SmokeDecoyEntity> {
    public SmokeDecoyEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    public void render(SmokeDecoyEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SmokeDecoyEntity flareDecoy) {
        return null;
    }
}
