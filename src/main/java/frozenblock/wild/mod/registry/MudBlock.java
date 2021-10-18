package frozenblock.wild.mod.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MudBlock extends Block {
    public MudBlock() {
        super(FabricBlockSettings
                .of(Material.SOLID_ORGANIC)
                .breakByTool(FabricToolTags.SHOVELS, 1)
                .sounds(BlockSoundGroup.GRAVEL)
                .strength(0.5f, 0.5f)
                .ticksRandomly()
        );
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        BlockState blockState = Blocks.POINTED_DRIPSTONE.getDefaultState();
        blockState = (BlockState) blockState.with(PointedDripstoneBlock.VERTICAL_DIRECTION, Direction.DOWN);
        if(world.getBlockState(new BlockPos((int) x, (int) y - 2, (int) z)) == blockState) {
            world.setBlockState(new BlockPos(pos), Blocks.CLAY.getDefaultState());
            if (!world.isClient) {
                world.playSound(
                        null,
                        pos,
                        SoundEvents.BLOCK_GRAVEL_BREAK,
                        SoundCategory.BLOCKS,
                        1f,
                        1f
                );
            }

        }
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        List<ItemStack> dropsOriginal = super.getDroppedStacks(state, builder);
        if (!dropsOriginal.isEmpty())
            return dropsOriginal;
        return Collections.singletonList(new ItemStack(this, 1));
    }
}
