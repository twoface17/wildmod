package net.frozenblock.wildmod.mixins;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {

    @Shadow
    @Final
    World world;

    @Shadow
    public abstract net.minecraft.world.event.listener.GameEventDispatcher getGameEventDispatcher(int ySectionCoord);

    @Shadow
    @Final
    private Int2ObjectMap<net.minecraft.world.event.listener.GameEventDispatcher> gameEventDispatchers;

}
