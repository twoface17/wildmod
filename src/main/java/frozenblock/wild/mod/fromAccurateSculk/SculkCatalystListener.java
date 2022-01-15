package frozenblock.wild.mod.fromAccurateSculk;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.blocks.SculkVeinBlock;
import frozenblock.wild.mod.registry.RegisterAccurateSculk;
import frozenblock.wild.mod.registry.RegisterBlocks;
import frozenblock.wild.mod.registry.RegisterSounds;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.GameEventTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.Vibration;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.SculkSensorListener;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

import static java.lang.Math.*;

public class SculkCatalystListener implements GameEventListener {
    protected final PositionSource positionSource;
    protected final int range;
    protected final SculkSensorListener.Callback callback;
    protected Optional<GameEvent> event = Optional.empty();
    protected int distance;
    protected int delay = 0;

    public SculkCatalystListener(PositionSource positionSource, int range, SculkSensorListener.Callback callback) {
        this.positionSource = positionSource;
        this.range = range;
        this.callback = callback;
    }

    public void tick(World world) {
        if (this.event.isPresent()) {
            --this.delay;
            if (this.delay <= 0) {
                this.delay = 0;
                this.callback.accept(world, this, this.event.get(), this.distance);
                this.event = Optional.empty();
            }
        }
    }

    @Override
    public PositionSource getPositionSource() {
        return this.positionSource;
    }

    @Override
    public int getRange() {
        return this.range;
    }

    @Override
    public boolean listen(World world, GameEvent gameEvent, @Nullable Entity entity, BlockPos blockPos) {
        if (!this.shouldActivate(gameEvent, entity, world)) {
            return false;
        }
        Optional<BlockPos> optional = this.positionSource.getPos(world);
        if (optional.isEmpty()) {
            return false;
        }
        BlockPos blockPos2 = optional.get();
        if (!this.callback.accepts(world, this, blockPos, gameEvent, entity)) {
            return false;
        }
        this.listen(world, gameEvent, blockPos, blockPos2, entity);
        return true;
    }

    private boolean shouldActivate(GameEvent gameEvent, @Nullable Entity entity, World world) {
    if (world.getGameRules().getBoolean(WildMod.CATALYST_DETECTS_ALL)) {
        return !GameEventTags.IGNORE_VIBRATIONS_SNEAKING.contains(gameEvent) || !Objects.requireNonNull(entity).bypassesSteppingEffects();
    } else return entity != null && gameEvent == ClickGameEvent.DEATH && SculkTags.DROPSXP.contains(entity.getType());
    }
    public void placeSculkOptim(BlockPos NewSculk, World world) {
        BlockState sculk = RegisterBlocks.SCULK.getDefaultState();
        veins(NewSculk, world);
        world.removeBlock(NewSculk, true);
        world.setBlockState(NewSculk, sculk);
        if (world.getBlockState(NewSculk.up()).getBlock()!=Blocks.WATER) {
            if (world.getBlockState(NewSculk.up()).contains(Properties.WATERLOGGED)&&world.getBlockState(NewSculk.up()).get(Properties.WATERLOGGED).equals(true)) {
                world.setBlockState(NewSculk.up(), Blocks.WATER.getDefaultState());
            } else
                world.removeBlock(NewSculk.up(), true);
        }
    }

    public void placeSculk(BlockPos blockPos, World world) {
        BlockPos NewSculk;
        if (SculkTags.BLOCK_REPLACEABLE.contains(world.getBlockState(blockPos).getBlock()) && SculkTags.SCULK_REPLACEABLE.contains(world.getBlockState(blockPos.up()).getBlock())) {
            NewSculk = blockPos;
            placeSculkOptim(NewSculk, world);
        } else if (SculkTags.BLOCK_REPLACEABLE.contains(world.getBlockState(sculkCheck(blockPos, world, blockPos)).getBlock()) && air(world, sculkCheck(blockPos, world, blockPos))) {
            NewSculk = sculkCheck(blockPos, world, blockPos);
            if (NewSculk != blockPos) {
                placeSculkOptim(NewSculk, world);
            }
        } else if (solid(world, sculkCheck(blockPos, world, blockPos))) {
            NewSculk = sculkCheck(blockPos, world, blockPos);
            veins(NewSculk, world);
        }
    }

