package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.entity.render.EntityModelLayer;
import net.frozenblock.wildmod.entity.render.EntityModelLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(EntityModelLayers.class)
public interface EntityModelLayersAccessor {
    @Accessor("LAYERS")
    static Set<EntityModelLayer> getLayers() {
        throw new AssertionError("This should not occur!");
    }
}
