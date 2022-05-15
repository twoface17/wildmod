package net.frozenblock.wildmod.block;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

import java.util.Random;

public class WildWorldEvents {
    /**
     * Sculk... charges? Sculk is weird.
     * <br>Spawns sculk charge particles.
     * <br>Called by {@link net.frozenblock.wildmod.world.gen.SculkSpreadManager#tick(WorldAccess, BlockPos, Random, boolean)}  SculkSpreadManager#tick}
     */
    public static final int SCULK_CHARGE = 3006;
    /**
     * A sculk shrieker shrieks.
     * <br>Spawns shriek particles and plays the shriek sound event.
     * <br>Called by {@link SculkShriekerBlock#scheduledTick(BlockState, ServerWorld, BlockPos, Random)}  SculkShriekerBlock#scheduledTick}
     */
    public static final int SCULK_SHRIEKS = 3007;
}
