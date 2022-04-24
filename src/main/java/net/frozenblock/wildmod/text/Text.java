/*package frozenblock.wild.mod.text;

import com.google.common.collect.Lists;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.Message;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Text extends net.minecraft.text.Text {
    Style getStyle();

    String getContent();

    default String getString() {
        return net.minecraft.text.Text.super.getString();
    }

    default String asTruncatedString(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        this.visit(string -> {
            int j = length - stringBuilder.length();
            if (j <= 0) {
                return TERMINATE_VISIT;
            } else {
                stringBuilder.append(string.length() <= j ? string : string.substring(0, j));
                return Optional.empty();
            }
        });
        return stringBuilder.toString();
    }

    List<net.minecraft.text.Text> getSiblings();

    default MutableText copy() {
        return (MutableText) net.minecraft.text.Text.of(this.getContent());
    }

    default MutableText shallowCopy() {
        return new MutableText(this.getContent(), new ArrayList<>(this.getSiblings()), this.getStyle()) {
            @Override
            public Style getStyle() {
                return null;
            }

            @Override
            public String asString() {
                return null;
            }

            @Override
            public List<net.minecraft.text.Text> getSiblings() {
                return null;
            }

            @Override
            public MutableText copy() {
                return null;
            }

            @Override
            public MutableText shallowCopy() {
                return null;
            }

            @Override
            public OrderedText asOrderedText() {
                return null;
            }

            @Override
            public MutableText setStyle(Style style) {
                return null;
            }

            @Override
            public MutableText append(net.minecraft.text.Text text) {
                return null;
            }
        };
    }

    OrderedText asOrderedText();

    default <T> Optional<T> visit(StyledVisitor<T> styledVisitor, Style style) {
        Style style2 = this.getStyle().withParent(style);
        Optional<T> optional = this.getContent().visit(styledVisitor, style2);
        if (optional.isPresent()) {
            return optional;
        } else {
            for(net.minecraft.text.Text text : this.getSiblings()) {
                Optional<T> optional2 = text.visit(styledVisitor, style2);
                if (optional2.isPresent()) {
                    return optional2;
                }
            }

            return Optional.empty();
        }
    }

    default <T> Optional<T> visit(Visitor<T> visitor) {
        Optional<T> optional = this.getContent().visit(visitor);
        if (optional.isPresent()) {
            return optional;
        } else {
            for(net.minecraft.text.Text text : this.getSiblings()) {
                Optional<T> optional2 = text.visit(visitor);
                if (optional2.isPresent()) {
                    return optional2;
                }
            }

            return Optional.empty();
        }
    }

    default List<net.minecraft.text.Text> getWithStyle(Style style) {
        List<net.minecraft.text.Text> list = Lists.newArrayList();
        this.visit((styleOverride, text) -> {
            if (!text.isEmpty()) {
                list.add(literal(text).fillStyle(styleOverride));
            }

            return Optional.empty();
        }, style);
        return list;
    }

    static net.minecraft.text.Text of(@Nullable String string) {
        return (net.minecraft.text.Text)(string != null ? literal(string) : ScreenTexts.EMPTY);
    }

    static MutableText literal(String string) {
        return MutableText.of(new LiteralTextContent(string));
    }

    static MutableText translatable(String key) {
        return MutableText.of(new TranslatableTextContent(key));
    }

    static MutableText translatable(String key, Object... args) {
        return MutableText.of(new TranslatableTextContent(key, args));
    }

    static MutableText empty() {
        return MutableText.of(TextContent.EMPTY);
    }

    static MutableText keybind(String string) {
        return MutableText.of(new KeybindTextContent(string));
    }

    static MutableText nbt(String rawPath, boolean interpret, Optional<net.minecraft.text.Text> separator, NbtDataSource nbtDataSource) {
        return MutableText.of(new NbtTextContent(rawPath, interpret, separator, nbtDataSource));
    }

    static MutableText score(String name, String objective) {
        return MutableText.of(new ScoreTextContent(name, objective));
    }

    static MutableText selector(String pattern, Optional<net.minecraft.text.Text> separator) {
        return MutableText.of(new SelectorTextContent(pattern, separator));
    }

    public static class Serializer implements JsonDeserializer<MutableText>, JsonSerializer<net.minecraft.text.Text> {
        private static final Gson GSON = (Gson) Util.make(() -> {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.disableHtmlEscaping();
            gsonBuilder.registerTypeHierarchyAdapter(net.minecraft.text.Text.class, new net.minecraft.text.Text.Serializer());
            gsonBuilder.registerTypeHierarchyAdapter(Style.class, new net.minecraft.text.Style.Serializer());
            gsonBuilder.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory());
            return gsonBuilder.create();
        });
        private static final Field JSON_READER_POS = (Field)Util.make(() -> {
            try {
                new JsonReader(new StringReader(""));
                Field field = JsonReader.class.getDeclaredField("pos");
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException var1) {
                throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", var1);
            }
        });
        private static final Field JSON_READER_LINE_START = (Field)Util.make(() -> {
            try {
                new JsonReader(new StringReader(""));
                Field field = JsonReader.class.getDeclaredField("lineStart");
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException var1) {
                throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", var1);
            }
        });

        public Serializer() {
        }

        public MutableText deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonPrimitive()) {
                return net.minecraft.text.Text.literal(jsonElement.getAsString());
            } else if (!jsonElement.isJsonObject()) {
                if (jsonElement.isJsonArray()) {
                    JsonArray jsonArray3 = jsonElement.getAsJsonArray();
                    MutableText mutableText = null;

                    for(JsonElement jsonElement2 : jsonArray3) {
                        MutableText mutableText2 = this.deserialize(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
                        if (mutableText == null) {
                            mutableText = mutableText2;
                        } else {
                            mutableText.append(mutableText2);
                        }
                    }

                    return mutableText;
                } else {
                    throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
                }
            } else {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                MutableText mutableText;
                if (jsonObject.has("text")) {
                    String string = JsonHelper.getString(jsonObject, "text");
                    mutableText = string.isEmpty() ? net.minecraft.text.Text.empty() : net.minecraft.text.Text.literal(string);
                } else if (jsonObject.has("translate")) {
                    String string = JsonHelper.getString(jsonObject, "translate");
                    if (jsonObject.has("with")) {
                        JsonArray jsonArray = JsonHelper.getArray(jsonObject, "with");
                        Object[] objects = new Object[jsonArray.size()];

                        for(int i = 0; i < objects.length; ++i) {
                            objects[i] = method_43474(this.deserialize(jsonArray.get(i), type, jsonDeserializationContext));
                        }

                        mutableText = net.minecraft.text.Text.translatable(string, objects);
                    } else {
                        mutableText = net.minecraft.text.Text.translatable(string);
                    }
                } else if (jsonObject.has("score")) {
                    JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "score");
                    if (!jsonObject2.has("name") || !jsonObject2.has("objective")) {
                        throw new JsonParseException("A score component needs a least a name and an objective");
                    }

                    mutableText = net.minecraft.text.Text.score(JsonHelper.getString(jsonObject2, "name"), JsonHelper.getString(jsonObject2, "objective"));
                } else if (jsonObject.has("selector")) {
                    Optional<net.minecraft.text.Text> optional = this.getSeparator(type, jsonDeserializationContext, jsonObject);
                    mutableText = net.minecraft.text.Text.selector(JsonHelper.getString(jsonObject, "selector"), optional);
                } else if (jsonObject.has("keybind")) {
                    mutableText = net.minecraft.text.Text.keybind(JsonHelper.getString(jsonObject, "keybind"));
                } else {
                    if (!jsonObject.has("nbt")) {
                        throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
                    }

                    String string = JsonHelper.getString(jsonObject, "nbt");
                    Optional<net.minecraft.text.Text> optional2 = this.getSeparator(type, jsonDeserializationContext, jsonObject);
                    boolean bl = JsonHelper.getBoolean(jsonObject, "interpret", false);
                    NbtDataSource nbtDataSource;
                    if (jsonObject.has("block")) {
                        nbtDataSource = new BlockNbtDataSource(JsonHelper.getString(jsonObject, "block"));
                    } else if (jsonObject.has("entity")) {
                        nbtDataSource = new EntityNbtDataSource(JsonHelper.getString(jsonObject, "entity"));
                    } else {
                        if (!jsonObject.has("storage")) {
                            throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
                        }

                        nbtDataSource = new StorageNbtDataSource(new Identifier(JsonHelper.getString(jsonObject, "storage")));
                    }

                    mutableText = net.minecraft.text.Text.nbt(string, bl, optional2, nbtDataSource);
                }

                if (jsonObject.has("extra")) {
                    JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "extra");
                    if (jsonArray2.size() <= 0) {
                        throw new JsonParseException("Unexpected empty array of components");
                    }

                    for(int j = 0; j < jsonArray2.size(); ++j) {
                        mutableText.append(this.deserialize(jsonArray2.get(j), type, jsonDeserializationContext));
                    }
                }

                mutableText.setStyle((Style)jsonDeserializationContext.deserialize(jsonElement, Style.class));
                return mutableText;
            }
        }

        private static Object method_43474(Object object) {
            if (object instanceof net.minecraft.text.Text text && text.getStyle().isEmpty() && text.getSiblings().isEmpty()) {
                TextContent textContent = text.getContent();
                if (textContent instanceof LiteralTextContent literalTextContent) {
                    return literalTextContent.string();
                }
            }

            return object;
        }

        private Optional<net.minecraft.text.Text> getSeparator(Type type, JsonDeserializationContext context, JsonObject json) {
            return json.has("separator") ? Optional.of(this.deserialize(json.get("separator"), type, context)) : Optional.empty();
        }

        private void addStyle(Style style, JsonObject json, JsonSerializationContext context) {
            JsonElement jsonElement = context.serialize(style);
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = (JsonObject)jsonElement;

                for(Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    json.add((String)entry.getKey(), (JsonElement)entry.getValue());
                }
            }

        }

        public JsonElement serialize(net.minecraft.text.Text text, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            if (!text.getStyle().isEmpty()) {
                this.addStyle(text.getStyle(), jsonObject, jsonSerializationContext);
            }

            if (!text.getSiblings().isEmpty()) {
                JsonArray jsonArray = new JsonArray();

                for(net.minecraft.text.Text text2 : text.getSiblings()) {
                    jsonArray.add(this.serialize(text2, net.minecraft.text.Text.class, jsonSerializationContext));
                }

                jsonObject.add("extra", jsonArray);
            }

            TextContent textContent = text.getContent();
            if (textContent == TextContent.EMPTY) {
                jsonObject.addProperty("text", "");
            } else if (textContent instanceof LiteralTextContent literalTextContent) {
                jsonObject.addProperty("text", literalTextContent.string());
            } else if (textContent instanceof TranslatableTextContent translatableTextContent) {
                jsonObject.addProperty("translate", translatableTextContent.getKey());
                if (translatableTextContent.getArgs().length > 0) {
                    JsonArray jsonArray2 = new JsonArray();

                    for(Object object : translatableTextContent.getArgs()) {
                        if (object instanceof net.minecraft.text.Text) {
                            jsonArray2.add(this.serialize((net.minecraft.text.Text)object, object.getClass(), jsonSerializationContext));
                        } else {
                            jsonArray2.add(new JsonPrimitive(String.valueOf(object)));
                        }
                    }

                    jsonObject.add("with", jsonArray2);
                }
            } else if (textContent instanceof ScoreTextContent scoreTextContent) {
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.addProperty("name", scoreTextContent.getName());
                jsonObject2.addProperty("objective", scoreTextContent.getObjective());
                jsonObject.add("score", jsonObject2);
            } else if (textContent instanceof SelectorTextContent selectorTextContent) {
                jsonObject.addProperty("selector", selectorTextContent.getPattern());
                this.addSeparator(jsonSerializationContext, jsonObject, selectorTextContent.getSeparator());
            } else if (textContent instanceof KeybindTextContent keybindTextContent) {
                jsonObject.addProperty("keybind", keybindTextContent.getKey());
            } else {
                if (!(textContent instanceof NbtTextContent)) {
                    throw new IllegalArgumentException("Don't know how to serialize " + textContent + " as a Component");
                }

                NbtTextContent nbtTextContent = (NbtTextContent)textContent;
                jsonObject.addProperty("nbt", nbtTextContent.getPath());
                jsonObject.addProperty("interpret", nbtTextContent.shouldInterpret());
                this.addSeparator(jsonSerializationContext, jsonObject, nbtTextContent.getSeparator());
                NbtDataSource nbtDataSource = nbtTextContent.getDataSource();
                if (nbtDataSource instanceof BlockNbtDataSource blockNbtDataSource) {
                    jsonObject.addProperty("block", blockNbtDataSource.rawPos());
                } else if (nbtDataSource instanceof EntityNbtDataSource entityNbtDataSource) {
                    jsonObject.addProperty("entity", entityNbtDataSource.rawSelector());
                } else {
                    if (!(nbtDataSource instanceof StorageNbtDataSource)) {
                        throw new IllegalArgumentException("Don't know how to serialize " + textContent + " as a Component");
                    }

                    StorageNbtDataSource storageNbtDataSource = (StorageNbtDataSource)nbtDataSource;
                    jsonObject.addProperty("storage", storageNbtDataSource.id().toString());
                }
            }

            return jsonObject;
        }

        private void addSeparator(JsonSerializationContext context, JsonObject json, Optional<net.minecraft.text.Text> separator) {
            separator.ifPresent(separatorx -> json.add("separator", this.serialize(separatorx, separatorx.getClass(), context)));
        }

        public static String toJson(net.minecraft.text.Text text) {
            return GSON.toJson(text);
        }

        public static JsonElement toJsonTree(net.minecraft.text.Text text) {
            return GSON.toJsonTree(text);
        }

        @Nullable
        public static MutableText fromJson(String json) {
            return (MutableText)JsonHelper.deserialize(GSON, json, MutableText.class, false);
        }

        @Nullable
        public static MutableText fromJson(JsonElement json) {
            return (MutableText)GSON.fromJson(json, MutableText.class);
        }

        @Nullable
        public static MutableText fromLenientJson(String json) {
            return (MutableText)JsonHelper.deserialize(GSON, json, MutableText.class, true);
        }

        public static MutableText fromJson(com.mojang.brigadier.StringReader reader) {
            try {
                JsonReader jsonReader = new JsonReader(new StringReader(reader.getRemaining()));
                jsonReader.setLenient(false);
                MutableText mutableText = (MutableText)GSON.getAdapter(MutableText.class).read(jsonReader);
                reader.setCursor(reader.getCursor() + getPosition(jsonReader));
                return mutableText;
            } catch (StackOverflowError | IOException var3) {
                throw new JsonParseException(var3);
            }
        }

        private static int getPosition(JsonReader reader) {
            try {
                return JSON_READER_POS.getInt(reader) - JSON_READER_LINE_START.getInt(reader) + 1;
            } catch (IllegalAccessException var2) {
                throw new IllegalStateException("Couldn't read position of JsonReader", var2);
            }
        }
    }
}
*/