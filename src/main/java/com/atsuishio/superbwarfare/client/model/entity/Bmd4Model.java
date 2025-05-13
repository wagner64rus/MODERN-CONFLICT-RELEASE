package com.atsuishio.superbwarfare.client.model.entity;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.entity.vehicle.Bmd4Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Bmd4Model extends GeoModel<Bmd4Entity> {

    @Override
    public ResourceLocation getAnimationResource(Bmd4Entity entity) {
        return Mod.loc("animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(Bmd4Entity entity) {
        return Mod.loc("geo/bmd4.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Bmd4Entity entity) {
        return Mod.loc("textures/entity/bmd4.png");
    }
} 