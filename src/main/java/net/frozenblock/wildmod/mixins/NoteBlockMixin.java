package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.event.GameEvent;
import net.minecraft.block.NoteBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoteBlock.class)
public class NoteBlockMixin {

    /*/**
     * @author FrozenBlock
     * @reason allay thing
     */
    /*@Overwrite
    private void playNote(World world, BlockPos pos) {
        if (world.getBlockState(pos.up()).isAir()) {
            return;
        }

        NoteBlock noteBlock = NoteBlock.class.cast(this);
        world.addSyncedBlockEvent(pos, noteBlock, 0, 0);
        world.emitGameEvent(GameEvent.NOTE_BLOCK_PLAY, pos);
    }
*/}
