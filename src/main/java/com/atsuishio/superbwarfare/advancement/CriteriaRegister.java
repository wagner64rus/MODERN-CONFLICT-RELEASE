package com.atsuishio.superbwarfare.advancement;

import com.atsuishio.superbwarfare.advancement.criteria.RPGMeleeExplosionTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CriteriaRegister {
    public static RPGMeleeExplosionTrigger RPG_MELEE_EXPLOSION;

    @SubscribeEvent
    public static void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            RPG_MELEE_EXPLOSION = register(new RPGMeleeExplosionTrigger());
        });
    }

    public static <T extends SimpleCriterionTrigger<?>> T register(T criterion) {
        CriteriaTriggers.register(criterion);
        return criterion;
    }
}
