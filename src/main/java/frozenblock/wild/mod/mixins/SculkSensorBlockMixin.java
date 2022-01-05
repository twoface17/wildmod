package frozenblock.wild.mod.mixins;

import frozenblock.wild.mod.fromAccurateSculk.ClickGameEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SculkSensorBlock.class)
public class SculkSensorBlockMixin {
    //FROM ACCURATE SCULK
    private ServerWorld world;

    public SculkSensorBlockMixin(ServerWorld world) {
        this.world = world;
    }

    @Inject(method = "setActive", at = @At("TAIL"))
    private static void setActive(World world, BlockPos blockPos, BlockState blockState, int i, CallbackInfo info) {
        world.emitGameEvent(ClickGameEvent.CLICK, blockPos.add(0.5, 0, 0.5));
    }
}
