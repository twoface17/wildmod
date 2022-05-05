package net.frozenblock.wildmod.world.gen.structure;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Deprecated
public class Codec<E extends Enum<E> & StringIdentifiable> implements com.mojang.serialization.Codec<E> {
    private final com.mojang.serialization.Codec<E> base;
    private final Function<String, E> idToIdentifiable;

    public Codec(E[] values, Function<String, E> idToIdentifiable) {
        this.base = Codecs.orCompressed(
                idChecked(StringIdentifiable::asString, idToIdentifiable),
                Codecs.rawIdChecked(Enum::ordinal, ordinal -> ordinal >= 0 && ordinal < values.length ? values[ordinal] : null, -1)
        );
        this.idToIdentifiable = idToIdentifiable;
    }

    public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> ops, T input) {
        return this.base.decode(ops, input);
    }

    public <T> DataResult<T> encode(E enum_, DynamicOps<T> dynamicOps, T object) {
        return this.base.encode(enum_, dynamicOps, object);
    }

    @Nullable
    public E byId(@Nullable String id) {
        return (E)this.idToIdentifiable.apply(id);
    }
    public static <E> com.mojang.serialization.Codec<E> idChecked(Function<E, String> elementToId, Function<String, E> idToElement) {

        return com.mojang.serialization.Codec.STRING
                .flatXmap(
                        id -> Optional.ofNullable(idToElement.apply(id))
                                .map(DataResult::success)
                                .orElseGet(() -> DataResult.error("Unknown element name:" + id)),
                        element -> Optional.ofNullable(elementToId.apply(element))
                                .map(DataResult::success)
                                .orElseGet(() -> DataResult.error("Element with unknown name: " + element))
                );
    }

    static <E extends Enum<E> & StringIdentifiable> Codec<E> createCodec(Supplier<E[]> enumValues) {
        E[] enums = (E[])enumValues.get();
        if (enums.length > 16) {
            Map<String, E> map = Arrays.stream(enums)
                    .collect(Collectors.toMap(identifiable -> ((StringIdentifiable)identifiable).asString(), enum_ -> enum_));
            return new Codec<>(enums, id -> id == null ? null : map.get(id));
        } else {
            return new Codec<>(enums, id -> {
                for(E enum_ : enums) {
                    if (enum_.asString().equals(id)) {
                        return enum_;
                    }
                }

                return null;
            });
        }
    }
}