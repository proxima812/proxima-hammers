package io.github.proxima812.proximahammers;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class HammerPowers {
    private static final double MAGNETURE_RADIUS = 9.0D;
    private static final int THOR_DOUBLE_JUMP_WINDOW_TICKS = 8;
    private static final int THOR_BOOST_TICKS = 34;
    private static final double THOR_LAUNCH_SPEED = 4.85D;
    private static final double THOR_BOOST_ACCELERATION = 0.32D;
    private static final double THOR_MAX_BOOST_SPEED = 5.6D;
    private static final double THOR_MIN_LAUNCH_UPWARD_SPEED = 2.75D;
    private static final double THOR_CRUISE_LIFT = 0.055D;
    private static final double THOR_MAX_FALL_SPEED = -0.08D;
    private static final Map<UUID, ThorState> THOR_STATES = new HashMap<>();

    private HammerPowers() {}

    public static void tick(ServerPlayer player) {
        if (hasHeldModule(player, HammerModule.MAGNETURE)) {
            pullNearbyItems(player);
        }

        if (hasHeldModule(player, HammerModule.THOR)) {
            tickThor(player);
        } else {
            stopThor(player);
        }
    }

    private static boolean hasHeldModule(ServerPlayer player, HammerModule module) {
        return hasModule(player.getMainHandItem(), module) || hasModule(player.getOffhandItem(), module);
    }

    public static boolean hasHeldThorHammer(ServerPlayer player) {
        return hasHeldModule(player, HammerModule.THOR);
    }

    public static boolean blocksThorImpactDamage(ServerPlayer player, DamageSource damageSource) {
        return hasHeldThorHammer(player)
                && (damageSource.is(DamageTypeTags.IS_FALL) || damageSource.is(DamageTypes.FLY_INTO_WALL));
    }

    public static void setJumping(ServerPlayer player, boolean jumping) {
        THOR_STATES.computeIfAbsent(player.getUUID(), ignored -> new ThorState()).jumping = jumping;
    }

    private static boolean hasModule(ItemStack stack, HammerModule module) {
        return stack.getItem() instanceof HammerItem && HammerModuleData.has(stack, module);
    }

    private static void pullNearbyItems(ServerPlayer player) {
        AABB box = AABB.ofSize(player.position(), MAGNETURE_RADIUS * 2.0D, MAGNETURE_RADIUS * 2.0D, MAGNETURE_RADIUS * 2.0D);
        for (var entity : player.level().getEntities(player, box, entity -> entity instanceof ItemEntity)) {
            ItemEntity itemEntity = (ItemEntity) entity;
            if (itemEntity.hasPickUpDelay()) {
                itemEntity.setNoPickUpDelay();
            }

            ItemStack stack = itemEntity.getItem().copy();
            if (player.addItem(stack)) {
                itemEntity.discard();
            } else {
                Vec3 direction = player.position().add(0.0D, 0.75D, 0.0D).subtract(itemEntity.position()).normalize();
                itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().scale(0.7D).add(direction.scale(0.18D)));
            }
        }
    }

    private static void tickThor(ServerPlayer player) {
        ThorState state = THOR_STATES.computeIfAbsent(player.getUUID(), ignored -> new ThorState());
        state.age++;

        boolean jump = state.jumping;
        if (jump && !state.wasJumping) {
            if (state.age - state.lastJumpTick <= THOR_DOUBLE_JUMP_WINDOW_TICKS) {
                launchThor(player, state);
            }
            state.lastJumpTick = state.age;
        }
        state.wasJumping = jump;

        if (state.active) {
            if (player.onGround() || player.isPassenger() || player.isSwimming() || player.isSpectator() || player.getAbilities().flying) {
                state.active = false;
                state.boostTicks = 0;
                return;
            }

            glide(player, state);
            spawnThorParticles(player, state.boostTicks > 0);
        }
    }

    private static void launchThor(ServerPlayer player, ThorState state) {
        if (player.isPassenger() || player.isSwimming() || player.isSpectator() || player.getAbilities().flying) {
            return;
        }

        state.active = true;
        state.boostTicks = THOR_BOOST_TICKS;

        Vec3 launch = player.getLookAngle().normalize().scale(THOR_LAUNCH_SPEED);
        if (launch.y < THOR_MIN_LAUNCH_UPWARD_SPEED) {
            launch = new Vec3(launch.x, THOR_MIN_LAUNCH_UPWARD_SPEED, launch.z);
        }

        state.cruiseSpeed = Math.max(horizontalSpeed(launch), THOR_LAUNCH_SPEED * 0.82D);
        player.setDeltaMovement(launch);
        syncVelocity(player, launch);
        player.resetFallDistance();
        spawnThorParticles(player, true);
    }

    private static void glide(ServerPlayer player, ThorState state) {
        player.startFallFlying();
        player.resetFallDistance();

        Vec3 look = player.getLookAngle();
        Vec3 current = player.getDeltaMovement();
        Vec3 horizontalLook = new Vec3(look.x, 0.0D, look.z);
        if (horizontalLook.lengthSqr() < 0.0001D) {
            horizontalLook = new Vec3(current.x, 0.0D, current.z);
        }
        if (horizontalLook.lengthSqr() < 0.0001D) {
            horizontalLook = new Vec3(0.0D, 0.0D, 1.0D);
        }

        double cruiseSpeed = Math.max(state.cruiseSpeed, THOR_LAUNCH_SPEED * 0.82D);
        Vec3 targetHorizontal = horizontalLook.normalize().scale(cruiseSpeed);
        double turnRate = state.boostTicks > 0 ? 0.34D : 0.18D;

        double vertical = Math.max(current.y * 0.82D, THOR_MAX_FALL_SPEED);
        if (look.y > 0.0D) {
            vertical += look.y * THOR_CRUISE_LIFT;
        }
        if (player.isShiftKeyDown()) {
            vertical -= 0.08D;
        }

        Vec3 next = new Vec3(
                current.x + (targetHorizontal.x - current.x) * turnRate,
                vertical,
                current.z + (targetHorizontal.z - current.z) * turnRate
        );

        if (state.boostTicks > 0) {
            Vec3 boost = look.normalize().scale(THOR_BOOST_ACCELERATION);
            next = next.add(boost);
            double speed = next.length();
            if (speed > THOR_MAX_BOOST_SPEED) {
                next = next.normalize().scale(THOR_MAX_BOOST_SPEED);
            }
            state.cruiseSpeed = Math.max(state.cruiseSpeed, horizontalSpeed(next));
            state.boostTicks--;
        }

        player.setDeltaMovement(next);
        if (state.boostTicks > 0 || state.age % 10 == 0) {
            syncVelocity(player, next);
        }
    }

    private static double horizontalSpeed(Vec3 velocity) {
        return Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
    }

    private static void syncVelocity(ServerPlayer player, Vec3 velocity) {
        player.connection.send(new ClientboundSetEntityMotionPacket(player.getId(), velocity));
    }

    private static void stopThor(ServerPlayer player) {
        ThorState state = THOR_STATES.remove(player.getUUID());
        if (state != null && state.active) {
            player.stopFallFlying();
        }
    }

    private static void spawnThorParticles(ServerPlayer player, boolean burst) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        Vec3 look = player.getLookAngle();
        Vec3 center = player.position().add(look.scale(0.75D)).add(0.0D, 1.0D, 0.0D);
        level.sendParticles(ParticleTypes.END_ROD, center.x, center.y, center.z, burst ? 16 : 4, 0.28D, 0.32D, 0.28D, burst ? 0.05D : 0.015D);
        level.sendParticles(ParticleTypes.WHITE_ASH, center.x, center.y, center.z, burst ? 18 : 5, 0.36D, 0.24D, 0.36D, 0.02D);
    }

    private static final class ThorState {
        private int age;
        private int lastJumpTick = -THOR_DOUBLE_JUMP_WINDOW_TICKS;
        private int boostTicks;
        private double cruiseSpeed;
        private boolean jumping;
        private boolean wasJumping;
        private boolean active;
    }
}
