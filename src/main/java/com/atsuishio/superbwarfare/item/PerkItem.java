package com.atsuishio.superbwarfare.item;

import com.atsuishio.superbwarfare.perk.AmmoPerk;
import com.atsuishio.superbwarfare.perk.Perk;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class PerkItem extends Item {
    private final Supplier<Perk> perk;

    public PerkItem(Supplier<Perk> perk) {
        super(new Properties());
        this.perk = perk;
    }

    public PerkItem(Supplier<Perk> perk, Rarity rarity) {
        super(new Properties().rarity(rarity));
        this.perk = perk;
    }

    public Perk getPerk() {
        return this.perk.get();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltips, TooltipFlag isAdvanced) {
        ChatFormatting chatFormatting = switch (this.getPerk().type) {
            case AMMO -> ChatFormatting.YELLOW;
            case FUNCTIONAL -> ChatFormatting.GREEN;
            case DAMAGE -> ChatFormatting.RED;
        };

        tooltips.add(Component.translatable("des.superbwarfare." + this.getPerk().descriptionId).withStyle(ChatFormatting.GRAY));
        tooltips.add(Component.literal(""));
        tooltips.add(Component.translatable("perk.superbwarfare.slot").withStyle(ChatFormatting.GOLD)
                .append(Component.translatable("perk.superbwarfare.slot_" + this.getPerk().type.getName()).withStyle(chatFormatting)));
        if (this.getPerk() instanceof AmmoPerk ammoPerk) {
            if (ammoPerk.damageRate < 1) {
                tooltips.add(Component.translatable("des.superbwarfare.perk_damage_reduce").withStyle(ChatFormatting.RED));
            } else if (ammoPerk.damageRate > 1) {
                tooltips.add(Component.translatable("des.superbwarfare.perk_damage_plus").withStyle(ChatFormatting.GREEN));
            }

            if (ammoPerk.speedRate < 1) {
                tooltips.add(Component.translatable("des.superbwarfare.perk_speed_reduce").withStyle(ChatFormatting.RED));
            } else if (ammoPerk.speedRate > 1) {
                tooltips.add(Component.translatable("des.superbwarfare.perk_speed_plus").withStyle(ChatFormatting.GREEN));
            }

            if (ammoPerk.slug) {
                tooltips.add(Component.translatable("des.superbwarfare.perk_slug").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
