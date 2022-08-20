package net.frozenblock.wildmod.block.deepdark;

import net.frozenblock.wildmod.block.wild.LichenGrower;
import net.frozenblock.wildmod.misc.WildDirection;
import net.frozenblock.wildmod.registry.RegisterBlocks;
import net.frozenblock.wildmod.registry.RegisterSounds;
import net.frozenblock.wildmod.registry.RegisterTags;
import net.frozenblock.wildmod.world.gen.SculkSpreadManager;
import net.frozenblock.wildmod.world.gen.SculkSpreadManager.Cursor;
import net.frozenblock.wildmod.world.gen.SculkSpreadable;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

public class SculkVeinBlock extends AbstractLichenBlock implements SculkSpreadable, Waterloggable {
    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private final LichenGrower allGrowTypeGrower = new LichenGrower(new SculkVeinBlock.SculkVeinGrowChecker(LichenGrower.GROW_TYPES));
    private final LichenGrower samePositionOnlyGrower = new LichenGrower(new SculkVeinBlock.SculkVeinGrowChecker(LichenGrower.GrowType.SAME_POSITION));

    public SculkVeinBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(WATERLOGGED, false));
    }

    public LichenGrower getGrower() {
        return this.allGrowTypeGrower;
    }

    public LichenGrower getSamePositionOnlyGrower() {
        return this.samePositionOnlyGrower;
    }

    public static boolean place(WorldAccess world, BlockPos pos, BlockState state, Collection<Direction> directions) {
        boolean bl = false;
        BlockState blockState = RegisterBlocks.SCULK_VEIN.getDefaultState();

        for (Direction direction : directions) {
            BlockPos blockPos = pos.offset(direction);
            if (canGrowOn(world, direction, blockPos, world.getBlockState(blockPos))) {
                blockState = blockState.with(getProperty(direction), true);
                bl = true;
            }
        }

        if (!bl) {
            return false;
        } else {
            if (!state.getFluidState().isEmpty()) {
                blockState = blockState.with(WATERLOGGED, true);
            }

            world.setBlockState(pos, blockState, 3);
            return true;
        }
    }

    public void spreadAtSamePosition(WorldAccess world, BlockState state, BlockPos pos, Random random) {
        if (state.isOf(this)) {
            for (Direction direction : DIRECTIONS) {
                BooleanProperty booleanProperty = getProperty(direction);
                if (state.get(booleanProperty) && world.getBlockState(pos.offset(direction)).isOf(RegisterBlocks.SCULK)) {
                    state = state.with(booleanProperty, false);
                }
            }

            if (!hasAnyDirection(state)) {
                FluidState fluidState = world.getFluidState(pos);
                state = (fluidState.isEmpty() ? Blocks.AIR : Blocks.WATER).getDefaultState();
            }

            world.setBlockState(pos, state, 3);
            SculkSpreadable.super.spreadAtSamePosition(world, state, pos, random);
        }
    }

    public int spread(
            Cursor cursor, WorldAccess world, BlockPos catalystPos, Random random, SculkSpreadManager spreadManager, boolean shouldConvertToBlock
    ) {
        if (shouldConvertToBlock && this.convertToBlock(spreadManager, world, cursor.getPos(), random)) {
            return cursor.getCharge() - 1;
        } else {
            return random.nextInt(spreadManager.getSpreadChance()) == 0 ? MathHelper.floor((float) cursor.getCharge() * 0.5F) : cursor.getCharge();
        }
    }

    private boolean convertToBlock(SculkSpreadManager spreadManager, WorldAccess world, BlockPos pos, Random random) {
        BlockState blockState = world.getBlockState(pos);
        TagKey<Block> tagKey = spreadManager.getReplaceableTag();

        for (Direction direction : WildDirection.shuffle(random)) {
            if (hasDirection(blockState, direction)) {
                BlockPos blockPos = pos.offset(direction);
                if (world.getBlockState(blockPos).isIn(tagKey)) {
                    BlockState blockState2 = RegisterBlocks.SCULK.getDefaultState();
                    world.setBlockState(blockPos, blockState2, 3);
                    world.playSound(null, blockPos, RegisterSounds.BLOCK_SCULK_SPREAD, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    this.allGrowTypeGrower.grow(blockState2, world, blockPos, spreadManager.isWorldGen());
                    Direction direction2 = direction.getOpposite();

                    for (Direction direction3 : DIRECTIONS) {
                        if (direction3 != direction2) {
                            BlockPos blockPos2 = blockPos.offset(direction3);
                            BlockState blockState3 = world.getBlockState(blockPos2);
                            if (blockState3.isOf(this)) {
                                this.spreadAtSamePosition(world, blockState3, blockPos2, random);
                            }
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }

    public static boolean veinCoversSculkReplaceable(WorldAccess world, BlockState state, BlockPos pos) {
        if (!state.isOf(RegisterBlocks.SCULK_VEIN)) {
            return false;
        } else {
            for (Direction direction : DIRECTIONS) {
                if (hasDirection(state, direction) && world.getBlockState(pos.offset(direction)).isIn(RegisterTags.SCULK_REPLACEABLE)) {
                    return true;
                }
            }

            return false;
        }
    }

    public BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
    ) {
        if (state.get(WATERLOGGED)) {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WATERLOGGED);
    }

    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return !context.getStack().isOf(RegisterBlocks.SCULK_VEIN.asItem()) || super.canReplace(state, context);
    }



    public boolean canGrowWithDirection(BlockView getter, BlockState state, BlockPos pos, Direction direction) {
        if (!this.canHaveDirection(direction) || state.isOf(this) && hasDirection(state, direction)) {
            return false;
        } else {
            BlockPos blockPos = pos.offset(direction);
            return canGrowOn(getter, direction, blockPos, getter.getBlockState(blockPos));
        }
    }

    public static boolean canGrowOn(BlockView getter, Direction direction, BlockPos pos, BlockState state) {
        return Block.isFaceFullSquare(state.getSidesShape(getter, pos), direction.getOpposite())
                || Block.isFaceFullSquare(state.getCollisionShape(getter, pos), direction.getOpposite());
    }

    public static boolean hasDirection(BlockState state, Direction direction) {
        BooleanProperty booleanProperty = AbstractLichenBlock.getProperty(direction);
        return state.contains(booleanProperty) && state.get(booleanProperty);
    }

    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    public static byte directionsToFlag(Collection<Direction> directions) {
        byte flag = 0;

        for(Direction direction : directions) {
            flag = (byte)(flag | 1 << direction.ordinal());
        }

        return flag;
    }

    public static Set<Direction> collectDirections(BlockState state) {
        if (!(state.getBlock() instanceof AbstractLichenBlock)) {
            return Set.of();
        } else {
            Set<Direction> directions = EnumSet.noneOf(Direction.class);

            for(Direction direction : DIRECTIONS) {
                if (hasDirection(state, direction)) {
                    directions.add(direction);
                }
            }

            return directions;
        }
    }

    public static Set<Direction> flagToDirections(byte flag) {
        Set<Direction> set = EnumSet.noneOf(Direction.class);

        for (Direction direction : Direction.values()) {
            if ((flag & (byte) (1 << direction.ordinal())) > 0) {
                set.add(direction);
            }
        }

        return set;
    }

    public class SculkVeinGrowChecker extends LichenGrower.LichenGrowChecker {
        private final LichenGrower.GrowType[] growTypes;

        public SculkVeinGrowChecker(LichenGrower.GrowType... growTypes) {
            super(SculkVeinBlock.this);
            this.growTypes = growTypes;
        }

        public boolean canGrow(BlockView world, BlockPos pos, BlockPos growPos, Direction direction, BlockState state) {
            BlockState blockState = world.getBlockState(growPos.offset(direction));
            if (!blockState.isOf(RegisterBlocks.SCULK) && !blockState.isOf(RegisterBlocks.SCULK_CATALYST) && !blockState.isOf(Blocks.MOVING_PISTON)) {
                if (pos.getManhattanDistance(growPos) == 2) {
                    BlockPos blockPos = pos.offset(direction.getOpposite());
                    if (world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, direction)) {
                        return false;
                    }
                }

                FluidState fluidState = state.getFluidState();
                if (!fluidState.isEmpty() && !fluidState.isOf(Fluids.WATER)) {
                    return false;
                } else {
                    Material material = state.getMaterial();
                    if (material == Material.FIRE) {
                        return false;
                    } else {
                        return material.isReplaceable() || super.canGrow(world, pos, growPos, direction, state);
                    }
                }
            } else {
                return false;
            }
        }

        public LichenGrower.GrowType[] getGrowTypes() {
            return this.growTypes;
        }

        public boolean canGrow(BlockState state) {
            return !state.isOf(RegisterBlocks.SCULK_VEIN);
        }
    }
}

