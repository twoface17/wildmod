package net.frozenblock.wildmod.entity;

import com.mojang.serialization.Lifecycle;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.registry.Registry;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.registry.RegistryKey;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public record FrogVariant(Identifier texture) {
    public static final FrogVariant TEMPERATE = register("temperate", "textures/entity/frog/temperate_frog.png");
    public static final FrogVariant WARM = register("warm", "textures/entity/frog/warm_frog.png");
    public static final FrogVariant COLD = register("cold", "textures/entity/frog/cold_frog.png");

    private static FrogVariant register(String id, String textureId) {
        return Registry.register(Registry.FROG_VARIANT, new Identifier(WildMod.MOD_ID, id), new FrogVariant(new Identifier(WildMod.MOD_ID, textureId)));
    }
}