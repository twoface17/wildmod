package net.frozenblock.wildmod.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import net.frozenblock.wildmod.entity.Entity;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;

public class SelectorTextContent implements TextContent {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String pattern;
    @Nullable
    private final EntitySelector selector;
    protected final Optional<Text> separator;

    public SelectorTextContent(String pattern, Optional<Text> separator) {
        this.pattern = pattern;
        this.separator = separator;
        this.selector = readSelector(pattern);
    }

    @Nullable
    private static EntitySelector readSelector(String pattern) {
        EntitySelector entitySelector = null;

        try {
            EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(pattern));
            entitySelector = entitySelectorReader.read();
        } catch (CommandSyntaxException var3) {
            LOGGER.warn("Invalid selector component: {}: {}", pattern, var3.getMessage());
        }

        return entitySelector;
    }

    public String getPattern() {
        return this.pattern;
    }

    @Nullable
    public EntitySelector getSelector() {
        return this.selector;
    }

    public Optional<Text> getSeparator() {
        return this.separator;
    }

    public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        if (source != null && this.selector != null) {
            Optional<? extends Text> optional = Texts.parse(source, this.separator, sender, depth);
            return Texts.join(this.selector.getEntities(source), optional, net.frozenblock.wildmod.entity.Entity::getDisplayName);
        } else {
            return Text.empty();
        }
    }

    public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
        return visitor.accept(style, this.pattern);
    }

    public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
        return visitor.accept(this.pattern);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else {
            if (object instanceof SelectorTextContent selectorTextContent
                    && this.pattern.equals(selectorTextContent.pattern)
                    && this.separator.equals(selectorTextContent.separator)) {
                return true;
            }

            return false;
        }
    }

    public int hashCode() {
        int i = this.pattern.hashCode();
        return 31 * i + this.separator.hashCode();
    }

    public String toString() {
        return "pattern{" + this.pattern + "}";
    }
}
