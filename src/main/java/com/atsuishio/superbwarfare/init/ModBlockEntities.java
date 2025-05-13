package com.atsuishio.superbwarfare.init;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.block.entity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Mod.MODID);

    public static final RegistryObject<BlockEntityType<ContainerBlockEntity>> CONTAINER = REGISTRY.register("container",
            () -> BlockEntityType.Builder.of(ContainerBlockEntity::new, ModBlocks.CONTAINER.get()).build(null));
    public static final RegistryObject<BlockEntityType<ChargingStationBlockEntity>> CHARGING_STATION = REGISTRY.register("charging_station",
            () -> BlockEntityType.Builder.of(ChargingStationBlockEntity::new, ModBlocks.CHARGING_STATION.get()).build(null));
    public static final RegistryObject<BlockEntityType<CreativeChargingStationBlockEntity>> CREATIVE_CHARGING_STATION = REGISTRY.register("creative_charging_station",
            () -> BlockEntityType.Builder.of(CreativeChargingStationBlockEntity::new, ModBlocks.CREATIVE_CHARGING_STATION.get()).build(null));
    public static final RegistryObject<BlockEntityType<FuMO25BlockEntity>> FUMO_25 = REGISTRY.register("fumo_25",
            () -> BlockEntityType.Builder.of(FuMO25BlockEntity::new, ModBlocks.FUMO_25.get()).build(null));
    public static final RegistryObject<BlockEntityType<SmallContainerBlockEntity>> SMALL_CONTAINER = REGISTRY.register("small_container",
            () -> BlockEntityType.Builder.of(SmallContainerBlockEntity::new, ModBlocks.SMALL_CONTAINER.get()).build(null));
    public static final RegistryObject<BlockEntityType<VehicleDeployerBlockEntity>> VEHICLE_DEPLOYER = REGISTRY.register("vehicle_deployer",
            () -> BlockEntityType.Builder.of(VehicleDeployerBlockEntity::new, ModBlocks.VEHICLE_DEPLOYER.get()).build(null));
    public static final RegistryObject<BlockEntityType<AircraftCatapultBlockEntity>> AIRCRAFT_CATAPULT = REGISTRY.register("aircraft_catapult",
            () -> BlockEntityType.Builder.of(AircraftCatapultBlockEntity::new, ModBlocks.AIRCRAFT_CATAPULT.get()).build(null));
}
