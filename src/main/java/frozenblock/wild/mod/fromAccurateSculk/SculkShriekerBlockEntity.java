package frozenblock.wild.mod.fromAccurateSculk;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.block.SculkShriekerBlock;
import frozenblock.wild.mod.block.entity.SculkShriekerWarningManager;
import frozenblock.wild.mod.entity.util.LargeEntitySpawnHelper;
import frozenblock.wild.mod.event.EntityPositionSource;
import frozenblock.wild.mod.event.PositionSource;
import frozenblock.wild.mod.event.SculkSensorListener;
import frozenblock.wild.mod.event.WildEventTags;
import frozenblock.wild.mod.registry.RegisterAccurateSculk;
import frozenblock.wild.mod.registry.RegisterEntities;
import frozenblock.wild.mod.registry.RegisterSounds;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;


public class SculkShriekerBlockEntity extends BlockEntity implements SculkSensorListener.Callback {
    private static final Logger field_38237 = LogUtils.getLogger();
    private static final int RANGE = 8;
    private static final int field_38750 = 10;
    private static final int field_38751 = 20;
    private static final int field_38752 = 5;
    private static final int field_38753 = 6;
    private static final int field_38754 = 40;
    private static final Int2ObjectMap<SoundEvent> WARNING_SOUNDS = Util.make(new Int2ObjectOpenHashMap<>(), warningSounds -> {
        warningSounds.put(1, RegisterSounds.ENTITY_WARDEN_NEARBY_CLOSE);
        warningSounds.put(2, RegisterSounds.ENTITY_WARDEN_NEARBY_CLOSER);
        warningSounds.put(3, RegisterSounds.ENTITY_WARDEN_NEARBY_CLOSEST);
    });
    private static final int field_38756 = 90;
    private int warningLevel;
    private SculkSensorListener vibrationListener = new SculkSensorListener((PositionSource) new BlockPositionSource(this.pos), 8, this, null, 0, 0);

    public SculkShriekerBlockEntity(BlockPos pos, BlockState state) {
        super(WildBlockEntityType.SCULK_SHRIEKER, pos, state);
        this.listener = new SculkShriekerListener(new BlockPositionSource(this.pos), ((SculkShriekerBlock)state.getBlock()).getRange(), this);
    }

    public SculkSensorListener getVibrationListener() {
        return this.vibrationListener;
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("warning_level", 99)) {
            this.warningLevel = nbt.getInt("warning_level");
        }

