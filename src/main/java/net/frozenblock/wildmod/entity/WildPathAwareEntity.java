package net.frozenblock.wildmod.entity;

import net.frozenblock.wildmod.event.EntityGameEventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.function.BiConsumer;

public abstract class WildPathAwareEntity extends PathAwareEntity {
    protected static final float DEFAULT_PATHFINDING_FAVOR = 0.0F;
    private static final Vec3i ITEM_PICK_UP_RANGE_EXPANDER = new Vec3i(1, 0, 1);

    protected WildPathAwareEntity(EntityType<? extends WildPathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public Vec3i getItemPickUpRangeExpander() {
        return ITEM_PICK_UP_RANGE_EXPANDER;
    }

    public float getPathfindingFavor(BlockPos pos) {
        return this.getPathfindingFavor(pos, this.world);
    }

    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return 0.0F;
    }

    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
        return this.getPathfindingFavor(this.getBlockPos(), world) >= 0.0F;
    }

    public boolean isNavigating() {
        return !this.getNavigation().isIdle();
    }

    protected void updateLeash() {
        super.updateLeash();
        Entity entity = this.getHoldingEntity();
        if (entity != null && entity.world == this.world) {
            this.setPositionTarget(entity.getBlockPos(), 5);
            float f = this.distanceTo(entity);

            this.updateForLeashLength(f);
            if (f > 10.0F) {
                this.detachLeash(true, true);
                this.goalSelector.disableControl(Goal.Control.MOVE);
            } else if (f > 6.0F) {
                double d = (entity.getX() - this.getX()) / (double) f;
                double e = (entity.getY() - this.getY()) / (double) f;
                double g = (entity.getZ() - this.getZ()) / (double) f;
                this.setVelocity(this.getVelocity().add(Math.copySign(d * d * 0.4, d), Math.copySign(e * e * 0.4, e), Math.copySign(g * g * 0.4, g)));
            } else if (this.method_43689()) {
                this.goalSelector.enableControl(Goal.Control.MOVE);
                float h = 2.0F;
                Vec3d vec3d = new Vec3d(entity.getX() - this.getX(), entity.getY() - this.getY(), entity.getZ() - this.getZ())
                        .normalize()
                        .multiply(Math.max(f - 2.0F, 0.0F));
                this.getNavigation().startMovingTo(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z, this.getRunFromLeashSpeed());
            }
        }

    }

    protected boolean method_43689() {
        return true;
    }

    protected double getRunFromLeashSpeed() {
        return 1.0;
    }

    protected void updateForLeashLength(float leashLength) {
    }

    public boolean disablesShield() {
        return this.getMainHandStack().getItem() instanceof AxeItem;
    }

    public void updateEventHandler(BiConsumer<EntityGameEventHandler<?>, ServerWorld> callback) {
    }
}
