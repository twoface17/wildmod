package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.event.WildGameEvent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerEntity.class)
public abstract class ShulkerEntityMixin extends GolemEntity {

    protected ShulkerEntityMixin(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    protected abstract @Nullable Direction findAttachSide(BlockPos pos);

    @Inject(method = "tryTeleport", at = @At("RETURN"))
    private void tryTeleport(CallbackInfoReturnable<Boolean> cir) {
        if (!this.isAiDisabled() && this.isAlive()) {
            BlockPos blockPos = this.getBlockPos();

            for (int i = 0; i < 5; ++i) {
                BlockPos blockPos2 = blockPos.add(
                        MathHelper.nextBetween(this.random, -8, 8), MathHelper.nextBetween(this.random, -8, 8), MathHelper.nextBetween(this.random, -8, 8)
                );
                if (blockPos2.getY() > this.world.getBottomY()
                        && this.world.isAir(blockPos2)
                        && this.world.getWorldBorder().contains(blockPos2)
                        && this.world.isSpaceEmpty(this, new Box(blockPos2).contract(1.0E-6))) {
                    Direction direction = this.findAttachSide(blockPos2);
                    if (direction != null) {
                        this.world.emitGameEvent(this, WildGameEvent.TELEPORT, blockPos);
                    }
                }
            }
        }
    }
}
