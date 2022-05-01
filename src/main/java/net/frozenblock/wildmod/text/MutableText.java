package net.frozenblock.wildmod.text;

import com.google.common.collect.Lists;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class MutableText implements Text {
    private final TextContent content;
    private final List<Text> siblings;
    private Style style;
    private OrderedText ordered = OrderedText.EMPTY;
    @Nullable
    private Language language;

    MutableText(TextContent content, List<Text> siblings, Style style) {
        this.content = content;
        this.siblings = siblings;
        this.style = style;
    }

    public static MutableText of(TextContent content) {
        return new MutableText(content, Lists.newArrayList(), Style.EMPTY);
    }

    public TextContent getContent() {
        return this.content;
    }

    public List<Text> getSiblings() {
        return this.siblings;
    }

    public MutableText setStyle(Style style) {
        this.style = style;
        return this;
    }

    public Style getStyle() {
        return this.style;
    }

    public MutableText append(String text) {
        return this.append(Text.literal(text));
    }

    public MutableText append(Text text) {
        this.siblings.add(text);
        return this;
    }

    public MutableText styled(UnaryOperator<Style> styleUpdater) {
        this.setStyle((Style)styleUpdater.apply(this.getStyle()));
        return this;
    }

    public MutableText fillStyle(Style styleOverride) {
        this.setStyle(styleOverride.withParent(this.getStyle()));
        return this;
    }

    public MutableText formatted(Formatting... formattings) {
        this.setStyle(this.getStyle().withFormatting(formattings));
        return this;
    }

    public MutableText formatted(Formatting formatting) {
        this.setStyle(this.getStyle().withFormatting(formatting));
        return this;
    }

    public OrderedText asOrderedText() {
        Language language = Language.getInstance();
        if (this.language != language) {
            this.ordered = language.reorder(this);
            this.language = language;
        }

        return this.ordered;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof MutableText)) {
            return false;
        } else {
            MutableText mutableText = (MutableText)object;
            return this.content.equals(mutableText.content) && this.style.equals(mutableText.style) && this.siblings.equals(mutableText.siblings);
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.content, this.style, this.siblings});
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.content.toString());
        boolean bl = !this.style.isEmpty();
        boolean bl2 = !this.siblings.isEmpty();
        if (bl || bl2) {
            stringBuilder.append('[');
            if (bl) {
                stringBuilder.append("style=");
                stringBuilder.append(this.style);
            }

            if (bl && bl2) {
                stringBuilder.append(", ");
            }

            if (bl2) {
                stringBuilder.append("siblings=");
                stringBuilder.append(this.siblings);
            }

            stringBuilder.append(']');
        }

        return stringBuilder.toString();
    }

    @Override
    public <T> Optional<T> visit(StyledVisitor<T> styledVisitor, net.minecraft.text.Style style) {
        return Optional.empty();
    }
}
