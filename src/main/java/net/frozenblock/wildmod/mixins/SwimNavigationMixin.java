package net.frozenblock.wildmod.mixins;

import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwimNavigation.class)
public class SwimNavigationMixin {

    @Inject(method = "canPathDirectlyThrough", at = @At("TAIL"), cancellable = true)
    protected void canPathDirectlyThrough(Vec3d origin, Vec3d target, CallbackInfoReturnable<Boolean> cir) {
        SwimNavigation swim = SwimNavigation.class.cast(this);
        cir.setReturnValue(doesNotCollide(swim.entity, origin, target));
    }

    private static boolean doesNotCollide(MobEntity entity, Vec3d startPos, Vec3d entityPos) {
        Vec3d vec3d = new Vec3d(entityPos.x, entityPos.y + (double) entity.getHeight() * 0.5, entityPos.z);
        return entity.world.raycast(new RaycastContext(startPos, vec3d, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity)).getType()
                == HitResult.Type.MISS;
    }
}
