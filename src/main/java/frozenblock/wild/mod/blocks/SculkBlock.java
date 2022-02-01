package frozenblock.wild.mod.blocks;

import frozenblock.wild.mod.fromAccurateSculk.NewProperties;
import frozenblock.wild.mod.registry.RegisterParticles;
import frozenblock.wild.mod.registry.RegisterSounds;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class SculkBlock extends Block {
    public static final BooleanProperty ORIGINAL = NewProperties.ORIGINAL_RF;
    public static final IntProperty POWER = Properties.POWER;
    private static final Random random = new Random();

    public SculkBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(POWER, 0).with(ORIGINAL, false));
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean keepValue = state.get(ORIGINAL) && nonSculkPower(world, pos)==null;
            BlockPos chosen = pos.offset(Direction.random(random));
            if (!keepValue && world.isChunkLoaded(chosen)) {
                if (world.getBlockState(chosen).getBlock() == SCULK_BLOCK) {
                    BlockState chosenState = world.getBlockState(chosen);
                    if (!chosenState.get(ORIGINAL)) { world.setBlockState(chosen, chosenState.with(POWER, MathHelper.clamp(chosenState.get(POWER) + 1, 0, 1))); }
                    world.setBlockState(pos, state.with(POWER, MathHelper.clamp(state.get(POWER) - 1, 0, 1)));
                } else {
                    world.setBlockState(pos, state.with(POWER, MathHelper.clamp(state.get(POWER) - 1, 0, 1)));
                }
            }
        if (state.get(POWER)>0 && !keepValue) {world.createAndScheduleBlockTick(pos, SCULK_BLOCK, UniformIntProvider.create(1,10).get(random));}
        if (state.get(POWER)>0 && keepValue) {world.createAndScheduleBlockTick(pos, SCULK_BLOCK, UniformIntProvider.create(1,5).get(random));}
        if (state.get(POWER)==0 && keepValue) {world.setBlockState(pos, SCULK_BLOCK.getDefaultState());}
        }

    public BlockPos nonSculkPower(World world, BlockPos pos) {
        if (world.getEmittedRedstonePower(pos.down(), Direction.DOWN) > 0 && world.getBlockState(pos.down()).getBlock()!=SCULK_BLOCK) {
            return pos.down();
        } else if (world.getEmittedRedstonePower(pos.up(), Direction.UP) > 0 && world.getBlockState(pos.up()).getBlock()!=SCULK_BLOCK) {
            return pos.up();
        } else if (world.getEmittedRedstonePower(pos.north(), Direction.NORTH) > 0 && world.getBlockState(pos.north()).getBlock()!=SCULK_BLOCK) {
            return pos.north();
        } else if (world.getEmittedRedstonePower(pos.south(), Direction.SOUTH) > 0 && world.getBlockState(pos.south()).getBlock()!=SCULK_BLOCK) {
            return pos.south();
        } else if (world.getEmittedRedstonePower(pos.west(), Direction.WEST) > 0 && world.getBlockState(pos.west()).getBlock()!=SCULK_BLOCK) {
            return pos.west();
        } else if (world.getEmittedRedstonePower(pos.west(), Direction.EAST) > 0 && world.getBlockState(pos.east()).getBlock()!=SCULK_BLOCK) {
            return pos.east();
        } else return null;
    }

    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (world instanceof ServerWorld) {
            if (nonSculkPower(world, blockPos)!=null) {
                if (world.getBlockState(blockPos).getBlock()==SCULK_BLOCK) {
                    if (world.getBlockState(blockPos).get(ORIGINAL)) {
                        world.createAndScheduleBlockTick(blockPos, SCULK_BLOCK, UniformIntProvider.create(1,10).get(random));
                    }
                }
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        if (newState.getBlock()==SCULK_BLOCK) {
            world.createAndScheduleBlockTick(pos, SCULK_BLOCK, UniformIntProvider.create(1,10).get(random));
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ORIGINAL).add(POWER);
    }

    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
        super.onStacksDropped(state, world, pos, stack);
        int i = 1;
        this.dropExperience(world, pos, i);
    }

    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    public boolean hasRandomTicks(BlockState state) {
        return state.get(POWER)>0;
    }

    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWER);
    }

    public static final Block SCULK_BLOCK = new SculkBlock(FabricBlockSettings.of(Material.SCULK).strength(1f).mapColor(MapColor.CYAN).sounds(new BlockSoundGroup(1.0f, 1.2f,
            RegisterSounds.BLOCK_SCULK_BREAK,
            RegisterSounds.BLOCK_SCULK_STEP,
            RegisterSounds.BLOCK_SCULK_PLACE,
            RegisterSounds.BLOCK_SCULK_HIT,
            RegisterSounds.BLOCK_SCULK_FALL
    )));

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        for (int r = 0; r<MathHelper.clamp(state.get(POWER), 0, 5)*3; ++r) {
            world.addImportantParticle(RegisterParticles.SCULK_RF, pos.getX() + getParticleDouble(), pos.getY() + getParticleDouble(), pos.getZ() + getParticleDouble(), pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public double getParticleDouble() {
        return (UniformIntProvider.create(0,11).get(new Random()))*0.1;
    }

}
