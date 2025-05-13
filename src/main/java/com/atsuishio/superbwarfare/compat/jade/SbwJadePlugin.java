package com.atsuishio.superbwarfare.compat.jade;

import com.atsuishio.superbwarfare.block.ContainerBlock;
import com.atsuishio.superbwarfare.block.VehicleDeployerBlock;
import com.atsuishio.superbwarfare.block.entity.VehicleDeployerBlockEntity;
import com.atsuishio.superbwarfare.compat.jade.providers.*;
import com.atsuishio.superbwarfare.entity.C4Entity;
import com.atsuishio.superbwarfare.entity.DPSGeneratorEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class SbwJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(VehicleDeployerProvider.INSTANCE, VehicleDeployerBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(VehicleHealthProvider.INSTANCE, VehicleEntity.class);
        registration.registerEntityComponent(C4InfoProvider.INSTANCE, C4Entity.class);
        registration.registerEntityComponent(DPSGeneratorProvider.INSTANCE, DPSGeneratorEntity.class);
        registration.registerBlockComponent(ContainerEntityProvider.INSTANCE, ContainerBlock.class);
        registration.registerBlockComponent(VehicleDeployerProvider.INSTANCE, VehicleDeployerBlock.class);
    }
}
