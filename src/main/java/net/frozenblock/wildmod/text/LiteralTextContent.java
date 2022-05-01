/*package net.frozenblock.wildmod.text;

import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;

import java.util.Optional;

public record LiteralTextContent(String string) implements TextContent {
    public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
        return visitor.accept(this.string);
    }

    public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
        return visitor.accept(style, this.string);
    }

    public String toString() {
        return "literal{" + this.string + "}";
    }
}
*/