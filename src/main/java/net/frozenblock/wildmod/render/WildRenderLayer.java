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

import static net.frozenblock.wildmod.render.WildRenderPhase.ENTITY_TRANSLUCENT_EMISSIVE_SHADER;

@Environment(EnvType.CLIENT)
public abstract class WildRenderLayer extends net.minecraft.client.render.RenderLayer {
    public WildRenderLayer(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    static WildRenderLayer.MultiPhase of(
            String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, WildRenderLayer.MultiPhaseParameters phaseData
    ) {
        return of(name, vertexFormat, drawMode, expectedBufferSize, false, false, phaseData);
    }

    private static WildRenderLayer.MultiPhase of(
            String name,
            VertexFormat vertexFormat,
            VertexFormat.DrawMode drawMode,
            int expectedBufferSize,
            boolean hasCrumbling,
            boolean translucent,
            WildRenderLayer.MultiPhaseParameters phases
    ) {
        return new WildRenderLayer.MultiPhase(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, phases);
    }

    private static final BiFunction<Identifier, Boolean, net.minecraft.client.render.RenderLayer> ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize(
            ((texture, affectsOutline) -> {
                MultiPhaseParameters multiPhaseParameters = MultiPhaseParameters.builder()
                        .shader(ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                        .texture(new WildRenderPhase.Texture(texture, false, false))
                        .transparency(WildRenderPhase.TRANSLUCENT_TRANSPARENCY)
                        .cull(WildRenderPhase.DISABLE_CULLING)
                        .writeMaskState(WildRenderPhase.COLOR_MASK)
                        .overlay(WildRenderPhase.ENABLE_OVERLAY_COLOR)
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

    public static WildRenderLayer getEntityTranslucentEmissive(Identifier texture, boolean affectsOutline) {
        return (WildRenderLayer) ENTITY_TRANSLUCENT_EMISSIVE.apply(texture, affectsOutline);
    }

    public static WildRenderLayer getEntityTranslucentEmissive(Identifier texture) {
        return getEntityTranslucentEmissive(texture, true);
    }

    @Environment(EnvType.CLIENT)
    static final class MultiPhase extends WildRenderLayer {
        static final BiFunction<Identifier, WildRenderPhase.Cull, WildRenderLayer> CULLING_LAYERS = Util.memoize(
                (texture, culling) -> WildRenderLayer.of(
                        "outline",
                        VertexFormats.POSITION_COLOR_TEXTURE,
                        VertexFormat.DrawMode.QUADS,
                        256,
                        WildRenderLayer.MultiPhaseParameters.builder()
                                .shader(WildRenderPhase.OUTLINE_SHADER)
                                .texture(new WildRenderPhase.Texture(texture, false, false))
                                .cull(culling)
                                .depthTest(WildRenderPhase.ALWAYS_DEPTH_TEST)
                                .target(WildRenderPhase.OUTLINE_TARGET)
                                .build(WildRenderLayer.OutlineMode.IS_OUTLINE)
                )
        );
        private final WildRenderLayer.MultiPhaseParameters phases;
        private final Optional<WildRenderLayer> affectedOutline;
        private final boolean outline;

        MultiPhase(
                String name,
                VertexFormat vertexFormat,
                VertexFormat.DrawMode drawMode,
                int expectedBufferSize,
                boolean hasCrumbling,
                boolean translucent,
                WildRenderLayer.MultiPhaseParameters phases
        ) {
            super(
                    name,
                    vertexFormat,
                    drawMode,
                    expectedBufferSize,
                    hasCrumbling,
                    translucent,
                    () -> phases.phases.forEach(WildRenderPhase::startDrawing),
                    () -> phases.phases.forEach(WildRenderPhase::endDrawing)
            );
            this.phases = phases;
            this.affectedOutline = phases.outlineMode == WildRenderLayer.OutlineMode.AFFECTS_OUTLINE
                    ? phases.texture.getId().map(texture -> CULLING_LAYERS.apply(texture, phases.cull))
                    : Optional.empty();
            this.outline = phases.outlineMode == WildRenderLayer.OutlineMode.IS_OUTLINE;
        }

        @Override
        public boolean isOutline() {
            return this.outline;
        }

        private WildRenderLayer.MultiPhaseParameters getPhases() {
            return this.phases;
        }

        @Override
        public String toString() {
            return "RenderType[" + this.name + ":" + this.phases + "]";
        }
    }

    @Environment(EnvType.CLIENT)
    protected static final class MultiPhaseParameters {
        final WildRenderPhase.TextureBase texture;
        private final WildRenderPhase.Shader shader;
        private final WildRenderPhase.Transparency transparency;
        private final WildRenderPhase.DepthTest depthTest;
        final WildRenderPhase.Cull cull;
        private final WildRenderPhase.Lightmap lightmap;
        private final WildRenderPhase.Overlay overlay;
        private final WildRenderPhase.Target target;
        private final WildRenderPhase.Texturing texturing;
        private final WildRenderPhase.WriteMaskState writeMaskState;
        private final WildRenderPhase.LineWidth lineWidth;
        final WildRenderLayer.OutlineMode outlineMode;
        final ImmutableList<WildRenderPhase> phases;

        MultiPhaseParameters(
                WildRenderPhase.TextureBase texture,
                WildRenderPhase.Shader shader,
                WildRenderPhase.Transparency transparency,
                WildRenderPhase.DepthTest depthTest,
                WildRenderPhase.Cull cull,
                WildRenderPhase.Lightmap lightmap,
                WildRenderPhase.Overlay overlay,
                WildRenderPhase.Layering layering,
                WildRenderPhase.Target target,
                WildRenderPhase.Texturing texturing,
                WildRenderPhase.WriteMaskState writeMaskState,
                WildRenderPhase.LineWidth lineWidth,
                WildRenderLayer.OutlineMode outlineMode
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
                    this.lineWidth
            );
        }

        public String toString() {
            return "CompositeState[" + this.phases + ", outlineProperty=" + this.outlineMode + "]";
        }

        public static WildRenderLayer.MultiPhaseParameters.Builder builder() {
            return new WildRenderLayer.MultiPhaseParameters.Builder();
        }

        @Environment(EnvType.CLIENT)
        public static class Builder {
            private WildRenderPhase.TextureBase texture = WildRenderPhase.NO_TEXTURE;
            private WildRenderPhase.Shader shader = WildRenderPhase.NO_SHADER;
            private WildRenderPhase.Transparency transparency = WildRenderPhase.NO_TRANSPARENCY;
            private WildRenderPhase.DepthTest depthTest = WildRenderPhase.LEQUAL_DEPTH_TEST;
            private WildRenderPhase.Cull cull = WildRenderPhase.ENABLE_CULLING;
            private WildRenderPhase.Lightmap lightmap = WildRenderPhase.DISABLE_LIGHTMAP;
            private WildRenderPhase.Overlay overlay = WildRenderPhase.DISABLE_OVERLAY_COLOR;
            private WildRenderPhase.Layering layering = WildRenderPhase.NO_LAYERING;
            private WildRenderPhase.Target target = WildRenderPhase.MAIN_TARGET;
            private WildRenderPhase.Texturing texturing = WildRenderPhase.DEFAULT_TEXTURING;
            private WildRenderPhase.WriteMaskState writeMaskState = WildRenderPhase.ALL_MASK;
            private WildRenderPhase.LineWidth lineWidth = WildRenderPhase.FULL_LINE_WIDTH;

            Builder() {
            }

            public WildRenderLayer.MultiPhaseParameters.Builder texture(WildRenderPhase.TextureBase texture) {
                this.texture = texture;
                return this;
            }

            public WildRenderLayer.MultiPhaseParameters.Builder shader(WildRenderPhase.Shader shader) {
                this.shader = shader;
                return this;
            }

            public WildRenderLayer.MultiPhaseParameters.Builder transparency(WildRenderPhase.Transparency transparency) {
                this.transparency = transparency;
                return this;
            }

            public WildRenderLayer.MultiPhaseParameters.Builder depthTest(WildRenderPhase.DepthTest depthTest) {
                this.depthTest = depthTest;
                return this;
            }

            public WildRenderLayer.MultiPhaseParameters.Builder cull(WildRenderPhase.Cull cull) {
                this.cull = cull;
                return this;
            }

            public WildRenderLayer.MultiPhaseParameters.Builder lightmap(WildRenderPhase.Lightmap lightmap) {
                this.lightmap = lightmap;
                return this;
            }

            public WildRenderLayer.MultiPhaseParameters.Builder overlay(WildRenderPhase.Overlay overlay) {
                this.overlay = overlay;
                return this;
            }

            public WildRenderLayer.MultiPhaseParameters.Builder layering(WildRenderPhase.Layering layering) {
                this.layering = layering;
                return this;
            }

            public WildRenderLayer.MultiPhaseParameters.Builder target(WildRenderPhase.Target target) {
                this.target = target;
                return this;
            }

            public WildRenderLayer.MultiPhaseParameters.Builder texturing(WildRenderPhase.Texturing texturing) {
                this.texturing = texturing;
                return this;
            }

            public WildRenderLayer.MultiPhaseParameters.Builder writeMaskState(WildRenderPhase.WriteMaskState writeMaskState) {
                this.writeMaskState = writeMaskState;
                return this;
            }

            public WildRenderLayer.MultiPhaseParameters.Builder lineWidth(WildRenderPhase.LineWidth lineWidth) {
                this.lineWidth = lineWidth;
                return this;
            }

            public WildRenderLayer.MultiPhaseParameters build(boolean affectsOutline) {
                return this.build(affectsOutline ? WildRenderLayer.OutlineMode.AFFECTS_OUTLINE : WildRenderLayer.OutlineMode.NONE);
            }

            public WildRenderLayer.MultiPhaseParameters build(WildRenderLayer.OutlineMode outlineMode) {
                return new WildRenderLayer.MultiPhaseParameters(
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
    enum OutlineMode {
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

}
