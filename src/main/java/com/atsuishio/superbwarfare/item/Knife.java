package com.atsuishio.superbwarfare.item;

import com.atsuishio.superbwarfare.init.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class Knife extends SwordItem {
    public Knife() {
        super(new Tier() {
            public int getUses() {
                return 1500;
            }

            public float getSpeed() {
                return 7f;
            }

            public float getAttackDamageBonus() {
                return 2.5f;
            }

            public int getLevel() {
                return 2;
            }

            public int getEnchantmentValue() {
                return 2;
            }

            public Ingredient getRepairIngredient() {
                return Ingredient.of(new ItemStack(ModItems.STEEL_INGOT.get()));
            }
        }, 3, -1.8f, new Item.Properties());
    }
}
