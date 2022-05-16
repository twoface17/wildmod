package net.frozenblock.wildmod.world.gen.root;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record MangroveRootPlacement(RegistryEntryList<Block> canGrowThrough, RegistryEntryList<Block> muddyRootsIn, BlockStateProvider muddyRootsProvider, int maxRootWidth, int maxRootLength, float randomSkewChance) {
    public static final Codec<MangroveRootPlacement> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(RegistryCodecs.entryList(Registry.BLOCK_KEY).fieldOf("can_grow_through").forGetter((rootPlacement) -> {
            return rootPlacement.canGrowThrough;
        }), RegistryCodecs.entryList(Registry.BLOCK_KEY).fieldOf("muddy_roots_in").forGetter((rootPlacement) -> {
            return rootPlacement.muddyRootsIn;
        }), BlockStateProvider.TYPE_CODEC.fieldOf("muddy_roots_provider").forGetter((rootPlacement) -> {
            return rootPlacement.muddyRootsProvider;
        }), Codec.intRange(1, 12).fieldOf("max_root_width").forGetter((rootPlacement) -> {
            return rootPlacement.maxRootWidth;
        }), Codec.intRange(1, 64).fieldOf("max_root_length").forGetter((rootPlacement) -> {
            return rootPlacement.maxRootLength;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("random_skew_chance").forGetter((rootPlacement) -> {
            return rootPlacement.randomSkewChance;
        })).apply(instance, (MangroveRootPlacement::new));
    });

    public MangroveRootPlacement(RegistryEntryList<Block> canGrowThrough, RegistryEntryList<Block> muddyRootsIn, BlockStateProvider muddyRootsProvider, int maxRootWidth, int maxRootLength, float randomSkewChance) {
        this.canGrowThrough = canGrowThrough;
        this.muddyRootsIn = muddyRootsIn;
        this.muddyRootsProvider = muddyRootsProvider;
        this.maxRootWidth = maxRootWidth;
        this.maxRootLength = maxRootLength;
        this.randomSkewChance = randomSkewChance;
    }

    public RegistryEntryList<Block> canGrowThrough() {
        return this.canGrowThrough;
    }

    public RegistryEntryList<Block> muddyRootsIn() {
        return this.muddyRootsIn;
    }

    public BlockStateProvider muddyRootsProvider() {
        return this.muddyRootsProvider;
    }

    public int maxRootWidth() {
        return this.maxRootWidth;
    }

    public int maxRootLength() {
        return this.maxRootLength;
    }

    public float randomSkewChance() {
        return this.randomSkewChance;
    }
}
