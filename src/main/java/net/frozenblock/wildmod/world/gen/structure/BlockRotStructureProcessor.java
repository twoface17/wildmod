package net.frozenblock.wildmod.world.gen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

public class BlockRotStructureProcessor extends net.minecraft.structure.processor.BlockRotStructureProcessor {
    public static final Codec<BlockRotStructureProcessor> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            RegistryCodecs.entryList(Registry.BLOCK_KEY).optionalFieldOf("rottable_blocks").forGetter(processor -> processor.rottableBlocks),
                            Codec.floatRange(0.0F, 1.0F).fieldOf("integrity").forGetter(processor -> processor.integrity)
                    )
                    .apply(instance, BlockRotStructureProcessor::new)
    );
    private final Optional<RegistryEntryList<Block>> rottableBlocks;
    private final float integrity;

    public BlockRotStructureProcessor(TagKey<Block> rottableBlocks, float integrity) {
        this(Optional.of(Registry.BLOCK.getOrCreateEntryList(rottableBlocks)), integrity);
    }

    public BlockRotStructureProcessor(float integrity) {
        this(Optional.empty(), integrity);
    }

    private BlockRotStructureProcessor(Optional<RegistryEntryList<Block>> rottableBlocks, float integrity) {
        super(integrity);
        this.integrity = integrity;
        this.rottableBlocks = rottableBlocks;
    }

    @Nullable
    public Structure.StructureBlockInfo process(
            WorldView world, BlockPos pos, BlockPos pivot, Structure.StructureBlockInfo originalBlockInfo, Structure.StructureBlockInfo currentBlockInfo, StructurePlacementData data
    ) {
        Random random = data.getRandom(currentBlockInfo.pos);
        return (!this.rottableBlocks.isPresent() || originalBlockInfo.state.isIn(this.rottableBlocks.get()))
                && !(random.nextFloat() <= this.integrity)
                ? null
                : currentBlockInfo;
    }

    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLOCK_ROT;
    }
}
