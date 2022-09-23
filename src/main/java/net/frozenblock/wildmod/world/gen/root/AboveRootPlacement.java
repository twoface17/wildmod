package net.frozenblock.wildmod.world.gen.root;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record AboveRootPlacement(BlockStateProvider aboveRootProvider, float aboveRootPlacementChance) {
    public static final Codec<AboveRootPlacement> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BlockStateProvider.TYPE_CODEC.fieldOf("above_root_provider").forGetter((aboveRootPlacement) -> {
            return aboveRootPlacement.aboveRootProvider;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("above_root_placement_chance").forGetter((aboveRootPlacement) -> {
            return aboveRootPlacement.aboveRootPlacementChance;
        })).apply(instance, (AboveRootPlacement::new));
    });
}
