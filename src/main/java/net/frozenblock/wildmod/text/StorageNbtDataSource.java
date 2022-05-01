/*package net.frozenblock.wildmod.text;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import java.util.stream.Stream;

public record StorageNbtDataSource(Identifier id) implements NbtDataSource {
    public Stream<NbtCompound> get(ServerCommandSource source) {
        NbtCompound nbtCompound = source.getServer().getDataCommandStorage().get(this.id);
        return Stream.of(nbtCompound);
    }

    public String toString() {
        return "storage=" + this.id;
    }
}
*/