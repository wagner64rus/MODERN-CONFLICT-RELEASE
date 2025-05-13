package com.atsuishio.superbwarfare.item.gun.machinegun;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.client.PoseTool;
import com.atsuishio.superbwarfare.client.renderer.item.M60ItemRenderer;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.item.gun.GunItem;
import com.atsuishio.superbwarfare.item.gun.data.GunData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class M60Item extends GunItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static ItemDisplayContext transformType;

    public M60Item() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new M60ItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }

            @Override
            public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack stack) {
                return PoseTool.pose(entityLiving, hand, stack);
            }
        });
    }

    public void getTransformType(ItemDisplayContext type) {
        transformType = type;
    }

    private PlayState fireAnimPredicate(AnimationState<M60Item> event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return PlayState.STOP;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof GunItem)) return PlayState.STOP;

        if (ClientEventHandler.firePosTimer > 0 && ClientEventHandler.firePosTimer < 0.45) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.m60.fire"));
        }

        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.m60.idle"));
    }

    private PlayState idlePredicate(AnimationState<M60Item> event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return PlayState.STOP;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof GunItem)) return PlayState.STOP;

        if (GunData.from(stack).reload.empty()) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.m60.reload"));
        }

        if (GunData.from(stack).reload.normal()) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.m60.reload2"));
        }

        if (player.isSprinting() && player.onGround() && ClientEventHandler.cantSprint == 0 && ClientEventHandler.drawTime < 0.01) {
            if (ClientEventHandler.tacticalSprint) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.m60.run_fast"));
            } else {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.m60.run"));
            }
        }

        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.m60.idle"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        var fireAnimController = new AnimationController<>(this, "fireAnimController", 0, this::fireAnimPredicate);
        data.add(fireAnimController);
        var idleController = new AnimationController<>(this, "idleController", 4, this::idlePredicate);
        data.add(idleController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public Set<SoundEvent> getReloadSound() {
        return Set.of(ModSounds.M_60_RELOAD_EMPTY.get(), ModSounds.M_60_RELOAD_NORMAL.get());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        var data = GunData.from(stack);
        if (data.draw.get()) {
            data.draw.set(false);
            if (data.ammo.get() <= 5) {
                data.hideBulletChain.set(true);
            }
        }
        super.inventoryTick(stack, level, entity, slot, selected);
    }

    @Override
    public ResourceLocation getGunIcon() {
        return Mod.loc("textures/gun_icon/m60_icon.png");
    }

    @Override
    public String getGunDisplayName() {
        return "M60";
    }

    @Override
    public boolean isOpenBolt(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canEjectShell(ItemStack stack) {
        return true;
    }

    @Override
    public void beforeShoot(GunData data, Player player, double spread, boolean zoom) {
        super.beforeShoot(data, player, spread, zoom);

        if (data.ammo.get() <= 5) {
            data.hideBulletChain.set(true);
        }
    }

    @Override
    public void addReloadTimeBehavior(Map<Integer, Consumer<GunData>> behaviors) {
        super.addReloadTimeBehavior(behaviors);

        behaviors.put(55, data -> data.hideBulletChain.reset());
    }
}