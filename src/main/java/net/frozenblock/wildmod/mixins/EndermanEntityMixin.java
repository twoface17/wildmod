package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.event.WildGameEvent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(EndermanEntity.class)
public class EndermanEntityMixin extends HostileEntity implements Angerable {

    protected EndermanEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "teleportTo(DDD)Z", at = @At("RETURN"))
    private void teleportTo(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);
        BlockState blockState = this.world.getBlockState(mutable);
        boolean bl = blockState.getMaterial().blocksMovement();
        boolean bl2 = blockState.getFluidState().isIn(FluidTags.WATER);
        if (bl && !bl2) {
            boolean bl3 = this.teleport(x, y, z, true);
            if (bl3) {
                this.world.emitGameEvent(this, WildGameEvent.TELEPORT, mutable);
            }
        }
    }

    @Shadow
    public int getAngerTime() {
        return 0;
    }

    @Shadow
    public void setAngerTime(int angerTime) {

    }

    @Nullable
    @Shadow
    public UUID getAngryAt() {
        return null;
    }

    @Shadow
    public void setAngryAt(@Nullable UUID angryAt) {

    }

    @Shadow
    public void chooseRandomAngerTime() {

    }
}
