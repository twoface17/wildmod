package net.frozenblock.wildmod.render;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Program;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class GameRenderer extends net.minecraft.client.render.GameRenderer {
    private final Map<String, Shader> shaders;
    private static Shader renderTypeEntityTranslucentEmissiveShader;

    @Nullable
    private static Shader positionShader;
    @Nullable
    private static Shader positionColorShader;
    @Nullable
    private static Shader positionColorTexShader;
    @Nullable
    private static Shader positionTexShader;
    @Nullable
    private static Shader positionTexColorShader;
    @Nullable
    private static Shader renderTypeTextShader;

    public GameRenderer(MinecraftClient client, ResourceManager resourceManager, BufferBuilderStorage buffers) {
        super(client, resourceManager, buffers);
        this.shaders = Maps.newHashMap();
    }

    public void preloadShaders(ResourceFactory factory) {
        if (this.blitScreenShader != null) {
            throw new RuntimeException("Blit shader already preloaded");
        } else {
            try {
                this.blitScreenShader = new Shader(factory, "blits_screen", VertexFormats.BLIT_SCREEN);
            } catch (IOException var3) {
                throw new RuntimeException("could not preload blit shader", var3);
            }

            positionShader = this.loadShader(factory, "position", VertexFormats.POSITION);
            positionColorShader = this.loadShader(factory, "position_color", VertexFormats.POSITION_COLOR);
            positionColorTexShader = this.loadShader(factory, "position_color_tex", VertexFormats.POSITION_COLOR_TEXTURE);
            positionTexShader = this.loadShader(factory, "position_tex", VertexFormats.POSITION_TEXTURE);
            positionTexColorShader = this.loadShader(factory, "position_tex_color", VertexFormats.POSITION_TEXTURE_COLOR);
            renderTypeTextShader = this.loadShader(factory, "rendertype_text", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
        }
    }

    private Shader loadShader(ResourceFactory factory, String name, VertexFormat vertexFormat) {
        try {
            Shader shader = new Shader(factory, name, vertexFormat);
            this.shaders.put(name, shader);
            return shader;
        } catch (Exception var5) {
            throw new IllegalStateException("could not preload shader " + name, var5);
        }
    }

    public void loadShaders(ResourceManager manager) {
        RenderSystem.assertOnRenderThread();
        List<Program> list = Lists.newArrayList();
        list.addAll(Program.Type.FRAGMENT.getProgramCache().values());
        list.addAll(Program.Type.VERTEX.getProgramCache().values());
        list.forEach(Program::release);
        List<Pair<Shader, Consumer<Shader>>> list2 = Lists.newArrayListWithCapacity(shaders.size());

        try {
            list2.add(
                    Pair.of(
                            new Shader(manager, "rendertype_entity_translucent_emissive", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
                            (Consumer)shader -> renderTypeEntityTranslucentEmissiveShader = (Shader) shader
                    )
            );
            list2.add(Pair.of(new Shader(manager, "position", VertexFormats.POSITION), (Consumer)shader -> positionShader = (Shader) shader));
            list2.add(Pair.of(new Shader(manager, "position_color", VertexFormats.POSITION_COLOR), (Consumer)shader -> positionColorShader = (Shader) shader));
        } catch (IOException var5) {
            list2.forEach((pair) -> {
                pair.getFirst().close();
            });
            throw new RuntimeException("could not reload shaders", var5);
        }

        super.loadShaders(manager);
    }

    public static Shader getRenderTypeEntityTranslucentEmissiveShader() {
        return renderTypeEntityTranslucentEmissiveShader;
    }
}