    public void pseudoSculk(World world, @Nullable Entity entity) {
        Optional<BlockPos> optional = this.positionSource.getPos(world);
        if (optional.isPresent()) {
            if (entity != null) {
                BlockPos thisPos = optional.get();
                if (SculkTags.THREE.contains(entity.getType())) {
                    ExperienceOrbEntity.spawn((ServerWorld) world, Vec3d.ofCenter(thisPos.add(0, 0.5, 0)), UniformIntProvider.create(1, 3).get(world.getRandom()));
                } else if (SculkTags.FIVE.contains(entity.getType())) {
                    ExperienceOrbEntity.spawn((ServerWorld) world, Vec3d.ofCenter(thisPos).add(0, 0.5, 0), UniformIntProvider.create(3, 5).get(world.getRandom()));
                } else if (SculkTags.TEN.contains(entity.getType())) {
                    ExperienceOrbEntity.spawn((ServerWorld) world, Vec3d.ofCenter(thisPos).add(0, 0.5, 0), UniformIntProvider.create(7, 10).get(world.getRandom()));
                } else if (SculkTags.TWENTY.contains(entity.getType())) {
                    ExperienceOrbEntity.spawn((ServerWorld) world, Vec3d.ofCenter(thisPos).add(0, 0.5, 0), UniformIntProvider.create(15, 20).get(world.getRandom()));
                } else if (SculkTags.FIFTY.contains(entity.getType())) {
                    ExperienceOrbEntity.spawn((ServerWorld) world, Vec3d.ofCenter(thisPos).add(0, 0.5, 0), UniformIntProvider.create(30, 50).get(world.getRandom()));
                } else if (SculkTags.ONEHUNDRED.contains(entity.getType())) {
                    ExperienceOrbEntity.spawn((ServerWorld) world, Vec3d.ofCenter(thisPos).add(0, 0.5, 0), 200);
                }
            }
        }
    }
    public void sculkOptim(float loop, int rVal, BlockPos down, World world) {
        placeSculk(down, world);
        float fLoop = loop * world.getGameRules().getInt(WildMod.SCULK_MULTIPLIER);
        for (int l = 0; l < fLoop; ++l) {
            double a = random() * 2 * PI;
            double r = sqrt(rVal*world.getGameRules().getInt(WildMod.SCULK_MULTIPLIER)) * sqrt(random());
            int x = (int) (r * cos(a));
            int y = (int) (r * sin(a));
            placeSculk(down.add(x, 0, y), world);
        }
    }
    public void sculk(BlockPos blockPos, World world, @Nullable Entity entity) {
        if (entity!=null) {
            world.playSound(null, blockPos, RegisterSounds.BLOCK_SCULK_CATALYST_BLOOM, SoundCategory.BLOCKS, 1F, 1F);
            BlockPos down = blockPos.down();
            if (!world.getGameRules().getBoolean(WildMod.SCULK_THREADING)) {
                if (SculkTags.THREE.contains(entity.getType())) {
                    sculkOptim(3, 4, down, world);
                } else if (SculkTags.FIVE.contains(entity.getType())) {
                    sculkOptim(4, 5, down, world);
                } else if (SculkTags.TEN.contains(entity.getType())) {
                    sculkOptim(9, 10, down, world);
                } else if (SculkTags.TWENTY.contains(entity.getType())) {
                    sculkOptim(19, 20, down, world);
                } else if (SculkTags.FIFTY.contains(entity.getType())) {
                    sculkOptim(59, 50, down, world);
                } else if (SculkTags.ONEHUNDRED.contains(entity.getType())) {
                    sculkOptim(1599, 33, down, world);
                } else if (world.getGameRules().getBoolean(WildMod.CATALYST_DETECTS_ALL)) {
                    sculkOptim((UniformIntProvider.create(1, 7).get(world.getRandom())), (UniformIntProvider.create(1, 7).get(world.getRandom())), down, world);
                }
        } else if (world.getGameRules().getBoolean(WildMod.SCULK_THREADING)) {

        }
        }
    }
    public BlockPos sculkCheck(BlockPos blockPos, World world, BlockPos blockPos2) {
        if (checkPt1(blockPos, world).getY()!=-64) {
            return checkPt1(blockPos, world);
        } else if (checkPt2(blockPos, world).getY()!=-64) {
            return checkPt2(blockPos, world);
        } else { return blockPos2; }
    }
    public BlockPos checkPt1(BlockPos blockPos, World world) {
        int upward = world.getGameRules().getInt(WildMod.UPWARD_SPREAD);
        int MAX = world.getHeight();
        if (blockPos.getY() + upward >= MAX) {
            upward = (MAX - blockPos.getY())-1;
        }
        for (int h = 0; h < upward; h++) {
            if (solrepsculk(world, blockPos.up(h))) {
                return blockPos.up(h);
            }
        }
        return new BlockPos(0,-64,0);
    }
    public BlockPos checkPt2(BlockPos blockPos, World world) {
        int downward = world.getGameRules().getInt(WildMod.DOWNWARD_SPREAD);
        int MIN = world.getBottomY();
        if (blockPos.getY() - downward <= MIN) {
            downward = (blockPos.getY()-MIN)-1;
        }
        for (int h = 0; h < downward; h++) {
            if (solrepsculk(world, blockPos.down(h))) {
                return blockPos.down(h);
            }
        }
        return new BlockPos(0,-64,0);
    }

