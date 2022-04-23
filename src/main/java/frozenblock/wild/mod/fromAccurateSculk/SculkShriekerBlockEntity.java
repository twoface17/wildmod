package frozenblock.wild.mod.fromAccurateSculk;

import frozenblock.wild.mod.block.SculkShriekerBlock;
import frozenblock.wild.mod.registry.RegisterAccurateSculk;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.SculkSensorListener;
import org.jetbrains.annotations.Nullable;


public class SculkShriekerBlockEntity extends BlockEntity implements SculkSensorListener.Callback {
    private final SculkShriekerListener listener;

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

    public SculkShriekerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(WildBlockEntityType.SCULK_SHRIEKER, blockPos, blockState);
        this.listener = new SculkShriekerListener(new BlockPositionSource(this.pos), ((SculkShriekerBlock)blockState.getBlock()).getRange(), this);
    }

    @Override
    public void readNbt(NbtCompound nbtCompound) {
        super.readNbt(nbtCompound);
        this.ticks = nbtCompound.getInt("ticks");
        this.shrieks = nbtCompound.getInt("shrieks");
        this.direction = nbtCompound.getInt("direction");
        this.prevTick = nbtCompound.getInt("prevTick");
        this.lastVibrationFrequency = nbtCompound.getInt("last_vibration_frequency");
    }

    @Override
    protected void writeNbt(NbtCompound nbtCompound) {
        super.writeNbt(nbtCompound);
        nbtCompound.putInt("ticks", this.ticks);
        nbtCompound.putInt("shrieks", this.shrieks);
        nbtCompound.putInt("direction", this.direction);
        nbtCompound.putInt("prevTick", this.prevTick);
        nbtCompound.putInt("last_vibration_frequency", this.lastVibrationFrequency);
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

    @Override
    public boolean accepts(World world, GameEventListener gameEventListener, BlockPos blockPos, GameEvent gameEvent, @Nullable Entity entity) {
        boolean bl = gameEvent == GameEvent.BLOCK_DESTROY && blockPos.equals(this.getPos());
        boolean bl2 = gameEvent == GameEvent.BLOCK_PLACE && blockPos.equals(this.getPos());
        getDir(world, this.getPos(), blockPos);
        return !bl && !bl2 && SculkShriekerBlock.isInactive(this.getCachedState());
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
