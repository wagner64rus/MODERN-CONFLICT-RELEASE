package com.atsuishio.superbwarfare.api.event;

import com.atsuishio.superbwarfare.item.ContainerBlockItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Register Entities as a container
 */
@ApiStatus.AvailableSince("0.7.7")
public class RegisterContainersEvent extends Event implements IModBusEvent {

    public static final List<ItemStack> CONTAINERS = new ArrayList<>();

    public <T extends Entity> void add(RegistryObject<EntityType<T>> type) {
        add(type.get(), false);
    }

    public <T extends Entity> void add(RegistryObject<EntityType<T>> type, boolean canBePlacedAboveWater) {
        add(type.get(), canBePlacedAboveWater);
    }

    public <T extends Entity> void add(EntityType<T> type) {
        add(type, false);
    }

    public <T extends Entity> void add(EntityType<T> type, boolean canBePlacedAboveWater) {
        ItemStack stack = ContainerBlockItem.createInstance(type, canBePlacedAboveWater);
        CONTAINERS.add(stack);
    }

    public void add(Entity entity) {
        add(entity, false);
    }

    public void add(Entity entity, boolean canBePlacedAboveWater) {
        ItemStack stack = ContainerBlockItem.createInstance(entity, canBePlacedAboveWater);
        CONTAINERS.add(stack);
    }
}
