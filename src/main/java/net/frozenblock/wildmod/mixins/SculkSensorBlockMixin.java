package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.entity.WardenEntity;
import net.frozenblock.wildmod.event.GameEvent;
import net.frozenblock.wildmod.fromAccurateSculk.SensorLastEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(SculkSensorBlock.class)
public class SculkSensorBlockMixin {
    //FROM ACCURATE SCULK
    private ServerWorld world;

    public SculkSensorBlockMixin(ServerWorld world) {
        this.world = world;
    }

    @Inject(method = "setActive", at = @At("TAIL"))
    private static void setActive(World world, BlockPos pos, BlockState state, int power, CallbackInfo info) {
        world.emitGameEvent(GameEvent.SCULK_SENSOR_TENDRILS_CLICKING, pos.add(0.5, 0, 0.5));

        int lastEntity = SensorLastEntity.getLastEntity(pos);
        LivingEntity target = (LivingEntity) world.getEntityById(lastEntity);
        BlockPos lastEventPos = SensorLastEntity.getLastPos(pos);
        net.minecraft.world.event.GameEvent event = SensorLastEntity.getLastEvent(pos);
        if (event != null && lastEntity != -1 && lastEventPos != null && target != null) {
            if (SculkSensorBlock.FREQUENCIES.containsKey(event)) {
                Box box = new Box(pos.getX() - 18, pos.getY() - 18, pos.getZ() - 18, pos.getX() + 18, pos.getY() + 18, pos.getZ() + 18);
                List<WardenEntity> list = world.getNonSpectatingEntities(WardenEntity.class, box);
                Iterator<WardenEntity> var11 = list.iterator();
                WardenEntity wardenEntity;
                while (var11.hasNext()) {
                    wardenEntity = var11.next();
                    if (wardenEntity.getBlockPos().isWithinDistance(pos, 16)) {
                        if (event != net.minecraft.world.event.GameEvent.PROJECTILE_LAND && event != net.minecraft.world.event.GameEvent.BLOCK_DESTROY && event != net.minecraft.world.event.GameEvent.BLOCK_PLACE && event != net.minecraft.world.event.GameEvent.BLOCK_ATTACH
                                && event != net.minecraft.world.event.GameEvent.BLOCK_CHANGE && event != net.minecraft.world.event.GameEvent.BLOCK_CLOSE && event != net.minecraft.world.event.GameEvent.BLOCK_OPEN && event != net.minecraft.world.event.GameEvent.BLOCK_PRESS
                                && event != net.minecraft.world.event.GameEvent.BLOCK_SWITCH && event != net.minecraft.world.event.GameEvent.BLOCK_DETACH && event != net.minecraft.world.event.GameEvent.BLOCK_UNPRESS && event != net.minecraft.world.event.GameEvent.BLOCK_UNSWITCH
                                && event != net.minecraft.world.event.GameEvent.RING_BELL) {
                            wardenEntity.listen(lastEventPos, wardenEntity.getWorld(), target, wardenEntity.eventSuspicionValue(event, target), pos);
                        } else {
                            wardenEntity.listen(lastEventPos, wardenEntity.getWorld(), null, 1, pos);
                        }
                    }
                }
            }
        }
    }
}
