package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.event.WildGameEvents;
import net.frozenblock.wildmod.event.GameEventListener;
import net.frozenblock.wildmod.event.VibrationListener;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SculkSensorBlockEntity.class)
public class SculkSensorBlockEntityMixin extends BlockEntity implements VibrationListener.Callback {
    public SculkSensorBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow public static int getPower(int distance, int range) {
        double d = (double)distance / (double)range;
        return Math.max(1, 15 - MathHelper.floor(d * 15.0));
    }

    @Shadow private int lastVibrationFrequency;

    @Override
    public boolean accepts(ServerWorld world, GameEventListener listener, BlockPos pos, WildGameEvents event, @Nullable WildGameEvents.Emitter emitter) {
        return !this.isRemoved() && (!pos.equals(this.getPos()) || event != net.minecraft.world.event.GameEvent.BLOCK_DESTROY && event != net.minecraft.world.event.GameEvent.BLOCK_PLACE)
                ? SculkSensorBlock.isInactive(this.getCachedState())
                : false;
    }

    @Override
    public void accept(
            ServerWorld world, GameEventListener listener, BlockPos pos, WildGameEvents event, @Nullable Entity entity, @Nullable Entity sourceEntity, int delay
    ) {
        BlockState blockState = this.getCachedState();
        if (SculkSensorBlock.isInactive(blockState)) {
            this.lastVibrationFrequency = SculkSensorBlock.FREQUENCIES.getInt(event);
            SculkSensorBlock.setActive(/*entity, */world, this.pos, blockState, getPower(delay, listener.getRange()));
        }

    }
}
