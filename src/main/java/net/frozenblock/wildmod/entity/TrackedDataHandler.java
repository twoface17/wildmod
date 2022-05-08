package net.frozenblock.wildmod.entity;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.collection.Int2ObjectBiMap;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface TrackedDataHandler<T> extends net.minecraft.entity.data.TrackedDataHandler<T> {
    void write(WildPacketByteBuf buf, T value);

    T read(WildPacketByteBuf buf);

    default TrackedData<T> create(int id) {
        return new TrackedData<>(id, this);
    }

    T copy(T value);

    static <T> TrackedDataHandler<T> of(WildPacketByteBuf.class_7462<T> arg, WildPacketByteBuf.class_7461<T> arg2) {
        return new TrackedDataHandler.ImmutableHandler<T>() {
            @Override
            public void write(PacketByteBuf buf, T value) {
                arg.accept((WildPacketByteBuf) buf, value);
            }

            @Override
            public T read(PacketByteBuf buf) {
                return arg2.apply((WildPacketByteBuf) buf);
            }

            @Override
            public void write(WildPacketByteBuf buf, T value) {
                arg.accept(buf, value);
            }

            @Override
            public T read(WildPacketByteBuf buf) {
                return (T)arg2.apply(buf);
            }
        };
    }

    static <T> TrackedDataHandler<Optional<T>> ofOptional(WildPacketByteBuf.class_7462<T> arg, WildPacketByteBuf.class_7461<T> arg2) {
        return of(arg.asOptional(), arg2.asOptional());
    }

    static <T extends Enum<T>> TrackedDataHandler<T> ofEnum(Class<T> enum_) {
        return of(WildPacketByteBuf::writeEnumConstant, buf -> buf.readEnumConstant(enum_));
    }

    static <T> TrackedDataHandler<T> of(IndexedIterable<T> registry) {
        return of((buf, value) -> buf.writeRegistryValue(registry, value), buf -> buf.readRegistryValue(registry));
    }

    static final Int2ObjectBiMap<TrackedDataHandler<?>> DATA_HANDLERS = Int2ObjectBiMap.create(16);

    public static int getId(TrackedDataHandler<?> handler) {
        return DATA_HANDLERS.getRawId(handler);
    }

    public static void register(TrackedDataHandler<?> handler) {
        DATA_HANDLERS.add(handler);
    }

    @Nullable
    public static TrackedDataHandler<?> get(int id) {
        return DATA_HANDLERS.get(id);
    }

    public interface ImmutableHandler<T> extends TrackedDataHandler<T> {
        @Override
        default T copy(T value) {
            return value;
        }
    }
}
