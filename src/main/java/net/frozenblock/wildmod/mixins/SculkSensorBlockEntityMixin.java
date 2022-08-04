package net.frozenblock.wildmod.mixins;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.frozenblock.wildmod.event.VibrationListener;
import net.frozenblock.wildmod.event.WildBlockPositionSource;
import net.frozenblock.wildmod.event.WildGameEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(SculkSensorBlockEntity.class)
public class SculkSensorBlockEntityMixin extends BlockEntity implements VibrationListener.Callback {
    public SculkSensorBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private static final Logger LOGGER = LogUtils.getLogger();

    private static int getPower(float distance, int range) {
        double d = (double) distance / (double) range;
        return Math.max(1, 15 - MathHelper.floor(d * 15.0));
    }

    @Shadow
    private int lastVibrationFrequency;

    private VibrationListener vibrationListener;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void SculkSensorBlockEntity(BlockPos pos, BlockState state, CallbackInfo ci) {
        this.vibrationListener = new VibrationListener(new WildBlockPositionSource(this.pos), ((SculkSensorBlock) state.getBlock()).getRange(), this, null, 0.0F, 0);
    }

    /**
     * @author FrozenBlock
     * @reason make it use vibration listener
     */
    @Overwrite
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.lastVibrationFrequency = nbt.getInt("last_vibration_frequency");
        if (nbt.contains("listener", NbtElement.COMPOUND_TYPE)) {
            DataResult<VibrationListener> var10000 = VibrationListener.createCodec(this).parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompound("listener")));
            Logger var10001 = LOGGER;
            Objects.requireNonNull(var10001);
            var10000.resultOrPartial(var10001::error).ifPresent((listener) -> {
                this.vibrationListener = listener;
            });
        }

    }

    /**
     * @author FrozenBlock
     * @reason make it use vibration listener
     */
    @Overwrite
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("last_vibration_frequency", this.lastVibrationFrequency);
        DataResult<NbtElement> var10000 = VibrationListener.createCodec(this).encodeStart(NbtOps.INSTANCE, this.vibrationListener);
        Logger var10001 = LOGGER;
        Objects.requireNonNull(var10001);
        var10000.resultOrPartial(var10001::error).ifPresent((listenerNbt) -> {
            nbt.put("listener", listenerNbt);
        });
    }

    @Override
    public boolean accepts(ServerWorld world, GameEventListener listener, BlockPos pos, WildGameEvent event, @Nullable WildGameEvent.Emitter emitter) {
        SculkSensorBlockEntity sculk = SculkSensorBlockEntity.class.cast(this);
        return !sculk.isRemoved() && (!pos.equals(sculk.getPos()) || event != GameEvent.BLOCK_DESTROY && event != GameEvent.BLOCK_PLACE) && SculkSensorBlock.isInactive(sculk.getCachedState());
    }

    @Override
    public void accept(
            ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity sourceEntity, float distance
    ) {
        SculkSensorBlockEntity sculk = SculkSensorBlockEntity.class.cast(this);
        BlockState blockState = sculk.getCachedState();
        if (SculkSensorBlock.isInactive(blockState)) {
            this.lastVibrationFrequency = SculkSensorBlock.FREQUENCIES.getInt(event);
            SculkSensorBlock.setActive(/*entity, */world, sculk.getPos(), blockState, getPower(distance, listener.getRange()));
        }

    }
}
