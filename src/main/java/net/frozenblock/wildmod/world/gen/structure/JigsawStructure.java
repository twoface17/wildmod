/*package net.frozenblock.wildmod.world.gen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.heightprovider.HeightProvider;

import java.util.Optional;
import java.util.function.Function;

public final class JigsawStructure extends net.frozenblock.wildmod.world.gen.StructureType {
    public static final int MAX_SIZE = 128;
    public static final Codec<JigsawStructure> CODEC = RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                                    configCodecBuilder(instance),
                                    StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(feature -> feature.startPool),
                                    Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(jigsawStructure -> jigsawStructure.field_39059),
                                    Codec.intRange(0, 7).fieldOf("size").forGetter(feature -> feature.size),
                                    HeightProvider.CODEC.fieldOf("start_height").forGetter(feature -> feature.startHeight),
                                    Codec.BOOL.fieldOf("use_expansion_hack").forGetter(feature -> feature.useExpansionHack),
                                    Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(feature -> feature.projectStartToHeightmap),
                                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(feature -> feature.maxDistanceFromCenter)
                            )
                            .apply(instance, JigsawStructure::new)
            )
            .flatXmap(createValidator(), createValidator())
            .codec();
    private final RegistryEntry<StructurePool> startPool;
    private final Optional<Identifier> field_39059;
    private final int size;
    private final HeightProvider startHeight;
    private final boolean useExpansionHack;
    private final Optional<Heightmap.Type> projectStartToHeightmap;
    private final int maxDistanceFromCenter;

    private static Function<JigsawStructure, DataResult<JigsawStructure>> createValidator() {
        return feature -> {
            int i = switch(feature.getTerrainAdaptation()) {
                case NONE -> 0;
                case BURY, BEARD_THIN, BEARD_BOX -> 12;
                default -> throw new IncompatibleClassChangeError();
            };
            return feature.maxDistanceFromCenter + i > 128
                    ? DataResult.error("Structure size including terrain adaptation must not exceed 128")
                    : DataResult.success(feature);
        };
    }

    public JigsawStructure(
            Config config,
            RegistryEntry<StructurePool> startPool,
            Optional<Identifier> optional,
            int i,
            HeightProvider heightProvider,
            boolean bl,
            Optional<Heightmap.Type> optional2,
            int j
    ) {
        super(config);
        this.startPool = startPool;
        this.field_39059 = optional;
        this.size = i;
        this.startHeight = heightProvider;
        this.useExpansionHack = bl;
        this.projectStartToHeightmap = optional2;
        this.maxDistanceFromCenter = j;
    }

    public JigsawStructure(
            Config config, RegistryEntry<StructurePool> startPool, int size, HeightProvider startHeight, boolean useExpansionHack, Heightmap.Type projectStartToHeightmap
    ) {
        this(config, startPool, Optional.empty(), size, startHeight, useExpansionHack, Optional.of(projectStartToHeightmap), 80);
    }

    public JigsawStructure(Config config, RegistryEntry<StructurePool> startPool, int size, HeightProvider startHeight, boolean useExpansionHack) {
        this(config, startPool, Optional.empty(), size, startHeight, useExpansionHack, Optional.empty(), 80);
    }

    public Optional<StructurePosition> getStructurePosition(Context context) {
        ChunkPos chunkPos = context.chunkPos();
        int i = this.startHeight.get(context.random(), new HeightContext(context.chunkGenerator(), context.world()));
        BlockPos blockPos = new BlockPos(chunkPos.getStartX(), i, chunkPos.getStartZ());
        StructurePools.initDefaultPools();
        return StructurePoolBasedGenerator.generate(
                context, this.startPool, this.field_39059, this.size, blockPos, this.useExpansionHack, this.projectStartToHeightmap, this.maxDistanceFromCenter
        );
    }

    public net.frozenblock.wildmod.world.gen.structure.StructureType<?> getType() {
        return net.frozenblock.wildmod.world.gen.structure.StructureType.JIGSAW;
    }
}
*/