package com.atsuishio.superbwarfare.capability.energy;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.EnergyStorage;

public class ItemEnergyStorage extends EnergyStorage {

    private static final String NBT_ENERGY = "Energy";

    private final ItemStack stack;

    public ItemEnergyStorage(ItemStack stack, int capacity) {
        super(capacity, Integer.MAX_VALUE, Integer.MAX_VALUE);

        this.stack = stack;
        this.energy = stack.hasTag() && stack.getTag().contains(NBT_ENERGY) ? stack.getTag().getInt(NBT_ENERGY) : 0;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);

        if (received > 0 && !simulate) {
            stack.getOrCreateTag().putInt(NBT_ENERGY, getEnergyStored());
        }

        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);

        if (extracted > 0 && !simulate) {
            stack.getOrCreateTag().putInt(NBT_ENERGY, getEnergyStored());
        }

        return extracted;
    }
}
