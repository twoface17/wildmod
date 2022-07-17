package net.frozenblock.wildmod.render;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.wildmod.world.gen.random.WildAbstractRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Program;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.random.AbstractRandom;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class WildGameRenderer extends net.minecraft.client.render.GameRenderer {
    private final Map<String, Shader> shaders = Maps.newHashMap();
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

    private float skyDarkness;
    private float lastSkyDarkness;
    @Nullable
    private final ShaderEffect shader;
    private final MinecraftClient client;
    private final ResourceManager resourceManager;
    private final AbstractRandom random = WildAbstractRandom.createAtomic();
    private float viewDistance;
    public final HeldItemRenderer firstPersonRenderer;
    private final MapRenderer mapRenderer;
    private final BufferBuilderStorage buffers;
    private int ticks;
    private float fovMultiplier;
    private float lastFovMultiplier;
    private final boolean renderHand = true;
    private final LightmapTextureManager lightmapTextureManager;
    private final Camera camera = new Camera();
    @Nullable
    private ItemStack floatingItem;
    private int floatingItemTimeLeft;
    private float floatingItemWidth;
    private float floatingItemHeight;

    public WildGameRenderer(MinecraftClient client, HeldItemRenderer heldItemRenderer, ResourceManager resourceManager, BufferBuilderStorage buffers) {
        super(client, resourceManager, buffers);
        this.client = client;
        this.resourceManager = resourceManager;
        this.firstPersonRenderer = heldItemRenderer;
        this.mapRenderer = new MapRenderer(client.getTextureManager());
        this.lightmapTextureManager = new LightmapTextureManager(this, client);
        this.buffers = buffers;
        this.shader = null;
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
                            (Consumer) shader -> renderTypeEntityTranslucentEmissiveShader = (Shader) shader
                    )
            );
            list2.add(Pair.of(new Shader(manager, "position", VertexFormats.POSITION), (Consumer) shader -> positionShader = (Shader) shader));
            list2.add(Pair.of(new Shader(manager, "position_color", VertexFormats.POSITION_COLOR), (Consumer) shader -> positionColorShader = (Shader) shader));
        } catch (IOException var5) {
            list2.forEach((pair) -> {
                pair.getFirst().close();
            });
            throw new RuntimeException("could not reload shaders", var5);
        }

        super.loadShaders(manager);
    }

    private void clearShaders() {
        RenderSystem.assertOnRenderThread();
        this.shaders.values().forEach(Shader::close);
        this.shaders.clear();
    }

    @Nullable
    public Shader getShader(@Nullable String name) {
        return name == null ? null : this.shaders.get(name);
    }

    public void tick() {
        this.updateFovMultiplier();
        this.lightmapTextureManager.tick();
        if (this.client.getCameraEntity() == null) {
            this.client.setCameraEntity(this.client.player);
        }

        this.camera.updateEyeHeight();
        ++this.ticks;
        this.firstPersonRenderer.updateHeldItems();
        this.client.worldRenderer.tickRainSplashing(this.camera);
        this.lastSkyDarkness = this.skyDarkness;
        if (this.client.inGameHud.getBossBarHud().shouldDarkenSky()) {
            this.skyDarkness += 0.05F;
            if (this.skyDarkness > 1.0F) {
                this.skyDarkness = 1.0F;
            }
        } else if (this.skyDarkness > 0.0F) {
            this.skyDarkness -= 0.0125F;
        }

        if (this.floatingItemTimeLeft > 0) {
            --this.floatingItemTimeLeft;
            if (this.floatingItemTimeLeft == 0) {
                this.floatingItem = null;
            }
        }

    }

    @Nullable
    public ShaderEffect getShader() {
        return this.shader;
    }

    private void updateFovMultiplier() {
        float f = 1.0F;
        if (this.client.getCameraEntity() instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
            f = abstractClientPlayerEntity.getFovMultiplier();
        }

        this.lastFovMultiplier = this.fovMultiplier;
        this.fovMultiplier += (f - this.fovMultiplier) * 0.5F;
        if (this.fovMultiplier > 1.5F) {
            this.fovMultiplier = 1.5F;
        }

        if (this.fovMultiplier < 0.1F) {
            this.fovMultiplier = 0.1F;
        }
    }

    public float getSkyDarkness(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastSkyDarkness, this.skyDarkness);
    }

    public static Shader getRenderTypeEntityTranslucentEmissiveShader() {
        return renderTypeEntityTranslucentEmissiveShader;
    }
}