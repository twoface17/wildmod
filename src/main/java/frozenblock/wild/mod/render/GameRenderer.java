package frozenblock.wild.mod.render;


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
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class GameRenderer extends net.minecraft.client.render.GameRenderer {
    private final Map<String, Shader> shaders;
    private static Shader renderTypeEntityTranslucentEmissiveShader;

    public GameRenderer(MinecraftClient client, ResourceManager resourceManager, BufferBuilderStorage buffers) {
        super(client, resourceManager, buffers);
        this.shaders = Maps.newHashMap();
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
                        shader -> renderTypeEntityTranslucentEmissiveShader = shader
                )
            );
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