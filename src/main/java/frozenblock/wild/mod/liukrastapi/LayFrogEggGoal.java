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
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class LayFrogEggGoal extends MoveToTargetPosGoal {
    private final FrogEntity frog;

    public LayFrogEggGoal(FrogEntity frog, double speed) {
        super(frog, speed, 16);
        this.frog = frog;
    }

    public boolean canStart() {
        return this.frog.hasFrogEgg() && super.canStart();
    }

    public boolean shouldContinue() {
        return super.shouldContinue();
    }

    public void tick() {
        super.tick();
    }

    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        return world.isAir(pos.up()) && FrogEggBlock.isWater(world, pos);
    }
    protected boolean canPlace(World world, BlockPos pos) {
        return world.isAir(pos.up()) && FrogEggBlock.isWater(world, pos);
    }
}
