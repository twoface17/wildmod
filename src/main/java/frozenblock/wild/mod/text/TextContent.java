package frozenblock.wild.mod.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface TextContent {
    TextContent EMPTY = new TextContent() {
        public String toString() {
            return "empty";
        }
    };

    default <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
        return Optional.empty();
    }

    default <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
        return Optional.empty();
    }

    default MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        return (MutableText) Text.of(String.valueOf(this));
    }
}
