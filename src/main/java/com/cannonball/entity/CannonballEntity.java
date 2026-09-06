package com.cannonball.entity;

import com.cannonball.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class CannonballEntity extends Projectile {
    private int powderCharge = 2;
    private int ricochetsRemaining = 3;
    private int blocksPenetrated = 0;
    private int lifeTicks = 0;

    public float spinAngle = 0.0F;
    public float prevSpinAngle = 0.0F;

    public CannonballEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    public CannonballEntity(Level level, double x, double y, double z, int powderCharge) {
        super(ModEntities.CANNONBALL, level);
        setPos(x, y, z);
        this.powderCharge = Math.max(1, Math.min(3, powderCharge));
    }

    public int getPowderCharge() {
        return powderCharge;
    }

    public void setPowderCharge(int charge) {
        this.powderCharge = Math.max(1, Math.min(3, charge));
    }

    @Override
    protected void defineSynchedData() {
        // No custom synced data needed via entity data tracker, position and velocity are synced by network
    }

    @Override
    public void tick() {
        super.tick();
        lifeTicks++;

        if (lifeTicks > 600) { // Discard after 30 seconds
            discard();
            return;
        }

        Vec3 motion = getDeltaMovement();
        double speed = motion.length();

        // Spin calculation
        prevSpinAngle = spinAngle;
        spinAngle += (float) (speed * 18.0);

        // Visual effects in flight
        if (level().isClientSide) {
            if (speed > 0.3) {
                level().addParticle(ParticleTypes.SMOKE, getX(), getY() + 0.15, getZ(),
                        -motion.x * 0.1 + (random.nextDouble() - 0.5) * 0.05,
                        -motion.y * 0.1 + (random.nextDouble() - 0.5) * 0.05,
                        -motion.z * 0.1 + (random.nextDouble() - 0.5) * 0.05);

                if (powderCharge >= 2 && random.nextFloat() < 0.4F) {
                    level().addParticle(ParticleTypes.FLAME, getX(), getY() + 0.15, getZ(),
                            -motion.x * 0.05, -motion.y * 0.05, -motion.z * 0.05);
                }
            }
        } else {
            // Server side whistling sound
            if (speed > 1.2 && lifeTicks % 7 == 0) {
                level().playSound(null, getX(), getY(), getZ(), ModSounds.CANNONBALL_WHISTLE,
                        SoundSource.NEUTRAL, 1.2F, (float) (0.8 + speed * 0.1));
            }

            // Continuous collision check
            Vec3 pos = position();
            Vec3 nextPos = pos.add(motion);

            HitResult hitResult = findHit(pos, nextPos);
            if (hitResult.getType() != HitResult.Type.MISS) {
                onHit(hitResult);
                return;
            }

            // Apply Ballistics physics: air drag and gravity
            motion = motion.scale(0.992); // realistic drag
            motion = motion.subtract(0, 0.038, 0); // gravity
            setDeltaMovement(motion);
            setPos(pos.add(motion));
            checkInsideBlocks();
        }
    }

    private HitResult findHit(Vec3 from, Vec3 to) {
        HitResult blockHit = level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        Vec3 blockHitLoc = blockHit.getType() == HitResult.Type.MISS ? to : blockHit.getLocation();

        AABB aabb = getBoundingBox().expandTowards(to.subtract(from)).inflate(1.0);
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(level(), this, from, blockHitLoc, aabb, this::canHitEntity);

        if (entityHit != null) {
            return entityHit;
        }
        return blockHit;
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && !entity.is(this) && (lifeTicks > 2 || !entity.is(getOwner()));
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            onEntityHit((EntityHitResult) hitResult);
        } else if (hitResult.getType() == HitResult.Type.BLOCK) {
            onBlockHit((BlockHitResult) hitResult);
        }
    }

    protected void onEntityHit(EntityHitResult entityHit) {
        if (level().isClientSide) return;

        Entity target = entityHit.getEntity();
        Vec3 motion = getDeltaMovement();
        double speed = motion.length();

        DamageSource damageSource = damageSources().thrown(this, getOwner() instanceof LivingEntity ? (LivingEntity) getOwner() : null);
        float damage = (float) (speed * 14.0 * powderCharge);
        target.hurt(damageSource, damage);

        // Huge kinetic knockback
        target.push(motion.x * 0.7, motion.y * 0.3 + 0.35, motion.z * 0.7);

        // Play impact sound
        level().playSound(null, getX(), getY(), getZ(), ModSounds.CANNONBALL_IMPACT, SoundSource.NEUTRAL, 1.5F, 0.9F);

        // Explode on direct entity hit if high powder charge
        if (powderCharge >= 2 || speed > 1.5) {
            detonate();
        } else {
            // Deflect downward or stop
            setDeltaMovement(motion.scale(0.3).subtract(0, 0.2, 0));
        }
    }

    protected void onBlockHit(BlockHitResult blockHit) {
        if (level().isClientSide) return;

        BlockPos hitPos = blockHit.getBlockPos();
        BlockState state = level().getBlockState(hitPos);
        Direction face = blockHit.getDirection();
        Vec3 normal = Vec3.atLowerCornerOf(face.getNormal());

        Vec3 motion = getDeltaMovement();
        double speed = motion.length();

        float hardness = state.getDestroySpeed(level(), hitPos);
        double normalDot = motion.dot(normal);
        double cosIncident = Math.abs(normalDot) / (speed + 1e-6);

        // Ricochet Condition:
        // Hard surface (hardness >= 1.5), glancing angle (cosIncident < 0.58 => angle > 55 deg), sufficient speed
        if (hardness >= 1.5F && cosIncident < 0.58 && speed > 0.65 && ricochetsRemaining > 0) {
            // Realistic velocity reflection: v' = (v - 2*(v.n)*n) * restitution
            double restitution = 0.68;
            Vec3 reflected = motion.subtract(normal.scale(2.0 * normalDot)).scale(restitution);
            setDeltaMovement(reflected);

            ricochetsRemaining--;

            // Move slightly off the surface to avoid immediate re-collision
            setPos(blockHit.getLocation().add(normal.scale(0.12)));

            // Ricochet Sound & FX
            level().playSound(null, getX(), getY(), getZ(), ModSounds.CANNONBALL_RICOCHET, SoundSource.NEUTRAL, 1.6F, (float) (1.4 + random.nextDouble() * 0.4));
            if (level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CRIT, blockHit.getLocation().x, blockHit.getLocation().y, blockHit.getLocation().z, 14, 0.1, 0.1, 0.1, 0.25);
                serverLevel.sendParticles(ParticleTypes.SMOKE, blockHit.getLocation().x, blockHit.getLocation().y, blockHit.getLocation().z, 8, 0.1, 0.1, 0.1, 0.05);
            }
            return;
        }

        // Penetration Condition:
        // Destructible block (hardness < 3.0), high kinetic energy, penetration quota
        if (hardness >= 0.0F && hardness < 2.5F && speed > 1.0 && blocksPenetrated < (powderCharge * 2)) {
            level().destroyBlock(hitPos, true, this);
            blocksPenetrated++;

            if (level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                serverLevel.sendParticles(new net.minecraft.core.particles.BlockParticleOption(ParticleTypes.BLOCK, state),
                        hitPos.getX() + 0.5, hitPos.getY() + 0.5, hitPos.getZ() + 0.5, 20, 0.25, 0.25, 0.25, 0.15);
            }

            // Retain forward momentum with slight dampening
            setDeltaMovement(motion.scale(0.72));
            setPos(blockHit.getLocation().add(motion.normalize().scale(0.25)));

            level().playSound(null, hitPos, ModSounds.CANNONBALL_IMPACT, SoundSource.BLOCKS, 1.2F, 0.8F);
            return;
        }

        // Direct impact / Detonation
        detonate();
    }

    private void detonate() {
        if (!isAlive()) return;

        float explosionPower = (float) (1.2 + powderCharge * 0.9);
        level().explode(this, getX(), getY(), getZ(), explosionPower, Level.ExplosionInteraction.MOB);

        level().playSound(null, getX(), getY(), getZ(), ModSounds.CANNONBALL_IMPACT, SoundSource.NEUTRAL, 1.8F, 0.8F);
        discard();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("PowderCharge", powderCharge);
        compound.putInt("RicochetsRemaining", ricochetsRemaining);
        compound.putInt("BlocksPenetrated", blocksPenetrated);
        compound.putInt("LifeTicks", lifeTicks);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        powderCharge = compound.getInt("PowderCharge");
        ricochetsRemaining = compound.getInt("RicochetsRemaining");
        blocksPenetrated = compound.getInt("BlocksPenetrated");
        lifeTicks = compound.getInt("LifeTicks");
    }
}
