package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.blocks.FrogEggBlock;
import frozenblock.wild.mod.entity.FrogEntity;
import frozenblock.wild.mod.registry.RegisterBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class LayFrogEggGoal extends MoveToTargetPosGoal {
    private final FrogEntity frog;

    public LayFrogEggGoal(FrogEntity frog, double speed) {
        super(frog, speed, 16);
        this.frog = frog;
    }

    public boolean canStart() {
        return this.frog.hasFrogEgg() && this.frog.getBlockPos().isWithinDistance(this.frog.getPos(), 9.0D) && super.canStart();
    }

    public boolean shouldContinue() {
        return super.shouldContinue() && this.frog.hasFrogEgg() && this.frog.getBlockPos().isWithinDistance(this.frog.getPos(), 9.0D);
    }

    public void tick() {
        super.tick();
        BlockPos blockPos = this.frog.getBlockPos();
        if (this.frog.isTouchingWater() && this.hasReached()) {
            World world = this.frog.world;
            world.playSound((PlayerEntity) null, blockPos, SoundEvents.ENTITY_TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3F, 0.9F + world.random.nextFloat() * 0.2F);
            world.setBlockState(this.targetPos.up(), (BlockState) RegisterBlocks.FROG_EGG.getDefaultState(), 3);
            world.syncWorldEvent(2005, this.targetPos.up(), 0);
            this.frog.setHasEgg(false);
            this.frog.setLoveTicks(600);
        }
    }

    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        return world.isAir(pos.up()) && FrogEggBlock.isWater(world, pos);
    }
}
