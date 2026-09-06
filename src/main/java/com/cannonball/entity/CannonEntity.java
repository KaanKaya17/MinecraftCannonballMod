package com.cannonball.entity;

import com.cannonball.item.ModItems;
import com.cannonball.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class CannonEntity extends Entity {
    private static final EntityDataAccessor<Float> DATA_PITCH = SynchedEntityData.defineId(CannonEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_POWDER = SynchedEntityData.defineId(CannonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_LOADED = SynchedEntityData.defineId(CannonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_RAMMED = SynchedEntityData.defineId(CannonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_FUSE = SynchedEntityData.defineId(CannonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_RECOIL = SynchedEntityData.defineId(CannonEntity.class, EntityDataSerializers.INT);

    private float health = 20.0F;

    public CannonEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
        this.setMaxUpStep(0.6F); // Step over slabs and small blocks smoothly
    }

    public CannonEntity(Level level, double x, double y, double z) {
        this(ModEntities.CANNON, level);
        setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_PITCH, 0.0F);
        this.entityData.define(DATA_POWDER, 0);
        this.entityData.define(DATA_LOADED, false);
        this.entityData.define(DATA_RAMMED, false);
        this.entityData.define(DATA_FUSE, -1);
        this.entityData.define(DATA_RECOIL, 0);
    }

    public float getCannonPitch() {
        return this.entityData.get(DATA_PITCH);
    }

    public void setCannonPitch(float pitch) {
        this.entityData.set(DATA_PITCH, Mth.clamp(pitch, -55.0F, 15.0F));
    }

    public int getPowder() {
        return this.entityData.get(DATA_POWDER);
    }

    public void setPowder(int powder) {
        this.entityData.set(DATA_POWDER, Math.max(0, Math.min(3, powder)));
    }

    public boolean isLoaded() {
        return this.entityData.get(DATA_LOADED);
    }

    public void setLoaded(boolean loaded) {
        this.entityData.set(DATA_LOADED, loaded);
    }

    public boolean isRammed() {
        return this.entityData.get(DATA_RAMMED);
    }

    public void setRammed(boolean rammed) {
        this.entityData.set(DATA_RAMMED, rammed);
    }

    public int getFuseTicks() {
        return this.entityData.get(DATA_FUSE);
    }

    public void setFuseTicks(int ticks) {
        this.entityData.set(DATA_FUSE, ticks);
    }

    public int getRecoilTicks() {
        return this.entityData.get(DATA_RECOIL);
    }

    public void setRecoilTicks(int ticks) {
        this.entityData.set(DATA_RECOIL, ticks);
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        // Passenger aiming & driving handling
        Entity passenger = this.getFirstPassenger();
        if (passenger instanceof Player player) {
            this.setYRot(player.getYRot());
            this.yRotO = player.yRotO;
            float clampedPitch = Mth.clamp(player.getXRot(), -55.0F, 15.0F);
            this.setXRot(clampedPitch);
            this.xRotO = clampedPitch;
            this.setCannonPitch(clampedPitch);

            // Driving controls (W / S keys)
            float forwardInput = player.zza; // +1.0 for W, -1.0 for S
            if (forwardInput != 0.0F) {
                float yawRad = (float) Math.toRadians(player.getYRot());
                // Forward towing speed: 0.08 blocks/tick (~1.6 blocks/sec), backward: 0.045 blocks/tick
                double driveSpeed = forwardInput > 0.0F ? 0.08 : -0.045;
                double mx = -Math.sin(yawRad) * driveSpeed;
                double mz = Math.cos(yawRad) * driveSpeed;

                this.setDeltaMovement(this.getDeltaMovement().add(mx, 0.0, mz));

                // Wooden axle & wheel sound while moving on ground
                if (onGround() && tickCount % 10 == 0) {
                    level().playSound(null, getX(), getY(), getZ(), ModSounds.CANNON_AIM, SoundSource.BLOCKS, 0.7F, (float) (0.85 + random.nextDouble() * 0.3));
                }
            }
        } else if (passenger instanceof LivingEntity living) {
            this.setYRot(living.getYRot());
            this.yRotO = living.yRotO;
            float clampedPitch = Mth.clamp(living.getXRot(), -55.0F, 15.0F);
            this.setXRot(clampedPitch);
            this.xRotO = clampedPitch;
            this.setCannonPitch(clampedPitch);
        } else {
            this.setXRot(getCannonPitch());
        }

        // Gravity
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }

        this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());

        // Ground friction / deceleration
        if (onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.72, 1.0, 0.72));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.92, 1.0, 0.92));
        }

        // Fuse countdown
        int fuse = getFuseTicks();
        if (fuse > 0) {
            setFuseTicks(fuse - 1);

            // Spark particles at breech touchhole
            if (level().isClientSide) {
                Vec3 touchhole = getBreechPos();
                level().addParticle(ParticleTypes.SMOKE, touchhole.x, touchhole.y, touchhole.z, 0, 0.05, 0);
                level().addParticle(ParticleTypes.SMALL_FLAME, touchhole.x, touchhole.y, touchhole.z, 0, 0.02, 0);
            } else if (fuse % 8 == 0) {
                level().playSound(null, getX(), getY(), getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 0.8F, 1.2F);
            }

            if (fuse - 1 == 0 && !level().isClientSide) {
                fire();
            }
        }

        // Recoil recovery
        int recoil = getRecoilTicks();
        if (recoil > 0) {
            setRecoilTicks(recoil - 1);
        }

        // Redstone Trigger (Server-Side)
        if (!level().isClientSide && fuse < 0 && isRammed() && getPowder() > 0) {
            BlockPos currentPos = blockPosition();
            if (level().hasNeighborSignal(currentPos) || level().hasNeighborSignal(currentPos.below())) {
                setFuseTicks(15); // Short fuse for redstone
            }
        }
    }

    public Vec3 getLookDir() {
        return Vec3.directionFromRotation(getCannonPitch(), getYRot());
    }

    public void fire() {
        if (level().isClientSide) return;

        int powder = getPowder();
        if (powder <= 0 || !isLoaded()) {
            setFuseTicks(-1);
            return;
        }

        // Muzzle position & direction exactly matching mouse look angle
        Vec3 dir = getLookDir();
        Vec3 muzzlePos = getMuzzlePos();

        // Spawn Cannonball
        CannonballEntity cannonball = new CannonballEntity(level(), muzzlePos.x, muzzlePos.y, muzzlePos.z, powder);
        double speed = 1.4 + (powder * 1.0); // 2.4 to 4.4 blocks/tick
        cannonball.setDeltaMovement(dir.scale(speed));

        Entity passenger = getFirstPassenger();
        if (passenger != null) {
            cannonball.setOwner(passenger);
        }
        level().addFreshEntity(cannonball);

        // Sound: Thunderous cannon blast
        level().playSound(null, getX(), getY(), getZ(), ModSounds.CANNON_FIRE, SoundSource.NEUTRAL, 3.5F, (float) (0.7 + (3 - powder) * 0.1));

        // Muzzle blast FX
        if (level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, muzzlePos.x, muzzlePos.y, muzzlePos.z, 22, 0.2, 0.2, 0.2, 0.06);
            serverLevel.sendParticles(ParticleTypes.FLAME, muzzlePos.x, muzzlePos.y, muzzlePos.z, 15, 0.15, 0.15, 0.15, 0.1);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, muzzlePos.x, muzzlePos.y, muzzlePos.z, 10, 0.2, 0.2, 0.2, 0.08);
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, muzzlePos.x, muzzlePos.y, muzzlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
        }

        // Visual recoil & physical kickback
        setRecoilTicks(10);
        this.setDeltaMovement(this.getDeltaMovement().add(-dir.x * 0.25, 0.05, -dir.z * 0.25));

        // Reset state
        setPowder(0);
        setLoaded(false);
        setRammed(false);
        setFuseTicks(-1);
    }

    public Vec3 getMuzzlePos() {
        Vec3 trunnionPos = new Vec3(getX(), getY() + 0.72, getZ());
        Vec3 dir = getLookDir();
        return trunnionPos.add(dir.scale(1.7));
    }

    public Vec3 getBreechPos() {
        Vec3 trunnionPos = new Vec3(getX(), getY() + 0.72, getZ());
        Vec3 dir = getLookDir();
        return trunnionPos.subtract(dir.scale(0.55));
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        // Loading Gunpowder
        if (held.is(Items.GUNPOWDER)) {
            if (getPowder() < 3 && !isLoaded()) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                setPowder(getPowder() + 1);
                level().playSound(null, getX(), getY(), getZ(), ModSounds.CANNON_LOAD, SoundSource.PLAYERS, 1.0F, 1.0F);
                if (!level().isClientSide) {
                    player.displayClientMessage(Component.translatable("message.cannonball.powder_loaded", getPowder(), 3), true);
                }
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
        }

        // Loading Cannonball
        if (held.is(ModItems.CANNONBALL)) {
            if (getPowder() > 0 && !isLoaded()) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                setLoaded(true);
                level().playSound(null, getX(), getY(), getZ(), ModSounds.CANNON_LOAD, SoundSource.PLAYERS, 1.2F, 0.8F);
                if (!level().isClientSide) {
                    player.displayClientMessage(Component.translatable("message.cannonball.ball_loaded"), true);
                }
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
        }

        // Ramming with Ramrod
        if (held.is(ModItems.RAMROD)) {
            if (isLoaded() && !isRammed()) {
                setRammed(true);
                level().playSound(null, getX(), getY(), getZ(), ModSounds.CANNON_RAM, SoundSource.PLAYERS, 1.2F, 1.0F);
                if (!level().isClientSide) {
                    player.displayClientMessage(Component.translatable("message.cannonball.rammed"), true);
                }
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
        }

        // Lighting Fuse with Flint and Steel or Fire Charge
        if (held.is(Items.FLINT_AND_STEEL) || held.is(Items.FIRE_CHARGE)) {
            if (isLoaded() && getFuseTicks() < 0) {
                if (!isRammed()) {
                    setRammed(true); // Automatically pack if primed with fire
                }
                if (!player.getAbilities().instabuild) {
                    if (held.is(Items.FLINT_AND_STEEL)) {
                        held.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                    } else {
                        held.shrink(1);
                    }
                }
                setFuseTicks(35);
                level().playSound(null, getX(), getY(), getZ(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
        }

        // Mounting to aim
        if (!player.isPassenger() && !isVehicle()) {
            if (!level().isClientSide) {
                player.startRiding(this);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }

        return super.interact(player, hand);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;
        if (this.isRemoved()) return false;

        Entity attacker = source.getEntity();

        // Creative mode: instabreak
        if (attacker instanceof Player player && player.getAbilities().instabuild) {
            destroy(false);
            return true;
        }

        if (!level().isClientSide) {
            this.health -= amount;
            this.markHurt();

            level().playSound(null, getX(), getY(), getZ(), SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.BLOCKS, 0.9F, 1.1F);

            if (this.health <= 0.0F) {
                destroy(true);
            }
        }
        return true;
    }

    public void destroy(boolean dropItems) {
        if (this.isRemoved()) return;
        this.discard();

        if (!level().isClientSide) {
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.ANVIL_DESTROY, SoundSource.BLOCKS, 1.0F, 0.8F);

            if (level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                serverLevel.sendParticles(new net.minecraft.core.particles.BlockParticleOption(ParticleTypes.BLOCK, net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState()),
                        getX(), getY() + 0.5, getZ(), 25, 0.35, 0.35, 0.35, 0.15);
                serverLevel.sendParticles(new net.minecraft.core.particles.BlockParticleOption(ParticleTypes.BLOCK, net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState()),
                        getX(), getY() + 0.5, getZ(), 15, 0.25, 0.25, 0.25, 0.15);
            }

            if (dropItems) {
                spawnAtLocation(new ItemStack(ModItems.CANNON));
                if (getPowder() > 0) {
                    spawnAtLocation(new ItemStack(Items.GUNPOWDER, getPowder()));
                }
                if (isLoaded()) {
                    spawnAtLocation(new ItemStack(ModItems.CANNONBALL));
                }
            }
        }
    }

    public void destroyAndDrop() {
        destroy(true);
    }

    @Override
    public void positionRider(Entity passenger, MoveFunction moveFunction) {
        if (this.hasPassenger(passenger)) {
            float yawRad = (float) Math.toRadians(getYRot());
            double seatDist = 1.0;
            double x = getX() + Math.sin(yawRad) * seatDist;
            double y = getY() + 0.35;
            double z = getZ() - Math.cos(yawRad) * seatDist;
            moveFunction.accept(passenger, x, y, z);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putFloat("CannonPitch", getCannonPitch());
        compound.putInt("Powder", getPowder());
        compound.putBoolean("Loaded", isLoaded());
        compound.putBoolean("Rammed", isRammed());
        compound.putInt("FuseTicks", getFuseTicks());
        compound.putFloat("Health", health);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        setCannonPitch(compound.getFloat("CannonPitch"));
        setPowder(compound.getInt("Powder"));
        setLoaded(compound.getBoolean("Loaded"));
        setRammed(compound.getBoolean("Rammed"));
        setFuseTicks(compound.getInt("FuseTicks"));
        if (compound.contains("Health")) {
            health = compound.getFloat("Health");
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
