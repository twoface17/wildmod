package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.entity.FrogEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class FrogWanderInWaterGoal extends MoveToTargetPosGoal {
    private static final int field_30385 = 1200;
    private final FrogEntity frog;

    public FrogWanderInWaterGoal(FrogEntity frog, double speed) {
        super(frog, frog.isBaby() ? 2.0D : speed, 24);
        this.frog = frog;
        this.lowestY = -1;
    }

    public boolean shouldContinue() {
        return !this.frog.isTouchingWater() && this.tryingTime <= 1200 && this.isTargetPos(this.frog.world, this.targetPos);
    }

    public boolean canStart() {
        return !this.frog.isTouchingWater() && !this.frog.hasFrogEgg() && super.canStart();
    }

    public boolean shouldResetPath() {
        return this.tryingTime % 160 == 0;
    }

    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        return world.getBlockState(pos).isOf(Blocks.WATER);
    }
}
