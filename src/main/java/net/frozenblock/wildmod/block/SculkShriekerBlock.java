/*
 * Decompiled with CFR 0.0.9 (FabricMC cc05e23f).
 */
package net.frozenblock.wildmod.block;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.block.entity.SculkShriekerBlockEntity;
import net.frozenblock.wildmod.entity.WardenEntity;
import net.frozenblock.wildmod.entity.util.LargeEntitySpawnHelper;
import net.frozenblock.wildmod.fromAccurateSculk.*;
import net.frozenblock.wildmod.registry.*;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

import java.util.*;

//SCULK SHRIEKER REWRITE FROM LUNADE'S MOD ACCURATE SCULK

public class SculkShriekerBlock
        extends BlockWithEntity implements Waterloggable {
    public static final int field_31239 = 40;
    public static final int field_31240 = 1;
    public static final EnumProperty<SculkShriekerPhase> SCULK_SHRIEKER_PHASE = WildProperties.SCULK_SHRIEKER_PHASE;
    public static final IntProperty POWER = Properties.POWER;
    private static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    public static final BooleanProperty CAN_SUMMON = WildProperties.CAN_SUMMON;
    private final int range;
    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public int getRange() {
        return this.range;
    }

    public SculkShriekerBlock(Settings settings, int i) {
        super(settings);
        this.setDefaultState(((this.stateManager.getDefaultState()).with(SCULK_SHRIEKER_PHASE, SculkShriekerPhase.INACTIVE)).with(WATERLOGGED, false));
        this.range = i;
    }

    @Override
    protected void dropExperience(ServerWorld serverWorld, BlockPos blockPos, int i) {
        if (serverWorld.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            ExperienceOrbEntity.spawn(serverWorld, Vec3d.ofCenter(blockPos), 5);
        }
    }
    @Override
    public void onStacksDropped(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, ItemStack itemStack) {
        int i;
        super.onStacksDropped(blockState, serverWorld, blockPos, itemStack);
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, itemStack) == 0) {
            i=1;
            this.dropExperience(serverWorld, blockPos, i);
        }
    }
    @Override
    public void scheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        if (SculkShriekerBlock.getPhase(blockState) == SculkShriekerPhase.COOLDOWN) {
            serverWorld.setBlockState(blockPos, blockState.with(SCULK_SHRIEKER_PHASE, SculkShriekerPhase.INACTIVE), 3);
        } else if (serverWorld.getGameRules().getBoolean(WildMod.SHRIEKER_GARGLES) && ((SculkShriekerBlockEntity) Objects.requireNonNull(serverWorld.getBlockEntity(blockPos))).getTicks() > 0 && SculkShriekerBlock.getPhase(blockState) == SculkShriekerPhase.ACTIVE && serverWorld.getBlockState(blockPos).get(Properties.WATERLOGGED)) {
            sendGargleParticles(serverWorld, blockPos);
            ((SculkShriekerBlockEntity) Objects.requireNonNull(serverWorld.getBlockEntity(blockPos))).setTicks(((SculkShriekerBlockEntity) Objects.requireNonNull(serverWorld.getBlockEntity(blockPos))).getTicks()-1);
            serverWorld.createAndScheduleBlockTick(new BlockPos(blockPos), blockState.getBlock(), 1);
            if (((SculkShriekerBlockEntity) Objects.requireNonNull(serverWorld.getBlockEntity(blockPos))).getTicks() <= ((SculkShriekerBlockEntity) Objects.requireNonNull(serverWorld.getBlockEntity(blockPos))).getPrevTick()-5) {
                sendParticles(serverWorld, blockPos, ((SculkShriekerBlockEntity) Objects.requireNonNull(serverWorld.getBlockEntity(blockPos))).getDirection());
                ((SculkShriekerBlockEntity) Objects.requireNonNull(serverWorld.getBlockEntity(blockPos))).setPrevTick(((SculkShriekerBlockEntity) Objects.requireNonNull(serverWorld.getBlockEntity(blockPos))).getTicks());
                (Objects.requireNonNull(serverWorld.getBlockEntity(blockPos))).markDirty();
            }
        } else if (((SculkShriekerBlockEntity) Objects.requireNonNull(serverWorld.getBlockEntity(blockPos))).getTicks() > 0 && SculkShriekerBlock.getPhase(blockState) == SculkShriekerPhase.ACTIVE) {
            sendParticles(serverWorld, blockPos, ((SculkShriekerBlockEntity) Objects.requireNonNull(serverWorld.getBlockEntity(blockPos))).getDirection());
            ((SculkShriekerBlockEntity) Objects.requireNonNull(serverWorld.getBlockEntity(blockPos))).setTicks(((SculkShriekerBlockEntity) Objects.requireNonNull(serverWorld.getBlockEntity(blockPos))).getTicks()-1);
            (Objects.requireNonNull(serverWorld.getBlockEntity(blockPos))).markDirty();
            serverWorld.createAndScheduleBlockTick(new BlockPos(blockPos), blockState.getBlock(), 5);
        } else if (((SculkShriekerBlockEntity) Objects.requireNonNull(serverWorld.getBlockEntity(blockPos))).getTicks()<=0 && SculkShriekerBlock.getPhase(blockState) == SculkShriekerPhase.ACTIVE) {
            SculkShriekerBlock.setCooldown(serverWorld, blockPos, blockState);
        }
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (world.isClient() || blockState.isOf(blockState2.getBlock())) {
            return;
        }
        if (blockState.get(POWER) > 0 && !world.getBlockTickScheduler().isQueued(blockPos, this)) {
            world.setBlockState(blockPos, blockState.with(POWER, 0), 18);
        }
        world.createAndScheduleBlockTick(new BlockPos(blockPos), blockState.getBlock(), 1);
    }

    @Override
    public void onStateReplaced(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState.isOf(blockState2.getBlock())) {
            return;
        }
        if (SculkShriekerBlock.getPhase(blockState) == SculkShriekerPhase.ACTIVE) {
            SculkShriekerBlock.updateNeighbors(world, blockPos);
        }
        super.onStateReplaced(blockState, world, blockPos, blockState2, bl);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, WorldAccess worldAccess, BlockPos blockPos, BlockPos blockPos2) {
        if (blockState.get(WATERLOGGED)) {
            worldAccess.createAndScheduleFluidTick(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(worldAccess));
        }
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, worldAccess, blockPos, blockPos2);
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        if (blockState.get(WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(blockState);
    }
    @Override
    public boolean isTranslucent(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return blockState.getFluidState().isEmpty();
    }

    private static void updateNeighbors(World world, BlockPos blockPos) {
        world.updateNeighborsAlways(blockPos, RegisterBlocks.SCULK_SHRIEKER);
        world.updateNeighborsAlways(blockPos.offset(Direction.UP.getOpposite()), RegisterBlocks.SCULK_SHRIEKER);
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SculkShriekerBlockEntity(blockPos, blockState);
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        FluidState fluidState = itemPlacementContext.getWorld().getFluidState(blockPos);
        return this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> GameEventListener getGameEventListener(World world, T blockEntity) {
        if (blockEntity instanceof SculkShriekerBlockEntity) {
            return ((SculkShriekerBlockEntity)blockEntity).getEventListener();
        }
        return null;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world2, BlockState blockState2, BlockEntityType<T> blockEntityType) {
        if (!world2.isClient) {
            return SculkShriekerBlock.checkType(blockEntityType, WildBlockEntityType.SCULK_SHRIEKER, (world, blockPos, blockState, sculkShriekerBlockEntity) -> sculkShriekerBlockEntity.getEventListener().tick(world));
        }
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext shapeContext) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext shapeContext) {
        return SHAPE;
    }

    @Override
    public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
        return blockState.get(POWER);
    }

    public static SculkShriekerPhase getPhase(BlockState blockState) {
        return blockState.get(SCULK_SHRIEKER_PHASE);
    }

    public static boolean isInactive(BlockState blockState) {
        return SculkShriekerBlock.getPhase(blockState) == SculkShriekerPhase.INACTIVE;
    }
    public static void sendDarkness(int dist, BlockPos blockPos, World world) {
        if (world.getGameRules().getBoolean(WildMod.DARKNESS_ENABLED)) {
            Box box = (new Box(blockPos.add(-24, -24, -24), blockPos.add(24, 24, 24)));
            List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, box);
            Iterator<PlayerEntity> var11 = list.iterator();
            PlayerEntity playerEntity;
            while (var11.hasNext()) {
                playerEntity = var11.next();
                if (playerEntity.getBlockPos().isWithinDistance(blockPos, (dist + 1)) && !playerEntity.getAbilities().creativeMode) {
                    playerEntity.addStatusEffect(new StatusEffectInstance(RegisterStatusEffects.DARKNESS, 300, shrieks-1, true, false, false));
                }
            }
        }
    }

    public static void setActive(World world, BlockPos blockPos, BlockState blockState) {
        if (!world.isClient && !findWarden(world, blockPos)) {
            world.setBlockState(blockPos, (blockState.with(SCULK_SHRIEKER_PHASE, SculkShriekerPhase.ACTIVE)), 3);
            SculkShriekerBlock.updateNeighbors(world, blockPos);
            ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setStepped(false);
                if (!world.getBlockState(blockPos).get(Properties.WATERLOGGED)) {
                    ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setTicks(10);
                    (Objects.requireNonNull(world.getBlockEntity(blockPos))).markDirty();
                    world.createAndScheduleBlockTick(new BlockPos(blockPos), blockState.getBlock(), 5);
                    world.playSound(
                            null,
                            blockPos,
                            RegisterSounds.BLOCK_SCULK_SHRIEKER_SHRIEK,
                            SoundCategory.BLOCKS,
                            1.0F,
                            world.random.nextFloat() * 0.1F + 0.9F
                    );
                } else if (world.getGameRules().getBoolean(WildMod.SHRIEKER_GARGLES) && world.getBlockState(blockPos).get(Properties.WATERLOGGED)) {
                    ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setPrevTick(85);
                    ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setTicks(80);
                    (Objects.requireNonNull(world.getBlockEntity(blockPos))).markDirty();
                    world.createAndScheduleBlockTick(new BlockPos(blockPos), blockState.getBlock(), 1);
                    world.playSound(
                            null,
                            blockPos,
                            RegisterAccurateSculk.GARGLE_EVENT,
                            SoundCategory.BLOCKS,
                            1f,
                            1f
                    );
                } else {
                    ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setTicks(10);
                    (Objects.requireNonNull(world.getBlockEntity(blockPos))).markDirty();
                    world.createAndScheduleBlockTick(new BlockPos(blockPos), blockState.getBlock(), 5);
                    world.playSound(
                            null,
                            blockPos,
                            RegisterSounds.BLOCK_SCULK_SHRIEKER_SHRIEK,
                            SoundCategory.BLOCKS,
                            1f,
                            world.random.nextFloat() * 0.1F + 0.9F
                    );
                }
            }
        }

    public static void setStepActive(World world, BlockPos blockPos, BlockState blockState) {
        if (!world.isClient && !findWarden(world, blockPos)) {
            world.setBlockState(blockPos, (blockState.with(SCULK_SHRIEKER_PHASE, SculkShriekerPhase.ACTIVE)), 3);
            SculkShriekerBlock.updateNeighbors(world, blockPos);
            ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setStepped(true);
            if (!world.getBlockState(blockPos).get(Properties.WATERLOGGED)) {
                ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setTicks(10);
                (Objects.requireNonNull(world.getBlockEntity(blockPos))).markDirty();
                world.createAndScheduleBlockTick(new BlockPos(blockPos), blockState.getBlock(), 5);
                world.playSound(
                        null,
                        blockPos,
                        RegisterSounds.BLOCK_SCULK_SHRIEKER_SHRIEK,
                        SoundCategory.BLOCKS,
                        1F,
                        world.random.nextFloat() * 0.1F + 0.9F
                    );
            } else if (world.getGameRules().getBoolean(WildMod.SHRIEKER_GARGLES) && world.getBlockState(blockPos).get(Properties.WATERLOGGED)) {
                ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setPrevTick(85);
                ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setTicks(80);
                (Objects.requireNonNull(world.getBlockEntity(blockPos))).markDirty();
                world.createAndScheduleBlockTick(new BlockPos(blockPos), blockState.getBlock(), 1);
                world.playSound(
                        null,
                        blockPos,
                        RegisterAccurateSculk.GARGLE_EVENT,
                        SoundCategory.BLOCKS,
                        1f,
                        1f
                );
            } else {
                ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setTicks(10);
                (Objects.requireNonNull(world.getBlockEntity(blockPos))).markDirty();
                world.createAndScheduleBlockTick(new BlockPos(blockPos), blockState.getBlock(), 5);
                world.playSound(
                        null,
                        blockPos,
                        RegisterSounds.BLOCK_SCULK_SHRIEKER_SHRIEK,
                        SoundCategory.BLOCKS,
                        1f,
                        world.random.nextFloat() * 0.1F + 0.9F
                );
            }
        }
    }

    public static void sendParticles(World world, BlockPos blockPos, int direction) {
        if (!world.isClient) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(blockPos);
            buf.writeInt(direction);
            for (ServerPlayerEntity player : PlayerLookup.around((ServerWorld) world, blockPos, 32)) {
                ServerPlayNetworking.send(player, RegisterAccurateSculk.SHRIEKER_SHRIEK_PACKET, buf);
            }
        }
    }
    public static void sendGargleParticles(World world, BlockPos blockPos) {
        if (!world.isClient) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(blockPos);
            int decider = UniformIntProvider.create(0, 1).get(world.getRandom());
            //Radius is cut from 32 to 12 in order to slightly help performance/bandwidth since this is kinda spamming tbh
            for (ServerPlayerEntity player : PlayerLookup.around((ServerWorld) world, blockPos, 12)) {
                if (decider > 0.5) {
                    ServerPlayNetworking.send(player, RegisterAccurateSculk.SHRIEKER_GARGLE1_PACKET, buf);
                } else if (decider < 0.5) {
                    ServerPlayNetworking.send(player, RegisterAccurateSculk.SHRIEKER_GARGLE2_PACKET, buf);
                }
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SCULK_SHRIEKER_PHASE, POWER);
        builder.add(WATERLOGGED);
    }

    @Override
    public boolean hasComparatorOutput(BlockState blockState) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
        return 0;
    }

    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (!world.isClient) {
            Direction[] var4 = Direction.values();
            int var5 = var4.length;
            int var6;
            for (var6 = 0; var6 < var5; ++var6) {
                Direction direction2 = var4[var6];
                if (world.getBlockState(blockPos2).getBlock() != Blocks.SCULK_SENSOR) {
                    if (world.isEmittingRedstonePower(blockPos.offset(direction2), direction2) && SculkShriekerBlock.getPhase(blockState) == SculkShriekerPhase.INACTIVE) {
                        if (!world.getBlockState(blockPos).get(Properties.WATERLOGGED)) {
                            writeDir(world, blockPos, blockPos2);
                            Objects.requireNonNull(world.getBlockEntity(blockPos)).markDirty();
                            setActive(world, blockPos, this.getDefaultState().with(Properties.WATERLOGGED, false));
                        } else if (world.getBlockState(blockPos).get(Properties.WATERLOGGED)) {
                            writeDir(world, blockPos, blockPos2);
                            Objects.requireNonNull(world.getBlockEntity(blockPos)).markDirty();
                            setActive(world, blockPos, this.getDefaultState().with(Properties.WATERLOGGED, true));
                        }
                    }
                }
            }
        }
    }

    public void writeDir(World world, BlockPos blockPos, BlockPos blockPos2) {
        int A = blockPos.getX();
        int B = blockPos.getZ();
        int C = blockPos2.getX();
        int D = blockPos2.getZ();
        double AA = Math.sqrt((A * A));
        double BB = Math.sqrt((B * B));
        double CC = Math.sqrt((C * C));
        double DD = Math.sqrt((D * D));
        double distX = A - C;
        double distZ = B - D;
        double distXa = AA - CC;
        double distZa = BB - DD;
        double AX = Math.sqrt(((distXa) * (distXa)));
        double AZ = Math.sqrt(((distZa) * (distZa)));
        if (AX > AZ) {
            if (distX < 0) {
                ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setDirection(3);
            } else if (distX > 0) {
                ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setDirection(4);
            }
        } else if (AZ > AX) {
            if (distZ < 0) {
                ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setDirection(1);
            } else if (distZ > 0) {
                ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setDirection(2);
            }
        } else if (AZ == AX) {
            if (distX < 0 && distZ < 0) {
                if ((UniformIntProvider.create(1, 2).get(world.getRandom())) > 1.5) {
                    ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setDirection(1);
                } else if ((UniformIntProvider.create(1, 2).get(world.getRandom())) < 1.5) {
                    ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setDirection(3);
                }
            } else if (distX > 0 && distZ > 0) {
                if ((UniformIntProvider.create(1, 2).get(world.getRandom())) > 1.5) {
                    ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setDirection(2);
                } else if ((UniformIntProvider.create(1, 2).get(world.getRandom())) < 1.5) {
                    ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setDirection(4);
                }
            } else if (distX < 0 && distZ > 0) {
                if ((UniformIntProvider.create(1, 2).get(world.getRandom())) > 1.5) {
                    ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setDirection(3);
                } else if ((UniformIntProvider.create(1, 2).get(world.getRandom())) < 1.5) {
                    ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setDirection(2);
                }
            } else if (distX > 0 && distZ < 0) {
                if ((UniformIntProvider.create(1, 2).get(world.getRandom())) > 1.5) {
                    ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setDirection(4);
                } else if ((UniformIntProvider.create(1, 2).get(world.getRandom())) < 1.5) {
                    ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setDirection(1);
                }
            } else
                ((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).setDirection((UniformIntProvider.create(1, 4).get(world.getRandom())));
        }
    }

    @Override
    public boolean canPathfindThrough(BlockState blockState, BlockView blockView, BlockPos blockPos, NavigationType navigationType) {
        return false;
    }

    @Override
    public boolean hasSidedTransparency(BlockState blockState) {
        return true;
    }

    public static void setCooldown(World world, BlockPos blockPos, BlockState blockState) {
        if (!world.isClient) {
            world.setBlockState(blockPos, (blockState.with(SCULK_SHRIEKER_PHASE, SculkShriekerPhase.COOLDOWN)), 3);
            if (((SculkShriekerBlockEntity) Objects.requireNonNull(world.getBlockEntity(blockPos))).getStepped()) {
                world.createAndScheduleBlockTick(new BlockPos(blockPos), blockState.getBlock(), 20);
            } else {
                world.createAndScheduleBlockTick(new BlockPos(blockPos), blockState.getBlock(), 1);
            }

            sendDarkness(24, blockPos, world);

            addShriek(blockPos, world, 1);

            SculkShriekerBlock.updateNeighbors(world, blockPos);
        }
    }

    /**SHRIEK COUNTER */

    public static int shrieks;
    private static long timer;

    public static BlockPos getRandomSpawnable(ArrayList<BlockPos> poses, World world, Random random) {
        ArrayList<BlockPos> finals = new ArrayList<>();
        Iterator<BlockPos> var11 = poses.iterator();
        BlockPos currentCheck;
        while(var11.hasNext()) {
            currentCheck = var11.next();
            if (world.isNight()) {
                finals.add(currentCheck);
            }
            if (!finals.contains(currentCheck) && !world.isSkyVisible(currentCheck.up())) {
                finals.add(currentCheck);
            }
        }
        if (!finals.isEmpty()) {
            return finals.get((int) (Math.random() * finals.size()));
        } return null;
    }

    public static void addShriek(BlockPos pos, World world, int i) {
        if (world instanceof ServerWorld serverWorld) {
            if (world.getTime() - timer < -90) {
                timer = 0;
                shrieks = 0;
            }
            if (world.getGameRules().getBoolean(WildMod.DO_WARDEN_SPAWNING) && world.getTime() > timer) {
                timer = world.getTime() + 30;
                shrieks = shrieks + i;
                if (world.isSkyVisible(pos.up()) && !world.isNight()) {
                    world.playSound(null, pos, RegisterSounds.ENTITY_WARDEN_AMBIENT, SoundCategory.HOSTILE, 5.0F, 0.8F);
                }
                if (shrieks >= 3 && (!world.isSkyVisible(pos.up()) || world.isNight())) {
                    shrieks = 0;
                    LargeEntitySpawnHelper.trySpawnAt(
                            RegisterEntities.WARDEN, SpawnReason.TRIGGERED, serverWorld, pos, 20, 5, 6);
                }

            }
        }
    }

    public static boolean findWarden(World world, BlockPos pos) {
        double x1 = pos.getX();
        double y1 = pos.getY();
        double z1 = pos.getZ();
        BlockPos side1 = new BlockPos(x1-26, y1-26, z1-26);
        BlockPos side2 = new BlockPos(x1+26, y1+26, z1+26);
        Box box = (new Box(side1, side2));
        List<WardenEntity> list = world.getNonSpectatingEntities(WardenEntity.class, box);
        if (!list.isEmpty()) {
            Iterator<WardenEntity> var11 = list.iterator();
            WardenEntity warden;
            while (var11.hasNext()) {
                warden = var11.next();
                if (warden.getBlockPos().isWithinDistance(pos, (24))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean verifyWardenSpawn(BlockPos p, World world) {
        if (canSpawn(world, p)) {
            return wardenNonCollide(p, world);
        }
        return false;
    }
    public static boolean canSpawn(World world, BlockPos p) {
        return !SculkTags.blockTagContains(world.getBlockState(p).getBlock(), SculkTags.WARDEN_UNSPAWNABLE) && !world.getBlockState(p).isAir() && world.getBlockState(p).getBlock()!=Blocks.WATER &&  world.getBlockState(p).getBlock()!=Blocks.LAVA;
    }
    public static boolean wardenNonCollide(BlockPos p, World world) {
        return SculkTags.blockTagContains(world.getBlockState(p.up()).getBlock(), SculkTags.WARDEN_NON_COLLIDE) && SculkTags.blockTagContains(world.getBlockState(p.up(2)).getBlock(), SculkTags.WARDEN_NON_COLLIDE) && SculkTags.blockTagContains(world.getBlockState(p.up(3)).getBlock(), SculkTags.WARDEN_NON_COLLIDE);
    }

}
