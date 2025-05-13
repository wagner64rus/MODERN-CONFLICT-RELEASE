package com.atsuishio.superbwarfare.network.message.send;

import com.atsuishio.superbwarfare.tools.EntityFindUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MeleeAttackMessage {

    private final UUID uuid;

    public MeleeAttackMessage(UUID uuid) {
        this.uuid = uuid;
    }

    public static MeleeAttackMessage decode(FriendlyByteBuf buffer) {
        return new MeleeAttackMessage(buffer.readUUID());
    }

    public static void encode(MeleeAttackMessage message, FriendlyByteBuf buffer) {
        buffer.writeUUID(message.uuid);
    }

    public static void handler(MeleeAttackMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getSender() != null) {
                Player player = context.getSender();

                Entity lookingEntity = EntityFindUtil.findEntity(player.level(), String.valueOf(message.uuid));
                if (lookingEntity != null) {
                    player.attack(lookingEntity);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
