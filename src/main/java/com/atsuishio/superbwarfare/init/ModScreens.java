package com.atsuishio.superbwarfare.init;

import com.atsuishio.superbwarfare.client.screens.ChargingStationScreen;
import com.atsuishio.superbwarfare.client.screens.FuMO25Screen;
import com.atsuishio.superbwarfare.client.screens.ReforgingTableScreen;
import com.atsuishio.superbwarfare.client.screens.VehicleScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModScreens {

    @SubscribeEvent
    public static void clientLoad(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.REFORGING_TABLE_MENU.get(), ReforgingTableScreen::new);
            MenuScreens.register(ModMenuTypes.CHARGING_STATION_MENU.get(), ChargingStationScreen::new);
            MenuScreens.register(ModMenuTypes.VEHICLE_MENU.get(), VehicleScreen::new);
            MenuScreens.register(ModMenuTypes.FUMO_25_MENU.get(), FuMO25Screen::new);
        });
    }
}
