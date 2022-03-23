package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.blocks.FrogSpawnBlock;
import frozenblock.wild.mod.entity.FrogEntity;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class LayFrogSpawnGoal extends MoveToTargetPosGoal {
    private final FrogEntity frog;

    public LayFrogSpawnGoal(FrogEntity frog, double speed) {
        super(frog, speed, 16);
        this.frog = frog;
    }

    public boolean canStart() {
        return this.frog.pregnant() && super.canStart();
    }

    public boolean shouldContinue() {
        return super.shouldContinue();
    }

    public void tick() {
        super.tick();
    }

    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        return world.isAir(pos.up()) && FrogSpawnBlock.isWater(world, pos);
    }
    protected boolean canPlace(World world, BlockPos pos) {
        return world.isAir(pos.up()) && FrogSpawnBlock.isWater(world, pos);
    }
}
