package frozenblock.wild.mod.mixins;

import frozenblock.wild.mod.liukrastapi.WardenGoal;
import frozenblock.wild.mod.registry.RegisterEntities;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.SimpleGameEventDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleGameEventDispatcher.class)
public class SimpleGameEventDispatcherMixin{

    @Shadow private final World world;

    public SimpleGameEventDispatcherMixin(World world) {
        this.world = world;
    }

    @Inject(at = @At("HEAD"), method = "dispatch")
    private void dispatch(GameEvent event, Entity entity, BlockPos pos, CallbackInfo ci) {
        if(SculkSensorBlock.FREQUENCIES.containsKey(event)) {
            if(entity == null) {
                WardenGoal.lasteventpos = pos;
                WardenGoal.lasteventWorld = this.world;
                WardenGoal.lastevententity = null;
            } else {
                if(entity.getType() == EntityType.PLAYER) {
                    if(event == GameEvent.STEP && entity.isSneaking()) {
                        WardenGoal.lasteventpos = null;
                        WardenGoal.lasteventWorld = null;
                        WardenGoal.lastevententity = null;
                    } else {
                        WardenGoal.lasteventpos = pos;
                        WardenGoal.lasteventWorld = entity.getEntityWorld();
                        WardenGoal.lastevententity = (LivingEntity) entity;
                    }
                } else if(entity.getType() != RegisterEntities.WARDEN) {
                    WardenGoal.lasteventpos = pos;
                    WardenGoal.lasteventWorld = entity.getEntityWorld();
                    if(entity.isLiving()) {
                        WardenGoal.lastevententity = (LivingEntity) entity;
                    } else {
                        WardenGoal.lastevententity = null;
                    }
                } else {
                    WardenGoal.lasteventpos = null;
                    WardenGoal.lasteventWorld = null;
                    WardenGoal.lastevententity = null;
                }
            }
        } else {
            WardenGoal.lasteventpos = null;
            WardenGoal.lasteventWorld = null;
            WardenGoal.lastevententity = null;
        }
    }

}
