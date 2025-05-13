package com.atsuishio.superbwarfare.item;

import com.atsuishio.superbwarfare.client.tooltip.component.ChargingStationImageComponent;
import com.atsuishio.superbwarfare.init.ModBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ChargingStationBlockItem extends BlockItem {

    public static final int MAX_ENERGY = 4000000;

    public ChargingStationBlockItem() {
        super(ModBlocks.CHARGING_STATION.get(), new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        CompoundTag tag = BlockItem.getBlockEntityData(pStack);
        int energy = tag == null ? 0 : tag.getInt("Energy");
        return energy != MAX_ENERGY && energy != 0;
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        CompoundTag tag = BlockItem.getBlockEntityData(pStack);
        int energy = tag == null ? 0 : tag.getInt("Energy");
        return Math.round((float) energy * 13.0F / MAX_ENERGY);
    }

    @Override
    public int getBarColor(@NotNull ItemStack pStack) {
        return 0xFFFF00;
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack pStack) {
        return Optional.of(new ChargingStationImageComponent(pStack));
    }
}
