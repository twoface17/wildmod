package frozenblock.wild.mod.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Program;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class WardenFeatureRenderer<T extends WardenEntity, M extends WardenEntityModel<T>> extends FeatureRenderer<T, M> {
    private final Identifier texture;
    private final AnimationAngleAdjuster<T> animationAngleAdjuster;
    private final ModelPartVisibility<T, M> modelPartVisibility;

    public WardenFeatureRenderer(FeatureRendererContext<T, M> context, Identifier texture, AnimationAngleAdjuster<T> animationAngleAdjuster, ModelPartVisibility<T, M> modelPartVisibility) {
        super(context);
        this.texture = texture;
        this.animationAngleAdjuster = animationAngleAdjuster;
        this.modelPartVisibility = modelPartVisibility;
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T wardenEntity, float f, float g, float h, float j, float k, float l) {
        if (!wardenEntity.isInvisible()) {
            this.updateModelPartVisibility();
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(getEntityTranslucentEmissive(this.texture));
            this.getContextModel().render(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlay(wardenEntity, 0.0F), 1.0F, 1.0F, 1.0F, this.animationAngleAdjuster.apply(wardenEntity, h, j));
            this.unhideAllModelParts();
        }
    }

    private void updateModelPartVisibility() {
        List<ModelPart> list = this.modelPartVisibility.getPartsToDraw(this.getContextModel());
        this.getContextModel().getPart().traverse().forEach((part) -> {
            part.visible = false;
        });
        list.forEach((part) -> {
            part.visible = true;
        });
    }

    private void unhideAllModelParts() {
        this.getContextModel().getPart().traverse().forEach(part -> part.visible = true);
    }

    @Environment(EnvType.CLIENT)
    public interface AnimationAngleAdjuster<T extends WardenEntity> {
        float apply(T warden, float tickDelta, float animationProgress);
    }

    @Environment(EnvType.CLIENT)
    public interface ModelPartVisibility<T extends WardenEntity, M extends EntityModel<T>> {
        List<ModelPart> getPartsToDraw(M model);
    }

    private static final BiFunction<Identifier, Boolean, RenderLayer> ENTITY_TRANSLUCENT_EMISSIVE;

    static {
        ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize((texture, affectsOutline) -> {
            MultiPhaseParameters multiPhaseParameters = MultiPhaseParameters.builder().shader(RenderPhase.ENTITY_TRANSLUCENT_EMISSIVE_SHADER).texture(new RenderPhase.Texture(texture, false, false)).transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY).cull(RenderPhase.DISABLE_CULLING).writeMaskState(RenderPhase.COLOR_MASK).overlay(RenderPhase.ENABLE_OVERLAY_COLOR).build(affectsOutline);
            return of("entity_translucent_emissive", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, true, multiPhaseParameters);
        });
    }

    static MultiPhase of(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, MultiPhaseParameters phaseData) {
        return of(name, vertexFormat, drawMode, expectedBufferSize, false, false, phaseData);
    }

    private static MultiPhase of(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, MultiPhaseParameters phases) {
        return new MultiPhase(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, phases);
    }

    public static RenderLayer getEntityTranslucentEmissive(Identifier texture, boolean affectsOutline) {
        return ENTITY_TRANSLUCENT_EMISSIVE.apply(texture, affectsOutline);
    }

    public static RenderLayer getEntityTranslucentEmissive(Identifier texture) {
        return getEntityTranslucentEmissive(texture, true);
    }

    @Environment(EnvType.CLIENT)
    protected static final class MultiPhaseParameters {
        final RenderPhase.TextureBase texture;
        private final RenderPhase.Shader shader;
        private final RenderPhase.Transparency transparency;
        private final RenderPhase.DepthTest depthTest;
        final RenderPhase.Cull cull;
        private final RenderPhase.Lightmap lightmap;
        private final RenderPhase.Overlay overlay;
        private final RenderPhase.Layering layering;
        private final RenderPhase.Target target;
        private final RenderPhase.Texturing texturing;
        private final RenderPhase.WriteMaskState writeMaskState;
        private final RenderPhase.LineWidth lineWidth;
        final OutlineMode outlineMode;
        final ImmutableList<RenderPhase> phases;

        MultiPhaseParameters(RenderPhase.TextureBase texture, RenderPhase.Shader shader, RenderPhase.Transparency transparency, RenderPhase.DepthTest depthTest, RenderPhase.Cull cull, RenderPhase.Lightmap lightmap, RenderPhase.Overlay overlay, RenderPhase.Layering layering, RenderPhase.Target target, RenderPhase.Texturing texturing, RenderPhase.WriteMaskState writeMaskState, RenderPhase.LineWidth lineWidth, OutlineMode outlineMode) {
            this.texture = texture;
            this.shader = shader;
            this.transparency = transparency;
            this.depthTest = depthTest;
            this.cull = cull;
            this.lightmap = lightmap;
            this.overlay = overlay;
            this.layering = layering;
            this.target = target;
            this.texturing = texturing;
            this.writeMaskState = writeMaskState;
            this.lineWidth = lineWidth;
            this.outlineMode = outlineMode;
            this.phases = ImmutableList.of(this.texture, this.shader, this.transparency, this.depthTest, this.cull, this.lightmap, this.overlay, this.layering, this.target, this.texturing, this.writeMaskState, this.lineWidth, new RenderPhase[0]);
        }

        public String toString() {
            return "CompositeState[" + this.phases + ", outlineProperty=" + this.outlineMode + "]";
        }

        public static MultiPhaseParameters.Builder builder() {
            return new MultiPhaseParameters.Builder();
        }

        @Environment(EnvType.CLIENT)
        public static class Builder {
            private RenderPhase.TextureBase texture;
            private RenderPhase.Shader shader;
            private RenderPhase.Transparency transparency;
            private RenderPhase.DepthTest depthTest;
            private RenderPhase.Cull cull;
            private RenderPhase.Lightmap lightmap;
            private RenderPhase.Overlay overlay;
            private RenderPhase.Layering layering;
            private RenderPhase.Target target;
            private RenderPhase.Texturing texturing;
            private RenderPhase.WriteMaskState writeMaskState;
            private RenderPhase.LineWidth lineWidth;

            Builder() {
                this.texture = RenderPhase.NO_TEXTURE;
                this.shader = RenderPhase.NO_SHADER;
                this.transparency = RenderPhase.NO_TRANSPARENCY;
                this.depthTest = RenderPhase.LEQUAL_DEPTH_TEST;
                this.cull = RenderPhase.ENABLE_CULLING;
                this.lightmap = RenderPhase.DISABLE_LIGHTMAP;
                this.overlay = RenderPhase.DISABLE_OVERLAY_COLOR;
                this.layering = RenderPhase.NO_LAYERING;
                this.target = RenderPhase.MAIN_TARGET;
                this.texturing = RenderPhase.DEFAULT_TEXTURING;
                this.writeMaskState = RenderPhase.ALL_MASK;
                this.lineWidth = RenderPhase.FULL_LINE_WIDTH;
            }

            public MultiPhaseParameters.Builder texture(RenderPhase.TextureBase texture) {
                this.texture = texture;
                return this;
            }

            public MultiPhaseParameters.Builder shader(RenderPhase.Shader shader) {
                this.shader = shader;
                return this;
            }

            public MultiPhaseParameters.Builder transparency(RenderPhase.Transparency transparency) {
                this.transparency = transparency;
                return this;
            }

            public MultiPhaseParameters.Builder depthTest(RenderPhase.DepthTest depthTest) {
                this.depthTest = depthTest;
                return this;
            }

            public Builder cull(RenderPhase.Cull cull) {
                this.cull = cull;
                return this;
            }

            public MultiPhaseParameters.Builder lightmap(RenderPhase.Lightmap lightmap) {
                this.lightmap = lightmap;
                return this;
            }

            public MultiPhaseParameters.Builder overlay(RenderPhase.Overlay overlay) {
                this.overlay = overlay;
                return this;
            }

            public MultiPhaseParameters.Builder layering(RenderPhase.Layering layering) {
                this.layering = layering;
                return this;
            }

            public MultiPhaseParameters.Builder target(RenderPhase.Target target) {
                this.target = target;
                return this;
            }

            public MultiPhaseParameters.Builder texturing(RenderPhase.Texturing texturing) {
                this.texturing = texturing;
                return this;
            }

            public MultiPhaseParameters.Builder writeMaskState(RenderPhase.WriteMaskState writeMaskState) {
                this.writeMaskState = writeMaskState;
                return this;
            }

            public MultiPhaseParameters.Builder lineWidth(RenderPhase.LineWidth lineWidth) {
                this.lineWidth = lineWidth;
                return this;
            }

            public MultiPhaseParameters build(boolean affectsOutline) {
                return this.build(affectsOutline ? OutlineMode.AFFECTS_OUTLINE : OutlineMode.NONE);
            }

            public MultiPhaseParameters build(OutlineMode outlineMode) {
                return new MultiPhaseParameters(this.texture, this.shader, this.transparency, this.depthTest, this.cull, this.lightmap, this.overlay, this.layering, this.target, this.texturing, this.writeMaskState, this.lineWidth, outlineMode);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    static final class MultiPhase extends RenderLayer {
        static final BiFunction<Identifier, RenderPhase.Cull, RenderLayer> CULLING_LAYERS = Util.memoize((texture, culling) -> {
            return of("outline", VertexFormats.POSITION_COLOR_TEXTURE, VertexFormat.DrawMode.QUADS, 256, WardenFeatureRenderer.MultiPhaseParameters.builder().shader(RenderPhase.OUTLINE_SHADER).texture(new RenderPhase.Texture(texture, false, false)).cull(culling).depthTest(RenderPhase.DepthTest.ALWAYS_DEPTH_TEST).target(RenderPhase.DepthTest.OUTLINE_TARGET).build(OutlineMode.IS_OUTLINE));
        });
        private final WardenFeatureRenderer.MultiPhaseParameters phases;
        private final Optional<RenderLayer> affectedOutline;
        private final boolean outline;

        MultiPhase(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, WardenFeatureRenderer.MultiPhaseParameters phases) {
            super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, () -> {
                phases.phases.forEach(RenderPhase::startDrawing);
            }, () -> {
                phases.phases.forEach(RenderPhase::endDrawing);
            });
            this.phases = phases;
            this.affectedOutline = phases.outlineMode == OutlineMode.AFFECTS_OUTLINE ? phases.texture.getId().map((texture) -> {
                return (RenderLayer)CULLING_LAYERS.apply(texture, phases.cull);
            }) : Optional.empty();
            this.outline = phases.outlineMode == OutlineMode.IS_OUTLINE;
        }

        public Optional<RenderLayer> getAffectedOutline() {
            return this.affectedOutline;
        }

        public boolean isOutline() {
            return this.outline;
        }

        protected final WardenFeatureRenderer.MultiPhaseParameters getPhases() {
            return this.phases;
        }

        public String toString() {
            return "RenderType[" + this.name + ":" + this.phases + "]";
        }
    }

    @Environment(EnvType.CLIENT)
    private static enum OutlineMode {
        NONE("none"),
        IS_OUTLINE("is_outline"),
        AFFECTS_OUTLINE("affects_outline");

        private final String name;

        OutlineMode(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    @Environment(EnvType.CLIENT)
    public class GameRenderer extends net.minecraft.client.render.GameRenderer {
        private final Map<String, Shader> shaders;
        @Nullable
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
                list2.add(Pair.of(new Shader(manager, "rendertype_entity_translucent_emissive", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL), (shader) -> {
                    renderTypeEntityTranslucentEmissiveShader = shader;
                }));
            } catch (IOException var5) {
                list2.forEach((pair) -> {
                    ((Shader)pair.getFirst()).close();
                });
                throw new RuntimeException("could not reload shaders", var5);
            }

            super.loadShaders(manager);
        }

        @Nullable
        public static Shader getRenderTypeEntityTranslucentEmissiveShader() {
            return renderTypeEntityTranslucentEmissiveShader;
        }
    }
}