    public boolean airveins(World world, BlockPos blockPos) {
        if (SculkTags.SCULK.contains(world.getBlockState(blockPos).getBlock())) {
            return false;
        } else if (world.getBlockState(blockPos).isAir()) {
            return false;
        } else if (FluidTags.WATER.contains(world.getFluidState(blockPos).getFluid())) {
            return false;
        } else if (FluidTags.LAVA.contains(world.getFluidState(blockPos).getFluid())) {
            return false;
        } else if (SculkTags.SCULK_REPLACEABLE.contains(world.getBlockState(blockPos).getBlock())) {
            return false;
        } else return !SculkTags.SCULK_UNTOUCHABLE.contains(world.getBlockState(blockPos).getBlock());
    }

    public boolean solid(World world, BlockPos blockPos) {
        return (!world.getBlockState(blockPos).isAir() && !SculkTags.SCULK_UNTOUCHABLE.contains(world.getBlockState(blockPos).getBlock()));
    }
    public boolean solidrep(World world, BlockPos blockPos) {
        return (!world.getBlockState(blockPos).isAir() && !SculkTags.SCULK_UNTOUCHABLE.contains(world.getBlockState(blockPos).getBlock()) && SculkTags.SCULK_REPLACEABLE.contains(world.getBlockState(blockPos).getBlock()) && !SculkTags.SCULK.contains(world.getBlockState(blockPos.down()).getBlock()));
    }
    public boolean solrepsculk(World world, BlockPos blockPos) {
        return (!SculkTags.SCULK_REPLACEABLE.contains(world.getBlockState(blockPos).getBlock()) && SculkTags.SCULK_REPLACEABLE.contains(world.getBlockState(blockPos.up()).getBlock()) && !SculkTags.SCULK.contains(world.getBlockState(blockPos).getBlock()));
    }
    public boolean air(World world, BlockPos blockPos) {
        return SculkTags.SCULK_REPLACEABLE.contains(world.getBlockState(blockPos.up()).getBlock());
    }

