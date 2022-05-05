package net.frozenblock.wildmod.entity;

import com.mojang.serialization.Lifecycle;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public record FrogVariant(Identifier texture) {
    public FrogVariant(Identifier texture) {
        this.texture = texture;
    }

    public Identifier texture() {
        return this.texture;
    }

    private static <T> Registry<T> create(RegistryKey<? extends Registry<T>> key, Lifecycle experimental, DefaultEntryGetter <T> defaultEntryGetter) {
        return create(key, Lifecycle.experimental(), defaultEntryGetter);
    }

    private static <T> RegistryKey<Registry<T>> createRegistryKey(String registryId) {
        return RegistryKey.ofRegistry(new Identifier(registryId));
    }

    @FunctionalInterface
    private interface DefaultEntryGetter<T> {
        T run(Registry<T> registry);
    }



    public static final RegistryKey<Registry<FrogVariant>> FROG_VARIANT_KEY = createRegistryKey("frog_variant");


    public interface TrackedDataHandle<T> extends TrackedDataHandler<T> {
        void write(PacketByteBuf buf, T value);
        T read(PacketByteBuf buf);
        default TrackedData<T> create(int id) {
            return new TrackedData<>(id, this);
        }
        T copy(T value);

        static <T> TrackedDataHandler<T> of(final BiConsumer<PacketByteBuf, T> writer, final Function<PacketByteBuf, T> reader) {
            return new ImmutableHandler<T>() {
                @Override
                public void write(PacketByteBuf buf, T value) {
                    writer.accept(buf, value);
                }

                @Override
                public T read(PacketByteBuf buf) {
                    return reader.apply(buf);
                }
            };
        }

        static <T> TrackedDataHandler<Optional<T>> ofOptional(final BiConsumer<PacketByteBuf, T> writer, final Function<PacketByteBuf, T> reader) {
            return new ImmutableHandler<Optional<T>>() {
                public void write(PacketByteBuf packetByteBuf, Optional<T> optional) {
                    if (optional.isPresent()) {
                        packetByteBuf.writeBoolean(true);
                        writer.accept(packetByteBuf, optional.get());
                    } else {
                        packetByteBuf.writeBoolean(false);
                    }

                }

                public Optional<T> read(PacketByteBuf packetByteBuf) {
                    return packetByteBuf.readBoolean() ? Optional.of(reader.apply(packetByteBuf)) : Optional.empty();
                }
            };
        }

        static <T extends Enum<T>> TrackedDataHandler<T> ofEnum(Class<T> enum_) {
            return of(PacketByteBuf::writeEnumConstant, (buf) -> {
                return buf.readEnumConstant(enum_);
            });
        }

        static <T> TrackedDataHandler<T> of(IndexedIterable<T> registry, PacketByteBuf packetByteBuf) {
            return of((buf, value) -> {
                writeRegistryValue(registry, value, new PacketByteBuf(null));
            }, (buf) -> {
                return readRegistryValue(registry, packetByteBuf);
            });
        }

        public interface ImmutableHandler<T> extends TrackedDataHandler<T> {
            default T copy(T value) {
                return value;
            }
        }
        public static <T> void writeRegistryValue(IndexedIterable<T> registry, T value, PacketByteBuf packetByteBuf) {
            int i = registry.getRawId(value);
            if (i == -1) {
                throw new IllegalArgumentException("Can't find id for '" + value + "' in map " + registry);
            } else {
                packetByteBuf.writeVarInt(i);
            }
        }

        public static <T> T readRegistryValue(IndexedIterable<T> registry, PacketByteBuf packetByteBuf) {
            int i = packetByteBuf.readVarInt();
            return registry.get(i);
        }
        public default int readVarInt(PacketByteBuf packetByteBuf) {
            int i = 0;
            int j = 0;

            byte b;
            do {
                b = this.readByte(packetByteBuf);
                i |= (b & 127) << j++ * 7;
                if (j > 5) {
                    throw new RuntimeException("VarInt too big");
                }
            } while((b & 128) == 128);

            return i;
        }
        public default byte readByte(PacketByteBuf packetByteBuf) {
            return packetByteBuf.readByte();
        }
    }
}
