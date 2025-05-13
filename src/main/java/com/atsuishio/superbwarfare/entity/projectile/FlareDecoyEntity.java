package com.atsuishio.superbwarfare.entity.projectile;

import com.atsuishio.superbwarfare.init.ModEntities;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class FlareDecoyEntity extends Entity implements DecoyEntity {

    public FlareDecoyEntity(EntityType<? extends FlareDecoyEntity> type, Level world) {
        super(type, world);
    }

    public FlareDecoyEntity(LivingEntity entity, Level level) {
        super(ModEntities.FLARE_DECOY.get(), level);
    }

    public FlareDecoyEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ModEntities.FLARE_DECOY.get(), level);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();
        this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.02, 0.0));
        this.move(MoverType.SELF, this.getDeltaMovement());
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            ParticleTool.sendParticle(serverLevel, ParticleTypes.END_ROD, this.xo, this.yo, this.zo,
                    1, 0, 0, 0, 0.02, true);
            ParticleTool.sendParticle(serverLevel, ParticleTypes.CLOUD, this.xo, this.yo, this.zo,
                    1, 0, 0, 0, 0.02, true);
        }
        if (this.tickCount > 200 || this.isInWater() || this.onGround()) {
            this.discard();
        }
    }

    public void decoyShoot(Entity entity, Vec3 shootVec, float pVelocity, float pInaccuracy) {
        Vec3 vec3 = shootVec.normalize().add(this.random.triangle(0.0, 0.0172275 * (double) pInaccuracy), this.random.triangle(0.0, 0.0172275 * (double) pInaccuracy), this.random.triangle(0.0, 0.0172275 * (double) pInaccuracy)).scale((double) pVelocity);
        this.setDeltaMovement(entity.getDeltaMovement().scale(0.75).add(vec3));
        double d0 = vec3.horizontalDistance();
        this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * 57.2957763671875));
        this.setXRot((float) (Mth.atan2(vec3.y, d0) * 57.2957763671875));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    public String getDecoyUUID() {
        return this.getStringUUID();
    }

    @Override
    public Vec3 getPosition() {
        return position();
    }
}