 public BlockState vein = SculkVeinBlock.SCULK_VEIN.getDefaultState().with(Properties.DOWN, true);
    public void tiltVeins(BlockPos blockPos, World world) {
        BlockState currentSculk = world.getBlockState(blockPos);
        if (!SculkTags.SCULK_UNBENDABLE.contains((world.getBlockState(blockPos.down())).getBlock())) {
            if (SculkTags.VEIN_CONNECTABLE.contains(world.getBlockState(blockPos.add(1, 1, 0)).getBlock()) && !SculkTags.SCULK_UNBENDABLE.contains((world.getBlockState(blockPos.add(1, 0, 0))).getBlock())) {
                world.setBlockState(blockPos, currentSculk.with(Properties.EAST, true));
                currentSculk = world.getBlockState(blockPos);
                if (SculkTags.VEIN_CONNECTABLE.contains(world.getBlockState(blockPos.add(-1, 1, 0)).getBlock()) && !SculkTags.SCULK_UNBENDABLE.contains((world.getBlockState(blockPos.add(-1, 0, 0))).getBlock())) {
                    world.setBlockState(blockPos, currentSculk.with(Properties.WEST, true));
                    currentSculk = world.getBlockState(blockPos);
                    if (SculkTags.VEIN_CONNECTABLE.contains(world.getBlockState(blockPos.add(0, 1, -1)).getBlock()) && !SculkTags.SCULK_UNBENDABLE.contains((world.getBlockState(blockPos.add(0, 0, -1))).getBlock())) {
                        world.setBlockState(blockPos, currentSculk.with(Properties.NORTH, true));
                        currentSculk = world.getBlockState(blockPos);
                        if (SculkTags.VEIN_CONNECTABLE.contains(world.getBlockState(blockPos.add(0, 1, 1)).getBlock()) && !SculkTags.SCULK_UNBENDABLE.contains((world.getBlockState(blockPos.add(0, 0, 1))).getBlock())) {
                            world.setBlockState(blockPos, currentSculk.with(Properties.SOUTH, true));
                        }
                    }
                }
            } else if (SculkTags.VEIN_CONNECTABLE.contains(world.getBlockState(blockPos.add(-1, 1, 0)).getBlock()) && !SculkTags.SCULK_UNBENDABLE.contains((world.getBlockState(blockPos.add(-1, 0, 0))).getBlock())) {
                world.setBlockState(blockPos, currentSculk.with(Properties.WEST, true));
                currentSculk = world.getBlockState(blockPos);
                if (SculkTags.VEIN_CONNECTABLE.contains(world.getBlockState(blockPos.add(0, 1, -1)).getBlock()) && !SculkTags.SCULK_UNBENDABLE.contains((world.getBlockState(blockPos.add(0, 0, -1))).getBlock())) {
                    world.setBlockState(blockPos, currentSculk.with(Properties.NORTH, true));
                    currentSculk = world.getBlockState(blockPos);
                    if (SculkTags.VEIN_CONNECTABLE.contains(world.getBlockState(blockPos.add(0, 1, 1)).getBlock()) && !SculkTags.SCULK_UNBENDABLE.contains((world.getBlockState(blockPos.add(0, 0, 1))).getBlock())) {
                        world.setBlockState(blockPos, currentSculk.with(Properties.SOUTH, true));
                    }
                }
            } else if (SculkTags.VEIN_CONNECTABLE.contains(world.getBlockState(blockPos.add(0, 1, -1)).getBlock()) && !SculkTags.SCULK_UNBENDABLE.contains((world.getBlockState(blockPos.add(0, 0, -1))).getBlock())) {
                world.setBlockState(blockPos, currentSculk.with(Properties.NORTH, true));
                currentSculk = world.getBlockState(blockPos);
                if (SculkTags.VEIN_CONNECTABLE.contains(world.getBlockState(blockPos.add(0, 1, 1)).getBlock()) && !SculkTags.SCULK_UNBENDABLE.contains((world.getBlockState(blockPos.add(0, 0, 1))).getBlock())) {
                    world.setBlockState(blockPos, currentSculk.with(Properties.SOUTH, true));
                }
            } else if (SculkTags.VEIN_CONNECTABLE.contains(world.getBlockState(blockPos.add(0, 1, 1)).getBlock()) && !SculkTags.SCULK_UNBENDABLE.contains((world.getBlockState(blockPos.add(0, 0, 1))).getBlock())) {
                world.setBlockState(blockPos, currentSculk.with(Properties.SOUTH, true));
            }
        }
    }
    public void tiltVeinsDown(BlockPos blockPos, World world) {
        BlockState currentSculk;
        if (!SculkTags.SCULK_UNBENDABLE.contains((world.getBlockState(blockPos.down())).getBlock())) {
            if (world.getBlockState(blockPos.add(1, -1, 0)).getBlock() == SculkVeinBlock.SCULK_VEIN) {
                currentSculk = world.getBlockState(blockPos.add(1, -1, 0));
                world.setBlockState(blockPos.add(1, -1, 0), currentSculk.with(Properties.WEST, true));
                if (world.getBlockState(blockPos.add(-1, -1, 0)).getBlock() == SculkVeinBlock.SCULK_VEIN) {
                    currentSculk = world.getBlockState(blockPos.add(-1, -1, 0));
                    world.setBlockState(blockPos.add(-1, -1, 0), currentSculk.with(Properties.EAST, true));
                    if (world.getBlockState(blockPos.add(0, -1, -1)).getBlock() == SculkVeinBlock.SCULK_VEIN) {
                        currentSculk = world.getBlockState(blockPos.add(0, -1, -1));
                        world.setBlockState(blockPos.add(0, -1, -1), currentSculk.with(Properties.SOUTH, true));
                        if (world.getBlockState(blockPos.add(0, -1, 1)).getBlock() == SculkVeinBlock.SCULK_VEIN) {
                            currentSculk = world.getBlockState(blockPos.add(0, -1, 1));
                            world.setBlockState(blockPos.add(0, -1, 1), currentSculk.with(Properties.NORTH, true));
                        }
                    }
                }
            } else if (world.getBlockState(blockPos.add(-1, -1, 0)).getBlock() == SculkVeinBlock.SCULK_VEIN) {
                currentSculk = world.getBlockState(blockPos.add(-1, -1, 0));
                world.setBlockState(blockPos.add(-1, -1, 0), currentSculk.with(Properties.EAST, true));
                if (world.getBlockState(blockPos.add(0, -1, -1)).getBlock() == SculkVeinBlock.SCULK_VEIN) {
                    currentSculk = world.getBlockState(blockPos.add(0, -1, -1));
                    world.setBlockState(blockPos.add(0, -1, -1), currentSculk.with(Properties.SOUTH, true));
                    if (world.getBlockState(blockPos.add(0, -1, 1)).getBlock() == SculkVeinBlock.SCULK_VEIN) {
                        currentSculk = world.getBlockState(blockPos.add(0, -1, 1));
                        world.setBlockState(blockPos.add(0, -1, 1), currentSculk.with(Properties.NORTH, true));
                    }
                }
            } else if (world.getBlockState(blockPos.add(0, -1, -1)).getBlock() == SculkVeinBlock.SCULK_VEIN) {
                currentSculk = world.getBlockState(blockPos.add(0, -1, -1));
                world.setBlockState(blockPos.add(0, -1, -1), currentSculk.with(Properties.SOUTH, true));
                if (world.getBlockState(blockPos.add(0, -1, 1)).getBlock() == SculkVeinBlock.SCULK_VEIN) {
                    currentSculk = world.getBlockState(blockPos.add(0, -1, 1));
                    world.setBlockState(blockPos.add(0, -1, 1), currentSculk.with(Properties.NORTH, true));
                }
            } else if (world.getBlockState(blockPos.add(0, -1, 1)).getBlock() == SculkVeinBlock.SCULK_VEIN) {
                currentSculk = world.getBlockState(blockPos.add(0, -1, 1));
                world.setBlockState(blockPos.add(0, -1, 1), currentSculk.with(Properties.NORTH, true));
            }
        }
    }
    public void veinPlaceOptim(BlockPos curr, World world) {
        if (SculkTags.ALWAYS_WATER.contains(world.getBlockState(curr).getBlock()) || world.getBlockState(curr)==Blocks.WATER.getDefaultState()) {
            world.setBlockState(curr, vein.with(Properties.WATERLOGGED, true));
            tiltVeins(curr, world);
            tiltVeinsDown(curr, world);
        } else if (world.getBlockState(curr).getBlock() != Blocks.WATER) {
            if (world.getBlockState(curr).getBlock() == SculkVeinBlock.SCULK_VEIN) {
                world.setBlockState(curr, world.getBlockState(curr).with(Properties.DOWN, true));
                tiltVeins(curr, world);
                tiltVeinsDown(curr, world);
            } else
                world.setBlockState(curr, vein);
            tiltVeins(curr, world);
            tiltVeinsDown(curr, world);
        }
    }

