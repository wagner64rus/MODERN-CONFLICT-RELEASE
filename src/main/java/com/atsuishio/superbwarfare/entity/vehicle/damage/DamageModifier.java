package com.atsuishio.superbwarfare.entity.vehicle.damage;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DamageModifier {

    private final List<DamageModify> immuneList = new ArrayList<>();
    private final List<DamageModify> reduceList = new ArrayList<>();
    private final List<DamageModify> multiplyList = new ArrayList<>();
    private final List<BiFunction<DamageSource, Float, Float>> customList = new ArrayList<>();

    /**
     * 免疫所有伤害
     */
    public DamageModifier immuneTo() {
        immuneList.add(new DamageModify(DamageModify.ModifyType.IMMUNITY, 0));
        return this;
    }

    /**
     * 免疫指定类型的伤害
     *
     * @param sourceTagKey 伤害类型
     */
    public DamageModifier immuneTo(TagKey<DamageType> sourceTagKey) {
        immuneList.add(new DamageModify(DamageModify.ModifyType.IMMUNITY, 0, sourceTagKey));
        return this;
    }

    /**
     * 免疫指定类型的伤害
     *
     * @param sourceKey 伤害类型
     */
    public DamageModifier immuneTo(ResourceKey<DamageType> sourceKey) {
        immuneList.add(new DamageModify(DamageModify.ModifyType.IMMUNITY, 0, sourceKey));
        return this;
    }

    /**
     * 免疫指定类型的伤害
     *
     * @param condition 伤害来源判定条件
     */
    public DamageModifier immuneTo(Function<DamageSource, Boolean> condition) {
        immuneList.add(new DamageModify(DamageModify.ModifyType.IMMUNITY, 0, condition));
        return this;
    }

    /**
     * 固定减少所有伤害一定数值
     *
     * @param value 要减少的数值
     */
    public DamageModifier reduce(float value) {
        reduceList.add(new DamageModify(DamageModify.ModifyType.REDUCE, value));
        return this;
    }

    /**
     * 固定减少指定类型的伤害一定数值
     *
     * @param value        要减少的数值
     * @param sourceTagKey 伤害类型
     */
    public DamageModifier reduce(float value, TagKey<DamageType> sourceTagKey) {
        reduceList.add(new DamageModify(DamageModify.ModifyType.REDUCE, value, sourceTagKey));
        return this;
    }

    /**
     * 固定减少指定类型的伤害一定数值
     *
     * @param value     要减少的数值
     * @param sourceKey 伤害类型
     */
    public DamageModifier reduce(float value, ResourceKey<DamageType> sourceKey) {
        reduceList.add(new DamageModify(DamageModify.ModifyType.REDUCE, value, sourceKey));
        return this;
    }

    /**
     * 固定减少指定类型的伤害一定数值
     *
     * @param value     要减少的数值
     * @param condition 伤害来源判定条件
     */
    public DamageModifier reduce(float value, Function<DamageSource, Boolean> condition) {
        reduceList.add(new DamageModify(DamageModify.ModifyType.REDUCE, value, condition));
        return this;
    }

    /**
     * 将所有类型的伤害值乘以指定数值
     *
     * @param value 要乘以的数值
     */
    public DamageModifier multiply(float value) {
        multiplyList.add(new DamageModify(DamageModify.ModifyType.MULTIPLY, value));
        return this;
    }

    /**
     * 将指定类型的伤害值乘以指定数值
     *
     * @param value        要乘以的数值
     * @param sourceTagKey 伤害类型
     */
    public DamageModifier multiply(float value, TagKey<DamageType> sourceTagKey) {
        multiplyList.add(new DamageModify(DamageModify.ModifyType.MULTIPLY, value, sourceTagKey));
        return this;
    }

    /**
     * 将指定类型的伤害值乘以指定数值
     *
     * @param value     要乘以的数值
     * @param sourceKey 伤害类型
     */
    public DamageModifier multiply(float value, ResourceKey<DamageType> sourceKey) {
        multiplyList.add(new DamageModify(DamageModify.ModifyType.MULTIPLY, value, sourceKey));
        return this;
    }

    /**
     * 将指定类型的伤害值乘以指定数值
     *
     * @param value     要乘以的数值
     * @param condition 伤害来源判定条件
     */
    public DamageModifier multiply(float value, Function<DamageSource, Boolean> condition) {
        multiplyList.add(new DamageModify(DamageModify.ModifyType.MULTIPLY, value, condition));
        return this;
    }

    /**
     * 自定义伤害值计算
     *
     * @param damageModifyFunction 自定义伤害值计算函数
     */
    public DamageModifier custom(BiFunction<DamageSource, Float, Float> damageModifyFunction) {
        customList.add(damageModifyFunction);
        return this;
    }

    private final List<DamageModify> combinedList = new ArrayList<>();

    /**
     * 计算减伤后的伤害值
     *
     * @param source 伤害来源
     * @param damage 原伤害值
     * @return 减伤后的伤害值
     */
    public float compute(DamageSource source, float damage) {
        if (combinedList.isEmpty()) {
            // 计算优先级 免疫 > 固定减伤 > 乘
            combinedList.addAll(immuneList);
            combinedList.addAll(reduceList);
            combinedList.addAll(multiplyList);
        }

        for (DamageModify damageModify : combinedList) {
            if (damageModify.match(source)) {
                damage = damageModify.compute(damage);

                if (damage <= 0) return 0;
            }
        }

        // 最后计算自定义伤害
        for (var func : customList) {
            damage = func.apply(source, damage);
        }

        return damage;
    }
}
