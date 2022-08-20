package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.entity.WildVibration;
import net.frozenblock.wildmod.event.WildGameEvent;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.SculkSensorListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SculkSensorBlockEntity.class)
public class SculkSensorBlockEntityMixin {
    @Shadow
    @Final
    private SculkSensorListener listener;

    @Inject(method = "accept", at = @At("HEAD"))
    private void accept(World world, GameEventListener listener, GameEvent event, int distance, CallbackInfo ci) {
        SculkSensorBlockEntity sculkSensor = SculkSensorBlockEntity.class.cast(this);
        if (!world.isClient() && world instanceof ServerWorld && SculkSensorBlock.isInactive(sculkSensor.getCachedState())) {
            WildVibration.Instance vibration = WildVibration.Instance.of(this.listener);
            world.emitGameEvent(vibration.getEntity(), WildGameEvent.SCULK_SENSOR_TENDRILS_CLICKING, vibration.getPos());
        }
    }
}
