package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.event.WildGameEvent;
import net.minecraft.block.NoteBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoteBlock.class)
public class NoteBlockMixin {

    /**
     * @author FrozenBlock
     * @reason Emits a game event for the allay to listen to
     */
    @Inject(method = "playNote", at = @At("TAIL"))
    private void playNote(World world, BlockPos pos, CallbackInfo ci) {
        NoteBlock noteBlock = NoteBlock.class.cast(this);
        world.addSyncedBlockEvent(pos, noteBlock, 0, 0);
        world.emitGameEvent(WildGameEvent.NOTE_BLOCK_PLAY, pos);
    }
}
