package net.frozenblock.wildmod.world.gen.structure;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.wildmod.WildMod;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.*;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.random.AbstractRandom;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class WildStructurePoolElement {
    public static final Codec<net.minecraft.structure.pool.StructurePoolElement> CODEC = Registry.STRUCTURE_POOL_ELEMENT
            .getCodec()
            .dispatch("element_type", net.minecraft.structure.pool.StructurePoolElement::getType, StructurePoolElementType::codec);
    @Nullable
    private volatile StructurePool.Projection projection;

    protected static <E extends net.minecraft.structure.pool.StructurePoolElement> RecordCodecBuilder<E, StructurePool.Projection> projectionGetter() {
        return StructurePool.Projection.CODEC.fieldOf("projection").forGetter(net.minecraft.structure.pool.StructurePoolElement::getProjection);
    }

    protected WildStructurePoolElement(StructurePool.Projection projection) {
        this.projection = projection;
    }

    public abstract Vec3i getStart(StructureManager structureManager, BlockRotation rotation);

    public abstract List<Structure.StructureBlockInfo> getStructureBlockInfos(
            StructureManager structureManager, BlockPos pos, BlockRotation rotation, Random random
    );

    public abstract BlockBox getBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation);

    public abstract boolean generate(
            StructureManager structureManager,
            StructureWorldAccess world,
            StructureAccessor structureAccessor,
            ChunkGenerator chunkGenerator,
            BlockPos pos,
            BlockPos blockPos,
            BlockRotation rotation,
            BlockBox box,
            AbstractRandom random,
            boolean keepJigsaws
    );

    public abstract StructurePoolElementType<?> getType();

    public void method_16756(
            WorldAccess world, Structure.StructureBlockInfo structureBlockInfo, BlockPos pos, BlockRotation rotation, AbstractRandom random, BlockBox box
    ) {
    }

    public WildStructurePoolElement setProjection(StructurePool.Projection projection) {
        this.projection = projection;
        return this;
    }

    public StructurePool.Projection getProjection() {
        StructurePool.Projection projection = this.projection;
        if (projection == null) {
            throw new IllegalStateException();
        } else {
            return projection;
        }
    }

    public int getGroundLevelDelta() {
        return 1;
    }

    public static Function<StructurePool.Projection, EmptyPoolElement> ofEmpty() {
        return projection -> EmptyPoolElement.INSTANCE;
    }

    public static Function<StructurePool.Projection, net.frozenblock.wildmod.world.gen.structure.LegacySinglePoolElement> ofLegacySingle(String id) {
        return projection -> new net.frozenblock.wildmod.world.gen.structure.LegacySinglePoolElement(Either.left(new Identifier(WildMod.MOD_ID, id)), StructureProcessorLists.EMPTY, projection);
    }

    public static Function<StructurePool.Projection, net.frozenblock.wildmod.world.gen.structure.LegacySinglePoolElement> ofProcessedLegacySingle(String id, RegistryEntry<StructureProcessorList> registryEntry) {
        return projection -> new LegacySinglePoolElement(Either.left(new Identifier(WildMod.MOD_ID, id)), registryEntry, projection);
    }

    public static Function<StructurePool.Projection, WildSinglePoolElement> ofSingle(String id) {
        return projection -> new WildSinglePoolElement(Either.left(new Identifier(WildMod.MOD_ID, id)), StructureProcessorLists.EMPTY, projection);
    }

    public static Function<StructurePool.Projection, WildSinglePoolElement> ofProcessedSingle(String id, RegistryEntry<StructureProcessorList> registryEntry) {
        return projection -> new WildSinglePoolElement(Either.left(new Identifier(WildMod.MOD_ID, id)), registryEntry, projection);
    }

    public static Function<StructurePool.Projection, WildFeaturePoolElement> ofFeature(RegistryEntry<PlacedFeature> registryEntry) {
        return projection -> new WildFeaturePoolElement(registryEntry, projection);
    }

    public static Function<StructurePool.Projection, ListPoolElement> ofList(List<Function<StructurePool.Projection, ? extends StructurePoolElement>> list) {
        return projection -> new ListPoolElement(
                list.stream().map(function -> function.apply(projection)).collect(Collectors.toList()), projection
        );
    }
}
