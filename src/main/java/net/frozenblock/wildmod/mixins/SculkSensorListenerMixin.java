package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.entity.WildVibration;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.SculkSensorListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SculkSensorListener.class)
public class SculkSensorListenerMixin implements WildVibration.Instance {

    private BlockPos pos;
    private Entity entity;
    private Entity source;
    private WildVibration vibration;

    @Inject(method = "listen(Lnet/minecraft/world/World;Lnet/minecraft/world/event/GameEvent;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/BlockPos;)Z", at = @At("TAIL"))
    public void listen(World world, GameEvent event, Entity entity, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        if (world instanceof ServerWorld server) {
            this.setPos(pos);
            this.setVibration(new WildVibration(entity));
            this.setEntity(this.vibration.getEntity(server).orElse(null));
            this.setSource(this.vibration.getOwner(server).orElse(null));
        }
    }

    @Override
    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public BlockPos getPos() {
        return this.pos;
    }

    @Override
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public void setSource(Entity entity) {
        this.source = entity;
    }

    @Override
    public Entity getSource() {
        return this.source;
    }

    @Override
    public void setVibration(WildVibration vibration) {
        this.vibration = vibration;
    }
}
