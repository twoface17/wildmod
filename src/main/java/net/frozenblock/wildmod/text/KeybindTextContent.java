package net.frozenblock.wildmod.text;

import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class KeybindTextContent implements TextContent {
    private final String key;
    @Nullable
    private Supplier<Text> translated;

    public KeybindTextContent(String key) {
        this.key = key;
    }

    private Text getTranslated() {
        if (this.translated == null) {
            this.translated = (Supplier)KeybindTranslations.FACTORY.apply(this.key);
        }

        return this.translated.get();
    }

    public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
        return this.getTranslated().visit(visitor);
    }

    public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
        return this.getTranslated().visit(visitor, style);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else {
            if (object instanceof KeybindTextContent keybindTextContent && this.key.equals(keybindTextContent.key)) {
                return true;
            }

            return false;
        }
    }

    public int hashCode() {
        return this.key.hashCode();
    }

    public String toString() {
        return "keybind{" + this.key + "}";
    }

    public String getKey() {
        return this.key;
    }
}
