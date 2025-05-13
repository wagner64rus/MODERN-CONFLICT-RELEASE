package com.atsuishio.superbwarfare.network.message.send;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.SoundTool;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ZoomMessage {

    private final int type;

    public ZoomMessage(int type) {
        this.type = type;
    }

    public static ZoomMessage decode(FriendlyByteBuf buffer) {
        return new ZoomMessage(buffer.readInt());
    }

    public static void encode(ZoomMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.type);
    }

    public static void handler(ZoomMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            var vehicle = player.getVehicle();
            // 缩放音效播放条件: 载具是武器载具，且该位置有可用武器

            if (message.type == 0) {

                if (player.isPassenger()
                        && vehicle instanceof WeaponVehicleEntity weaponEntity
                        && vehicle instanceof VehicleEntity vehicleEntity
                        && weaponEntity.hasWeapon(vehicleEntity.getSeatIndex(player))
                        && weaponEntity.banHand(player)
                ) {
                    SoundTool.playLocalSound(player, ModSounds.CANNON_ZOOM_IN.get(), 2, 1);
                }

            }

            if (message.type == 1) {
                if (player.isPassenger()
                        && vehicle instanceof WeaponVehicleEntity weaponEntity
                        && vehicle instanceof VehicleEntity vehicleEntity
                        && weaponEntity.hasWeapon(vehicleEntity.getSeatIndex(player))
                        && weaponEntity.banHand(player)
                ) {
                    SoundTool.playLocalSound(player, ModSounds.CANNON_ZOOM_OUT.get(), 2, 1);
                }

                if (player.getMainHandItem().getItem() == ModItems.JAVELIN.get()) {
                    var handItem = player.getMainHandItem();
                    var tag = handItem.getOrCreateTag();
                    tag.putBoolean("Seeking", false);
                    tag.putInt("SeekTime", 0);
                    tag.putString("TargetEntity", "none");
                    var clientboundstopsoundpacket = new ClientboundStopSoundPacket(new ResourceLocation(Mod.MODID, "javelin_lock"), SoundSource.PLAYERS);
                    player.connection.send(clientboundstopsoundpacket);
                }
            }
        });
        context.setPacketHandled(true);
    }

}
