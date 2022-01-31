package frozenblock.wild.mod.fromAccurateSculk;

import frozenblock.wild.mod.blocks.SculkCatalystBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.SculkSensorListener;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class SculkCatalystBlockEntity extends BlockEntity implements SculkSensorListener.Callback {
    private final SculkCatalystListener listener;
    private int lastVibrationFrequency;
    public int lastSculkRange;

    public SculkCatalystBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(NewBlockEntityType.SCULK_CATALYST, blockPos, blockState);
        this.listener = new SculkCatalystListener(new BlockPositionSource(this.pos), ((SculkCatalystBlock)blockState.getBlock()).getRange(), this);
    }

    @Override
    public void readNbt(NbtCompound nbtCompound) {
        super.readNbt(nbtCompound);
        this.lastVibrationFrequency = nbtCompound.getInt("last_vibration_frequency");
        this.lastSculkRange = nbtCompound.getInt("lastSculkRange");
    }

    @Override
    protected void writeNbt(NbtCompound nbtCompound) {
        super.writeNbt(nbtCompound);
        nbtCompound.putInt("last_vibration_frequency", this.lastVibrationFrequency);
        nbtCompound.putInt("lastSculkRange", this.lastSculkRange);
    }

    public SculkCatalystListener getEventListener() {
        return this.listener;
    }


    @Override
    public boolean accepts(World world, GameEventListener gameEventListener, BlockPos blockPos, GameEvent gameEvent, @Nullable Entity entity) {
        boolean bl = gameEvent == GameEvent.BLOCK_DESTROY && blockPos.equals(this.getPos());
        boolean bl2 = gameEvent == GameEvent.BLOCK_PLACE && blockPos.equals(this.getPos());
        return !bl && !bl2 && SculkCatalystBlock.isInactive(this.getCachedState());
    }

    @Override
    public void accept(World world, GameEventListener gameEventListener, GameEvent gameEvent, int i) {
        BlockState blockState = this.getCachedState();
        if (!world.isClient() && SculkCatalystBlock.isInactive(blockState)) {
            SculkCatalystBlock.setActive(world, this.pos, blockState);
            double d = (double)this.pos.getX() + 0.5;
            double e = (double)this.pos.getY() +1;
            double f = (double)this.pos.getZ() + 0.5;
            Random random = new Random();
            BlockPos target = this.getPos();
        }
    }

    public static int getPower(int i, int j) {
        double d = (double)i / (double)j;
        return Math.max(1, 15 - MathHelper.floor(d * 15.0));
    }
}