    public void veins(BlockPos blockPos, World world) {
        if (SculkTags.SCULK_REPLACEABLE.contains(world.getBlockState(blockPos.add(1, 1, 0)).getBlock()) && solid(world, blockPos.add(1, 0, 0)) && airveins(world, blockPos.add(1, 0, 0))) {
                veinPlaceOptim(blockPos.add(1, 1, 0), world);
            } else if (sculkCheck(blockPos.add(1, 1, 0), world, blockPos) != blockPos.add(1, 1, 0) && solidrep(world, sculkCheck(blockPos.add(1, 1, 0), world, blockPos)) && airveins(world, sculkCheck(blockPos.add(1, 1, 0), world, blockPos))) {
                    veinPlaceOptim(sculkCheck(blockPos.add(1, 1, 0), world, blockPos).up(), world);
            }
            if (SculkTags.SCULK_REPLACEABLE.contains(world.getBlockState(blockPos.add(-1, 1, 0)).getBlock()) && solid(world, blockPos.add(-1, 0, 0)) && airveins(world, blockPos.add(-1, 0, 0))) {
                    veinPlaceOptim(blockPos.add(-1,1,0), world);
            } else if (sculkCheck(blockPos.add(-1, 1, 0), world, blockPos) != blockPos.add(-1, 1, 0) && solidrep(world, sculkCheck(blockPos.add(-1, 1, 0), world, blockPos)) && airveins(world, sculkCheck(blockPos.add(-1, 1, 0), world, blockPos))) {
                    veinPlaceOptim(sculkCheck(blockPos.add(-1,1,0), world, blockPos).up(), world);
            }
            if (SculkTags.SCULK_REPLACEABLE.contains(world.getBlockState(blockPos.add(0, 1, 1)).getBlock()) && solid(world, blockPos.add(0, 0, 1)) && airveins(world, blockPos.add(0, 0, 1))) {
                    veinPlaceOptim(blockPos.add(0,1,1), world);
            } else if (sculkCheck(blockPos.add(0, 1, 1), world, blockPos) != blockPos.add(0, 1, 1) && solidrep(world, sculkCheck(blockPos.add(0, 1, 1), world, blockPos)) && airveins(world, sculkCheck(blockPos.add(0, 1, 1), world, blockPos))) {
                    veinPlaceOptim(sculkCheck(blockPos.add(0,1,0), world, blockPos).up(), world);
            }
            if (SculkTags.SCULK_REPLACEABLE.contains(world.getBlockState(blockPos.add(0, 1, -1)).getBlock()) && solid(world, blockPos.add(0, 0, -1)) && airveins(world, blockPos.add(0, 0, -1))) {
                    veinPlaceOptim(blockPos.add(0, 1, -1), world);
            } else if (sculkCheck(blockPos.add(0, 1, -1), world, blockPos) != blockPos.add(0, 1, -1) && solidrep(world, sculkCheck(blockPos.add(0, 1, -1), world, blockPos)) && airveins(world, sculkCheck(blockPos.add(0, 1, -1), world, blockPos))) {
                    veinPlaceOptim(sculkCheck(blockPos.add(0,1,-1), world, blockPos).up(), world);
            }
            if (SculkTags.SCULK_REPLACEABLE.contains(world.getBlockState(blockPos.up()).getBlock()) && solid(world, blockPos.add(0, 0, 0)) && airveins(world, blockPos.add(0, 0, 0))) {
                    veinPlaceOptim(blockPos.add(0, 1, 0), world);
            } else if (sculkCheck(blockPos.up(), world, blockPos) != blockPos.up() && solidrep(world, sculkCheck(blockPos.up(), world, blockPos)) && airveins(world, sculkCheck(blockPos.up(), world, blockPos))) {
                    veinPlaceOptim(sculkCheck(blockPos.up(), world, blockPos).up(), world);
            }
        }

