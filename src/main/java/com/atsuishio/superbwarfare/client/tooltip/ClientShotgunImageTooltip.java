package com.atsuishio.superbwarfare.client.tooltip;

import com.atsuishio.superbwarfare.client.tooltip.component.GunImageComponent;
import com.atsuishio.superbwarfare.item.gun.data.GunData;
import com.atsuishio.superbwarfare.perk.AmmoPerk;
import com.atsuishio.superbwarfare.perk.Perk;
import com.atsuishio.superbwarfare.tools.FormatTool;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ClientShotgunImageTooltip extends ClientGunImageTooltip {

    public ClientShotgunImageTooltip(GunImageComponent tooltip) {
        super(tooltip);
    }

    @Override
    protected Component getDamageComponent() {
        boolean slug = false;

        var data = GunData.from(stack);
        var perk = data.perk.get(Perk.Type.AMMO);
        if (perk instanceof AmmoPerk ammoPerk && ammoPerk.slug) {
            slug = true;
        }

        if (slug) {
            double damage = getGunData().damage() * getGunData().projectileAmount();
            double extraDamage = -1;
            for (var type : Perk.Type.values()) {
                var instance = getGunData().perk.getInstance(type);
                if (instance != null) {
                    damage = instance.perk().getDisplayDamage(damage, getGunData(), instance);
                    if (instance.perk().getExtraDisplayDamage(damage, getGunData(), instance) >= 0) {
                        extraDamage = instance.perk().getExtraDisplayDamage(damage, getGunData(), instance);
                    }
                }
            }

            return Component.translatable("des.superbwarfare.guns.damage").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("").withStyle(ChatFormatting.RESET))
                    .append(Component.literal(FormatTool.format1D(damage) + (extraDamage >= 0 ? " + " + FormatTool.format1D(extraDamage) : "")).withStyle(ChatFormatting.GREEN));
        } else {
            double damage = getGunData().damage();
            double extraDamage = -1;
            for (var type : Perk.Type.values()) {
                var instance = getGunData().perk.getInstance(type);
                if (instance != null) {
                    damage = instance.perk().getDisplayDamage(damage, getGunData(), instance);
                    if (instance.perk().getExtraDisplayDamage(damage, getGunData(), instance) >= 0) {
                        extraDamage = instance.perk().getExtraDisplayDamage(damage, getGunData(), instance);
                    }
                }
            }

            return Component.translatable("des.superbwarfare.guns.damage").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("").withStyle(ChatFormatting.RESET))
                    .append(Component.literal(extraDamage >= 0 ?
                            ("(" + FormatTool.format1D(damage) + " + " + FormatTool.format1D(extraDamage) + ") * " + FormatTool.format0D(getGunData().projectileAmount()))
                            : FormatTool.format1D(damage, " * ") + FormatTool.format0D(getGunData().projectileAmount())
                    ).withStyle(ChatFormatting.GREEN));
        }
    }
}
