package com.atsuishio.superbwarfare.datagen;

import com.atsuishio.superbwarfare.init.ModBlocks;
import com.atsuishio.superbwarfare.init.ModItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Set;

public class ModBlockLootProvider extends BlockLootSubProvider {

    public ModBlockLootProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.SANDBAG.get());
        this.dropSelf(ModBlocks.BARBED_WIRE.get());
        this.dropSelf(ModBlocks.JUMP_PAD.get());
        this.dropSelf(ModBlocks.DRAGON_TEETH.get());
        this.dropSelf(ModBlocks.REFORGING_TABLE.get());
        this.dropSelf(ModBlocks.LEAD_BLOCK.get());
        this.dropSelf(ModBlocks.STEEL_BLOCK.get());
        this.dropSelf(ModBlocks.TUNGSTEN_BLOCK.get());
        this.dropSelf(ModBlocks.CEMENTED_CARBIDE_BLOCK.get());
        this.dropSelf(ModBlocks.SILVER_BLOCK.get());
        this.dropSelf(ModBlocks.CREATIVE_CHARGING_STATION.get());
        this.dropSelf(ModBlocks.FUMO_25.get());
        this.dropSelf(ModBlocks.VEHICLE_DEPLOYER.get());
        this.dropSelf(ModBlocks.AIRCRAFT_CATAPULT.get());

        this.add(ModBlocks.CHARGING_STATION.get(), createCopyNBTDrops(ModBlocks.CHARGING_STATION.get(),
                List.of(Pair.of("Energy", "BlockEntityTag.Energy"),
                        Pair.of("id", "BlockEntityTag.id"))));

        this.add(ModBlocks.GALENA_ORE.get(), this.createOreDrop(ModBlocks.GALENA_ORE.get(), ModItems.GALENA.get()));
        this.add(ModBlocks.SCHEELITE_ORE.get(), this.createOreDrop(ModBlocks.SCHEELITE_ORE.get(), ModItems.SCHEELITE.get()));
        this.add(ModBlocks.SILVER_ORE.get(), this.createOreDrop(ModBlocks.SILVER_ORE.get(), ModItems.RAW_SILVER.get()));
        this.add(ModBlocks.DEEPSLATE_GALENA_ORE.get(), this.createOreDrop(ModBlocks.DEEPSLATE_GALENA_ORE.get(), ModItems.GALENA.get()));
        this.add(ModBlocks.DEEPSLATE_SCHEELITE_ORE.get(), this.createOreDrop(ModBlocks.DEEPSLATE_SCHEELITE_ORE.get(), ModItems.SCHEELITE.get()));
        this.add(ModBlocks.DEEPSLATE_SILVER_ORE.get(), this.createOreDrop(ModBlocks.DEEPSLATE_SILVER_ORE.get(), ModItems.RAW_SILVER.get()));

        this.add(ModBlocks.CONTAINER.get(), LootTable.lootTable().withPool(this.applyExplosionCondition(ModBlocks.CONTAINER.get(),
                LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(ModBlocks.CONTAINER.get()))
                        .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Entity", "BlockEntityTag.Entity")
                                .copy("EntityType", "BlockEntityTag.EntityType")))));
        this.add(ModBlocks.SMALL_CONTAINER.get(), LootTable.lootTable().withPool(this.applyExplosionCondition(ModBlocks.SMALL_CONTAINER.get(),
                LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(ModBlocks.SMALL_CONTAINER.get()))
                        .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("LootTable", "BlockEntityTag.LootTable")
                                .copy("LootTableSeed", "BlockEntityTag.LootTableSeed")))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.REGISTRY.getEntries().stream().map(RegistryObject::get)::iterator;
    }

    public LootTable.Builder createCopyNBTDrops(Block pBlock, List<Pair<String, String>> paths) {
        var pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(pBlock));
        if (!paths.isEmpty()) {
            var copy = CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY);
            for (var path : paths) {
                copy.copy(path.getFirst(), path.getSecond());
            }
            pool.apply(copy);
        }
        return LootTable.lootTable().withPool(this.applyExplosionCondition(pBlock, pool));
    }
}