    public void listen(World world, GameEvent gameEvent, BlockPos blockPos, BlockPos blockPos2, @Nullable Entity entity) {
        this.event = Optional.of(gameEvent);
        if (world instanceof ServerWorld) {
            this.delay = 2;
           // ChunkGenerator manager = ((ServerWorld) world).toServerWorld().getChunkManager().getChunkGenerator();
           // Random random = new Random();
           // SculkSpreadFeatures.SCULK_PATCH_SPREAD.generate((ServerWorld) world, manager, random, blockPos.up());
            if (world.getGameRules().getBoolean(WildMod.DO_CATALYST_POLLUTION)) {
                sculk(blockPos, world, entity);
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(blockPos2);
                for (ServerPlayerEntity player : PlayerLookup.around((ServerWorld) world, blockPos2, 32)) {
                    ServerPlayNetworking.send(player, RegisterAccurateSculk.CATALYST_PARTICLE_PACKET, buf);
                }
                if (world.getGameRules().getBoolean(WildMod.DO_CATALYST_VIBRATIONS)) {
                ((ServerWorld)world).sendVibrationPacket(new Vibration(blockPos, this.positionSource, this.delay));
            }
            } else if (world.getGameRules().getBoolean(WildMod.DO_CATALYST_POLLUTION)) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(blockPos2);
                for (ServerPlayerEntity player : PlayerLookup.around((ServerWorld) world, blockPos2, 32)) {
                    ServerPlayNetworking.send(player, RegisterAccurateSculk.CATALYST_PARTICLE_PACKET, buf);
                }
                pseudoSculk(world, entity);
                if (world.getGameRules().getBoolean(WildMod.DO_CATALYST_VIBRATIONS)) {
                    ((ServerWorld)world).sendVibrationPacket(new Vibration(blockPos, this.positionSource, this.delay));
                }
            }
        }
    }


    public interface Callback {
        boolean accepts(World var1, GameEventListener var2, BlockPos var3, GameEvent var4, @Nullable Entity var5);

        void accept(World var1, GameEventListener var2, GameEvent var3, int var4);
    }
}