        if (nbt.contains("listener", 10)) {
            SculkSensorListener.createCodec(this)
                .parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompound("listener")))
                .resultOrPartial(field_38237::error)
                .ifPresent(vibrationListener -> this.vibrationListener = vibrationListener);
        }
    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("warning_level", this.warningLevel);
        SculkSensorListener.createCodec(this)
            .encodeStart(NbtOps.INSTANCE, this.vibrationListener)
            .resultOrPartial(field_38237::error)
            .ifPresent(nbtElement -> nbt.put("listener", nbtElement));
    }

    public TagKey<GameEvent> getTag() {
        return WildEventTags.SHRIEKER_CAN_LISTEN;
    }

    @Override
    public boolean accepts(ServerWorld world, frozenblock.wild.mod.event.GameEventListener listener, BlockPos pos, frozenblock.wild.mod.event.GameEvent event, @Nullable frozenblock.wild.mod.event.GameEvent.Emitter emitter) {
        return this.canWarn(world);
    }

    @Override
    public void accept(
            ServerWorld world, frozenblock.wild.mod.event.GameEventListener listener, BlockPos pos, frozenblock.wild.mod.event.GameEvent event, @Nullable Entity entity, @Nullable Entity sourceEntity, int delay
    ) {
        this.shriek(world);
    }

    private boolean canWarn(ServerWorld world) {
        BlockState blockState = this.getCachedState();
        if (blockState.get(SculkShriekerBlock.SHRIEKING)) {
            return false;
        } else if (!blockState.get(SculkShriekerBlock.CAN_SUMMON)) {
            return true;
        } else {
            BlockPos blockPos = this.getPos();
            return getClosestPlayerWarningManager(world, blockPos).map(warningManager -> warningManager.canIncreaseWarningLevel(world, blockPos)).orElse(false);
        }
    }

    public void shriek(ServerWorld world) {
        BlockState blockState = this.getCachedState();
        if (this.canWarn(world) && this.trySyncWarningLevel(world, blockState)) {
            BlockPos blockPos = this.getPos();
            world.setBlockState(blockPos, (BlockState)blockState.with(SculkShriekerBlock.SHRIEKING, true), 2);
            world.createAndScheduleBlockTick(blockPos, blockState.getBlock(), 90);
            world.syncWorldEvent(3007, blockPos, 0);
        }

    }

    /*private boolean trySyncWarningLevel(ServerWorld world, BlockState state) {
        if (state.get(SculkShriekerBlock.CAN_SUMMON)) {
            BlockPos blockPos = this.getPos();
            Optional<SculkShriekerWarningManager> optional = getClosestPlayerWarningManager(world, blockPos)
                    .filter(warningManager -> warningManager.warnNearbyPlayers(world, blockPos));
            if (optional.isEmpty()) {
                return false;
            }

            this.warningLevel = ((SculkShriekerWarningManager)optional.get()).getWarningLevel();
        }

        return true;
    }

    private static Optional<SculkShriekerWarningManager> getClosestPlayerWarningManager(ServerWorld world, BlockPos pos) {
        PlayerEntity playerEntity = world.getClosestPlayer(
                (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 16.0, EntityPredicates.EXCEPT_SPECTATOR.and(Entity::isAlive)
        );
        return playerEntity == null ? Optional.empty() : Optional.of(playerEntity.getSculkShriekerWarningManager());
    }

    public void warn(ServerWorld world) {
        if (this.getCachedState().get(SculkShriekerBlock.CAN_SUMMON)) {
            WardenEntity.addDarknessToClosePlayers(world, Vec3d.ofCenter(this.getPos()), null, 40);
            if (this.warningLevel >= 3) {
                trySpawnWarden(world, this.getPos());
                return;
            }
        }

        this.playWarningSound();
    }

    private void playWarningSound() {
        SoundEvent soundEvent = (SoundEvent)WARNING_SOUNDS.get(this.warningLevel);
        if (soundEvent != null) {
            BlockPos blockPos = this.getPos();
            int i = blockPos.getX() + MathHelper.nextBetween(this.world.random, -10, 10);
            int j = blockPos.getY() + MathHelper.nextBetween(this.world.random, -10, 10);
            int k = blockPos.getZ() + MathHelper.nextBetween(this.world.random, -10, 10);
            this.world.playSound(null, i, (double)j, (double)k, soundEvent, SoundCategory.HOSTILE, 5.0F, 1.0F);
        }

    }

    private static void trySpawnWarden(ServerWorld world, BlockPos pos) {
        if (world.getGameRules().getBoolean(WildMod.DO_WARDEN_SPAWNING.)) {
            LargeEntitySpawnHelper.trySpawnAt(RegisterEntities.WARDEN, SpawnReason.TRIGGERED, world, pos, 20, 5, 6)
                    .ifPresent(entity -> entity.playSound(RegisterSounds.ENTITY_WARDEN_AGITATED, 5.0F, 1.0F));
        }

    }

    public void onListen() {
        this.markDirty();
    }


    */private final SculkShriekerListener listener;

    int ticks;
    int prevTick;
    int direction;
    int shrieks;
    boolean stepped;
    int lastVibrationFrequency;

    public int getTicks() {
        return ticks;
    }
    public int getShrieks() {
        return shrieks;
    }
    public int getPrevTick() {
        return prevTick;
    }
    public int getDirection() {
        return direction;
    }
    public void setTicks(int i) {
        ticks = i;
    }
    public void setShrieks(int i) {
        shrieks = i;
    }
    public void setPrevTick(int i) {
        prevTick = i;
    }
    public void setDirection(int i) {
        direction = i;
        this.markDirty();
    }

    public boolean getStepped() {
        return stepped;
    }
    public void setStepped(boolean bl) {
        stepped = bl;
    }

    public SculkShriekerListener getEventListener() {
        return this.listener;
    }

    public int getLastVibrationFrequency() {
        return this.lastVibrationFrequency;
    }

    public void setLastVibrationFrequency(int i) {
        this.lastVibrationFrequency=i;
    }

    public void getDir(World world, BlockPos blockPos, BlockPos blockPos2) {
        if (SculkShriekerBlock.isInactive(this.getCachedState())) {
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
                    direction = 3;
                    this.markDirty();
                } else if (distX > 0) {
                    direction = 4;
                    this.markDirty();
                }
            } else if (AZ > AX) {
                if (distZ < 0) {
                    direction = 1;
                    this.markDirty();
                } else if (distZ > 0) {
                    direction = 2;
                    this.markDirty();
                }
            } else if (AZ == AX) {
                if (distX < 0 && distZ < 0) {
                    if ((UniformIntProvider.create(1, 2).get(world.getRandom())) > 1.5) {
                        direction = 1;
                        this.markDirty();
                    } else if ((UniformIntProvider.create(1, 2).get(world.getRandom())) < 1.5) {
                        direction = 3;
                        this.markDirty();
                    }
                } else if (distX > 0 && distZ > 0) {
                    if ((UniformIntProvider.create(1, 2).get(world.getRandom())) > 1.5) {
                        direction = 2;
                        this.markDirty();
                    } else if ((UniformIntProvider.create(1, 2).get(world.getRandom())) < 1.5) {
                        direction = 4;
                        this.markDirty();
                    }
                } else if (distX < 0 && distZ > 0) {
                    if ((UniformIntProvider.create(1, 2).get(world.getRandom())) > 1.5) {
                        direction = 3;
                        this.markDirty();
                    } else if ((UniformIntProvider.create(1, 2).get(world.getRandom())) < 1.5) {
                        direction = 2;
                        this.markDirty();
                    }
                } else if (distX > 0 && distZ < 0) {
                    if ((UniformIntProvider.create(1, 2).get(world.getRandom())) > 1.5) {
                        direction = 4;
                        this.markDirty();
                    } else if ((UniformIntProvider.create(1, 2).get(world.getRandom())) < 1.5) {
                        direction = 1;
                        this.markDirty();
                    }
                }
            } else direction = (UniformIntProvider.create(1, 4).get(world.getRandom()));
            this.markDirty();
        }
    }

    @Override
    public void accept(World world, GameEventListener gameEventListener, GameEvent gameEvent, int i) {
        BlockState blockState = this.getCachedState();
        if (!world.isClient() && SculkShriekerBlock.isInactive(blockState) && gameEvent== RegisterAccurateSculk.CLICK) {
            SculkShriekerBlock.setActive(world, this.pos, blockState);
        }
    }

    public static int getPower(int i, int j) {
        double d = (double)i / (double)j;
        return Math.max(1, 15 - MathHelper.floor(d * 15.0));
    }

}

