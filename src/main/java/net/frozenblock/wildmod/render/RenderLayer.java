package net.frozenblock.wildmod.render;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.Optional;
import java.util.function.BiFunction;

import static net.frozenblock.wildmod.render.RenderPhase.ENTITY_TRANSLUCENT_EMISSIVE_SHADER;

@Environment(EnvType.CLIENT)
public abstract class RenderLayer extends net.minecraft.client.render.RenderLayer {
    public RenderLayer(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    static RenderLayer.MultiPhase of(
            String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, RenderLayer.MultiPhaseParameters phaseData
    ) {
        return of(name, vertexFormat, drawMode, expectedBufferSize, false, false, phaseData);
    }

    private static RenderLayer.MultiPhase of(
            String name,
            VertexFormat vertexFormat,
            VertexFormat.DrawMode drawMode,
            int expectedBufferSize,
            boolean hasCrumbling,
            boolean translucent,
            RenderLayer.MultiPhaseParameters phases
    ) {
        return new RenderLayer.MultiPhase(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, phases);
    }

    private static final BiFunction<Identifier, Boolean, net.minecraft.client.render.RenderLayer> ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize(
            ((texture, affectsOutline) -> {
                MultiPhaseParameters multiPhaseParameters = MultiPhaseParameters.builder()
                        .shader(ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                        .texture(new RenderPhase.Texture(texture, false, false))
                        .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                        .cull(RenderPhase.DISABLE_CULLING)
                        .writeMaskState(RenderPhase.COLOR_MASK)
                        .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
                        .build(affectsOutline);
                return of(
                        "entity_translucent_emissive",
                        VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                        VertexFormat.DrawMode.QUADS,
                        256,
                        true,
                        true,
                        multiPhaseParameters
                );
            })
    );

    public static RenderLayer getEntityTranslucentEmissive(Identifier texture, boolean affectsOutline) {
        return (RenderLayer)ENTITY_TRANSLUCENT_EMISSIVE.apply(texture, affectsOutline);
    }

    public static RenderLayer getEntityTranslucentEmissive(Identifier texture) {
        return getEntityTranslucentEmissive(texture, true);
    }

    @Environment(EnvType.CLIENT)
    static final class MultiPhase extends RenderLayer {
        static final BiFunction<Identifier, RenderPhase.Cull, RenderLayer> CULLING_LAYERS = Util.memoize(
                (texture, culling) -> RenderLayer.of(
                        "outline",
                        VertexFormats.POSITION_COLOR_TEXTURE,
                        VertexFormat.DrawMode.QUADS,
                        256,
                        RenderLayer.MultiPhaseParameters.builder()
                                .shader(RenderPhase.OUTLINE_SHADER)
                                .texture(new RenderPhase.Texture(texture, false, false))
                                .cull(culling)
                                .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
                                .target(RenderPhase.OUTLINE_TARGET)
                                .build(RenderLayer.OutlineMode.IS_OUTLINE)
                )
        );
        private final RenderLayer.MultiPhaseParameters phases;
        private final Optional<RenderLayer> affectedOutline;
        private final boolean outline;

        MultiPhase(
                String name,
                VertexFormat vertexFormat,
                VertexFormat.DrawMode drawMode,
                int expectedBufferSize,
                boolean hasCrumbling,
                boolean translucent,
                RenderLayer.MultiPhaseParameters phases
        ) {
            super(
                    name,
                    vertexFormat,
                    drawMode,
                    expectedBufferSize,
                    hasCrumbling,
                    translucent,
                    () -> phases.phases.forEach(RenderPhase::startDrawing),
                    () -> phases.phases.forEach(RenderPhase::endDrawing)
            );
            this.phases = phases;
            this.affectedOutline = phases.outlineMode == RenderLayer.OutlineMode.AFFECTS_OUTLINE
                    ? phases.texture.getId().map(texture -> (RenderLayer)CULLING_LAYERS.apply(texture, phases.cull))
                    : Optional.empty();
            this.outline = phases.outlineMode == RenderLayer.OutlineMode.IS_OUTLINE;
        }

        @Override
        public boolean isOutline() {
            return this.outline;
        }

        protected final RenderLayer.MultiPhaseParameters getPhases() {
            return this.phases;
        }

        @Override
        public String toString() {
            return "RenderType[" + this.name + ":" + this.phases + "]";
        }
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
        private final RenderPhase.Target target;
        private final RenderPhase.Texturing texturing;
        private final RenderPhase.WriteMaskState writeMaskState;
        private final RenderPhase.LineWidth lineWidth;
        final RenderLayer.OutlineMode outlineMode;
        final ImmutableList<RenderPhase> phases;

        MultiPhaseParameters(
                RenderPhase.TextureBase texture,
                RenderPhase.Shader shader,
                RenderPhase.Transparency transparency,
                RenderPhase.DepthTest depthTest,
                RenderPhase.Cull cull,
                RenderPhase.Lightmap lightmap,
                RenderPhase.Overlay overlay,
                RenderPhase.Layering layering,
                RenderPhase.Target target,
                RenderPhase.Texturing texturing,
                RenderPhase.WriteMaskState writeMaskState,
                RenderPhase.LineWidth lineWidth,
                RenderLayer.OutlineMode outlineMode
        ) {
            this.texture = texture;
            this.shader = shader;
            this.transparency = transparency;
            this.depthTest = depthTest;
            this.cull = cull;
            this.lightmap = lightmap;
            this.overlay = overlay;
            this.target = target;
            this.texturing = texturing;
            this.writeMaskState = writeMaskState;
            this.lineWidth = lineWidth;
            this.outlineMode = outlineMode;
            this.phases = ImmutableList.of(
                    this.texture,
                    this.shader,
                    this.transparency,
                    this.depthTest,
                    this.cull,
                    this.lightmap,
                    this.overlay,
                    layering,
                    this.target,
                    this.texturing,
                    this.writeMaskState,
                    this.lineWidth,
                    new RenderPhase[0]
            );
        }

        public String toString() {
            return "CompositeState[" + this.phases + ", outlineProperty=" + this.outlineMode + "]";
        }

        public static RenderLayer.MultiPhaseParameters.Builder builder() {
            return new RenderLayer.MultiPhaseParameters.Builder();
        }

        @Environment(EnvType.CLIENT)
        public static class Builder {
            private RenderPhase.TextureBase texture = RenderPhase.NO_TEXTURE;
            private RenderPhase.Shader shader = RenderPhase.NO_SHADER;
            private RenderPhase.Transparency transparency = RenderPhase.NO_TRANSPARENCY;
            private RenderPhase.DepthTest depthTest = RenderPhase.LEQUAL_DEPTH_TEST;
            private RenderPhase.Cull cull = RenderPhase.ENABLE_CULLING;
            private RenderPhase.Lightmap lightmap = RenderPhase.DISABLE_LIGHTMAP;
            private RenderPhase.Overlay overlay = RenderPhase.DISABLE_OVERLAY_COLOR;
            private RenderPhase.Layering layering = RenderPhase.NO_LAYERING;
            private RenderPhase.Target target = RenderPhase.MAIN_TARGET;
            private RenderPhase.Texturing texturing = RenderPhase.DEFAULT_TEXTURING;
            private RenderPhase.WriteMaskState writeMaskState = RenderPhase.ALL_MASK;
            private RenderPhase.LineWidth lineWidth = RenderPhase.FULL_LINE_WIDTH;

            Builder() {
            }

            public RenderLayer.MultiPhaseParameters.Builder texture(RenderPhase.TextureBase texture) {
                this.texture = texture;
                return this;
            }

            public RenderLayer.MultiPhaseParameters.Builder shader(RenderPhase.Shader shader) {
                this.shader = shader;
                return this;
            }

            public RenderLayer.MultiPhaseParameters.Builder transparency(RenderPhase.Transparency transparency) {
                this.transparency = transparency;
                return this;
            }

            public RenderLayer.MultiPhaseParameters.Builder depthTest(RenderPhase.DepthTest depthTest) {
                this.depthTest = depthTest;
                return this;
            }

            public RenderLayer.MultiPhaseParameters.Builder cull(RenderPhase.Cull cull) {
                this.cull = cull;
                return this;
            }

            public RenderLayer.MultiPhaseParameters.Builder lightmap(RenderPhase.Lightmap lightmap) {
                this.lightmap = lightmap;
                return this;
            }

            public RenderLayer.MultiPhaseParameters.Builder overlay(RenderPhase.Overlay overlay) {
                this.overlay = overlay;
                return this;
            }

            public RenderLayer.MultiPhaseParameters.Builder layering(RenderPhase.Layering layering) {
                this.layering = layering;
                return this;
            }

            public RenderLayer.MultiPhaseParameters.Builder target(RenderPhase.Target target) {
                this.target = target;
                return this;
            }

            public RenderLayer.MultiPhaseParameters.Builder texturing(RenderPhase.Texturing texturing) {
                this.texturing = texturing;
                return this;
            }

            public RenderLayer.MultiPhaseParameters.Builder writeMaskState(RenderPhase.WriteMaskState writeMaskState) {
                this.writeMaskState = writeMaskState;
                return this;
            }

            public RenderLayer.MultiPhaseParameters.Builder lineWidth(RenderPhase.LineWidth lineWidth) {
                this.lineWidth = lineWidth;
                return this;
            }

            public RenderLayer.MultiPhaseParameters build(boolean affectsOutline) {
                return this.build(affectsOutline ? RenderLayer.OutlineMode.AFFECTS_OUTLINE : RenderLayer.OutlineMode.NONE);
            }

            public RenderLayer.MultiPhaseParameters build(RenderLayer.OutlineMode outlineMode) {
                return new RenderLayer.MultiPhaseParameters(
                        this.texture,
                        this.shader,
                        this.transparency,
                        this.depthTest,
                        this.cull,
                        this.lightmap,
                        this.overlay,
                        this.layering,
                        this.target,
                        this.texturing,
                        this.writeMaskState,
                        this.lineWidth,
                        outlineMode
                );
            }
        }
    }

    @Environment(EnvType.CLIENT)
    static enum OutlineMode {
        NONE("none"),
        IS_OUTLINE("is_outline"),
        AFFECTS_OUTLINE("affects_outline");

        private final String name;

        private OutlineMode(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

}
