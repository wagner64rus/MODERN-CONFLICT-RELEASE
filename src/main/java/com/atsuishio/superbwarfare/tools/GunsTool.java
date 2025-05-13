package com.atsuishio.superbwarfare.tools;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.item.gun.data.DefaultGunData;
import com.atsuishio.superbwarfare.network.message.receive.GunsDataMessage;
import com.google.gson.Gson;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.UUID;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = Mod.MODID)
public class GunsTool {

    public static HashMap<String, DefaultGunData> gunsData = new HashMap<>();

    /**
     * 初始化数据，从data中读取数据json文件
     */
    public static void initJsonData(ResourceManager manager) {
        for (var entry : manager.listResources("guns", file -> file.getPath().endsWith(".json")).entrySet()) {
            var id = entry.getKey();
            var attribute = entry.getValue();
            try {
                Gson gson = new Gson();
                var data = gson.fromJson(new InputStreamReader(attribute.open()), DefaultGunData.class);
                var path = id.getPath();

                gunsData.put(path.substring(5, path.length() - 5), data);
            } catch (Exception e) {
                Mod.LOGGER.error(e.getMessage());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Mod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), GunsDataMessage.create());
        }
    }

    @SubscribeEvent
    public static void serverStarted(ServerStartedEvent event) {
        initJsonData(event.getServer().getResourceManager());
    }

    @SubscribeEvent
    public static void onDataPackSync(OnDatapackSyncEvent event) {
        initJsonData(event.getPlayerList().getServer().getResourceManager());

        event.getPlayerList().getPlayers().forEach(player -> Mod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), GunsDataMessage.create()));
    }

    public static void setGunIntTag(ItemStack stack, String name, int num) {
        CompoundTag tag = stack.getOrCreateTag();
        var data = tag.getCompound("GunData");
        data.putInt(name, num);
        stack.addTagElement("GunData", data);
    }

    public static int getGunIntTag(final CompoundTag tag, String name) {
        return getGunIntTag(tag, name, 0);
    }

    public static int getGunIntTag(final CompoundTag tag, String name, int defaultValue) {
        var data = tag.getCompound("GunData");
        if (!data.contains(name)) return defaultValue;
        return data.getInt(name);
    }

    public static double getGunDoubleTag(ItemStack stack, String name) {
        return getGunDoubleTag(stack, name, 0);
    }

    public static double getGunDoubleTag(ItemStack stack, String name, double defaultValue) {
        var data = stack.getOrCreateTag().getCompound("GunData");
        if (!data.contains(name)) return defaultValue;
        return data.getDouble(name);
    }

    @Nullable
    public static UUID getGunUUID(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("GunData")) return null;

        CompoundTag data = tag.getCompound("GunData");
        if (!data.hasUUID("UUID")) return null;
        return data.getUUID("UUID");
    }
}
