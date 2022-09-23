package net.frozenblock.wildmod.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public abstract class WildRenderPhase extends net.minecraft.client.render.RenderPhase {
    private static final float VIEW_OFFSET_Z_LAYERING_SCALE = 0.99975586F;
    protected final String name;
    private final Runnable beginAction;
    private final Runnable endAction;
    protected static final Transparency NO_TRANSPARENCY = new Transparency("no_transparency", () -> {
        RenderSystem.disableBlend();
    }, () -> {
    });
    protected static final Transparency ADDITIVE_TRANSPARENCY = new Transparency("additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency LIGHTNING_TRANSPARENCY = new Transparency("lightning_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency GLINT_TRANSPARENCY = new Transparency("glint_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_COLOR, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency CRUMBLING_TRANSPARENCY = new Transparency("crumbling_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.DST_COLOR, GlStateManager.DstFactor.SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency TRANSLUCENT_TRANSPARENCY = new Transparency("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final WildRenderPhase.Shader NO_SHADER = new Shader();
    protected static final Shader ENTITY_TRANSLUCENT_EMISSIVE_SHADER = new WildRenderPhase.Shader(
            WildGameRenderer::getRenderTypeEntityTranslucentEmissiveShader);
    protected static final Shader OUTLINE_SHADER = new Shader(GameRenderer::getRenderTypeOutlineShader);

    protected static final WildRenderPhase.Texture MIPMAP_BLOCK_ATLAS_TEXTURE;
    protected static final WildRenderPhase.Texture BLOCK_ATLAS_TEXTURE;
    protected static final WildRenderPhase.TextureBase NO_TEXTURE;
    protected static final WildRenderPhase.Texturing DEFAULT_TEXTURING;
    protected static final WildRenderPhase.Texturing GLINT_TEXTURING;
    protected static final WildRenderPhase.Texturing ENTITY_GLINT_TEXTURING;
    protected static final WildRenderPhase.Lightmap ENABLE_LIGHTMAP;
    protected static final WildRenderPhase.Lightmap DISABLE_LIGHTMAP;
    protected static final WildRenderPhase.Overlay ENABLE_OVERLAY_COLOR;
    protected static final WildRenderPhase.Overlay DISABLE_OVERLAY_COLOR;
    protected static final WildRenderPhase.Cull ENABLE_CULLING;
    protected static final WildRenderPhase.Cull DISABLE_CULLING;
    protected static final WildRenderPhase.DepthTest ALWAYS_DEPTH_TEST;
    protected static final WildRenderPhase.DepthTest EQUAL_DEPTH_TEST;
    protected static final WildRenderPhase.DepthTest LEQUAL_DEPTH_TEST;
    protected static final WildRenderPhase.WriteMaskState ALL_MASK;
    protected static final WildRenderPhase.WriteMaskState COLOR_MASK;
    protected static final WildRenderPhase.WriteMaskState DEPTH_MASK;
    protected static final WildRenderPhase.Layering NO_LAYERING;
    protected static final WildRenderPhase.Layering POLYGON_OFFSET_LAYERING;
    protected static final WildRenderPhase.Layering VIEW_OFFSET_Z_LAYERING;
    protected static final WildRenderPhase.Target MAIN_TARGET;
    protected static final WildRenderPhase.Target OUTLINE_TARGET;
    protected static final WildRenderPhase.Target TRANSLUCENT_TARGET;
    protected static final WildRenderPhase.Target PARTICLES_TARGET;
    protected static final WildRenderPhase.Target WEATHER_TARGET;
    protected static final WildRenderPhase.Target CLOUDS_TARGET;
    protected static final WildRenderPhase.Target ITEM_TARGET;
    protected static final WildRenderPhase.LineWidth FULL_LINE_WIDTH;

    public WildRenderPhase(String name, Runnable beginAction, Runnable endAction) {
        super(name, beginAction, endAction);
        this.name = name;
        this.beginAction = beginAction;
        this.endAction = endAction;
    }

    public void startDrawing() {
        this.beginAction.run();
    }

    public void endDrawing() {
        this.endAction.run();
    }

    public String toString() {
        return this.name;
    }

    private static void setupGlintTexturing(float scale) {
        long l = Util.getMeasuringTimeMs() * 8L;
        float f = (float) (l % 110000L) / 110000.0F;
        float g = (float) (l % 30000L) / 30000.0F;
        Matrix4f matrix4f = Matrix4f.translate(-f, g, 0.0F);
        matrix4f.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(10.0F));
        matrix4f.multiply(Matrix4f.scale(scale, scale, scale));
        RenderSystem.setTextureMatrix(matrix4f);
    }

    static {
        MIPMAP_BLOCK_ATLAS_TEXTURE = new Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, true);
        BLOCK_ATLAS_TEXTURE = new Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, false);
        NO_TEXTURE = new TextureBase();
        DEFAULT_TEXTURING = new Texturing("default_texturing", () -> {
        }, () -> {
        });
        GLINT_TEXTURING = new Texturing("glint_texturing", () -> {
            setupGlintTexturing(8.0F);
        }, () -> {
            RenderSystem.resetTextureMatrix();
        });
        ENTITY_GLINT_TEXTURING = new Texturing("entity_glint_texturing", () -> {
            setupGlintTexturing(0.16F);
        }, () -> {
            RenderSystem.resetTextureMatrix();
        });
        ENABLE_LIGHTMAP = new Lightmap(true);
        DISABLE_LIGHTMAP = new Lightmap(false);
        ENABLE_OVERLAY_COLOR = new Overlay(true);
        DISABLE_OVERLAY_COLOR = new Overlay(false);
        ENABLE_CULLING = new Cull(true);
        DISABLE_CULLING = new Cull(false);
        ALWAYS_DEPTH_TEST = new DepthTest("always", 519);
        EQUAL_DEPTH_TEST = new DepthTest("==", 514);
        LEQUAL_DEPTH_TEST = new DepthTest("<=", 515);
        ALL_MASK = new WriteMaskState(true, true);
        COLOR_MASK = new WriteMaskState(true, false);
        DEPTH_MASK = new WriteMaskState(false, true);
        NO_LAYERING = new Layering("no_layering", () -> {
        }, () -> {
        });
        POLYGON_OFFSET_LAYERING = new Layering("polygon_offset_layering", () -> {
            RenderSystem.polygonOffset(-1.0F, -10.0F);
            RenderSystem.enablePolygonOffset();
        }, () -> {
            RenderSystem.polygonOffset(0.0F, 0.0F);
            RenderSystem.disablePolygonOffset();
        });
        VIEW_OFFSET_Z_LAYERING = new Layering("view_offset_z_layering", () -> {
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.push();
            matrixStack.scale(0.99975586F, 0.99975586F, 0.99975586F);
            RenderSystem.applyModelViewMatrix();
        }, () -> {
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.pop();
            RenderSystem.applyModelViewMatrix();
        });
        MAIN_TARGET = new Target("main_target", () -> {
        }, () -> {
        });
        OUTLINE_TARGET = new Target("outline_target", () -> {
            MinecraftClient.getInstance().worldRenderer.getEntityOutlinesFramebuffer().beginWrite(false);
        }, () -> {
            MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        });
        TRANSLUCENT_TARGET = new Target("translucent_target", () -> {
            if (MinecraftClient.isFabulousGraphicsOrBetter()) {
                MinecraftClient.getInstance().worldRenderer.getTranslucentFramebuffer().beginWrite(false);
            }

        }, () -> {
            if (MinecraftClient.isFabulousGraphicsOrBetter()) {
                MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
            }

        });
        PARTICLES_TARGET = new Target("particles_target", () -> {
            if (MinecraftClient.isFabulousGraphicsOrBetter()) {
                MinecraftClient.getInstance().worldRenderer.getParticlesFramebuffer().beginWrite(false);
            }

        }, () -> {
            if (MinecraftClient.isFabulousGraphicsOrBetter()) {
                MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
            }

        });
        WEATHER_TARGET = new Target("weather_target", () -> {
            if (MinecraftClient.isFabulousGraphicsOrBetter()) {
                MinecraftClient.getInstance().worldRenderer.getWeatherFramebuffer().beginWrite(false);
            }

        }, () -> {
            if (MinecraftClient.isFabulousGraphicsOrBetter()) {
                MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
            }

        });
        CLOUDS_TARGET = new Target("clouds_target", () -> {
            if (MinecraftClient.isFabulousGraphicsOrBetter()) {
                MinecraftClient.getInstance().worldRenderer.getCloudsFramebuffer().beginWrite(false);
            }

        }, () -> {
            if (MinecraftClient.isFabulousGraphicsOrBetter()) {
                MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
            }

        });
        ITEM_TARGET = new Target("item_entity_target", () -> {
            if (MinecraftClient.isFabulousGraphicsOrBetter()) {
                MinecraftClient.getInstance().worldRenderer.getEntityFramebuffer().beginWrite(false);
            }

        }, () -> {
            if (MinecraftClient.isFabulousGraphicsOrBetter()) {
                MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
            }

        });
        FULL_LINE_WIDTH = new LineWidth(OptionalDouble.of(1.0));
    }

    @Environment(EnvType.CLIENT)
    protected static class Transparency extends WildRenderPhase {
        public Transparency(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Shader extends WildRenderPhase {
        private final Optional<Supplier<net.minecraft.client.render.Shader>> supplier;

        public Shader(Supplier<net.minecraft.client.render.Shader> supplier) {
            super("shader", () -> {
                RenderSystem.setShader(supplier);
            }, () -> {
            });
            this.supplier = Optional.of(supplier);
        }

        public Shader() {
            super("shader", () -> {
                RenderSystem.setShader(() -> {
                    return null;
                });
            }, () -> {
            });
            this.supplier = Optional.empty();
        }

        public String toString() {
            return this.name + "[" + this.supplier + "]";
        }
    }

    @Environment(EnvType.CLIENT)
    protected static class Texture extends TextureBase {
        private final Optional<Identifier> id;
        private final boolean blur;
        private final boolean mipmap;

        public Texture(@NotNull Identifier id, boolean blur, boolean mipmap) {
            super(() -> {
                RenderSystem.enableTexture();
                TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
                textureManager.getTexture(id).setFilter(blur, mipmap);
                RenderSystem.setShaderTexture(0, id);
            }, () -> {
            });
            this.id = Optional.of(id);
            this.blur = blur;
            this.mipmap = mipmap;
        }

        public String toString() {
            return this.name + "[" + this.id + "(blur=" + this.blur + ", mipmap=" + this.mipmap + ")]";
        }

        protected Optional<Identifier> getId() {
            return this.id;
        }
    }

    @Environment(EnvType.CLIENT)
    protected static class TextureBase extends WildRenderPhase {
        public TextureBase(Runnable apply, Runnable unapply) {
            super("texture", apply, unapply);
        }

        TextureBase() {
            super("texture", () -> {
            }, () -> {
            });
        }

        protected Optional<Identifier> getId() {
            return Optional.empty();
        }
    }

    @Environment(EnvType.CLIENT)
    protected static class Texturing extends WildRenderPhase {
        public Texturing(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(EnvType.CLIENT)
    protected static class Lightmap extends Toggleable {
        public Lightmap(boolean lightmap) {
            super("lightmap", () -> {
                if (lightmap) {
                    MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().enable();
                }

            }, () -> {
                if (lightmap) {
                    MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().disable();
                }

            }, lightmap);
        }
    }

    @Environment(EnvType.CLIENT)
    protected static class Overlay extends Toggleable {
        public Overlay(boolean overlayColor) {
            super("overlay", () -> {
                if (overlayColor) {
                    MinecraftClient.getInstance().gameRenderer.getOverlayTexture().setupOverlayColor();
                }

            }, () -> {
                if (overlayColor) {
                    MinecraftClient.getInstance().gameRenderer.getOverlayTexture().teardownOverlayColor();
                }

            }, overlayColor);
        }
    }

    @Environment(EnvType.CLIENT)
    protected static class Cull extends Toggleable {
        public Cull(boolean culling) {
            super("cull", () -> {
                if (!culling) {
                    RenderSystem.disableCull();
                }

            }, () -> {
                if (!culling) {
                    RenderSystem.enableCull();
                }

            }, culling);
        }
    }

    @Environment(EnvType.CLIENT)
    protected static class DepthTest extends WildRenderPhase {
        private final String depthFunctionName;

        public DepthTest(String depthFunctionName, int depthFunction) {
            super("depth_test", () -> {
                if (depthFunction != 519) {
                    RenderSystem.enableDepthTest();
                    RenderSystem.depthFunc(depthFunction);
                }

            }, () -> {
                if (depthFunction != 519) {
                    RenderSystem.disableDepthTest();
                    RenderSystem.depthFunc(515);
                }

            });
            this.depthFunctionName = depthFunctionName;
        }

        public String toString() {
            return this.name + "[" + this.depthFunctionName + "]";
        }
    }

    @Environment(EnvType.CLIENT)
    protected static class WriteMaskState extends WildRenderPhase {
        private final boolean color;
        private final boolean depth;

        public WriteMaskState(boolean color, boolean depth) {
            super("write_mask_state", () -> {
                if (!depth) {
                    RenderSystem.depthMask(depth);
                }

                if (!color) {
                    RenderSystem.colorMask(color, color, color, color);
                }

            }, () -> {
                if (!depth) {
                    RenderSystem.depthMask(true);
                }

                if (!color) {
                    RenderSystem.colorMask(true, true, true, true);
                }

            });
            this.color = color;
            this.depth = depth;
        }

        public String toString() {
            return this.name + "[writeColor=" + this.color + ", writeDepth=" + this.depth + "]";
        }
    }

    @Environment(EnvType.CLIENT)
    protected static class Layering extends WildRenderPhase {
        public Layering(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(EnvType.CLIENT)
    protected static class Target extends WildRenderPhase {
        public Target(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(EnvType.CLIENT)
    protected static class LineWidth extends WildRenderPhase {
        private final OptionalDouble width;

        public LineWidth(OptionalDouble width) {
            super("line_width", () -> {
                if (!Objects.equals(width, OptionalDouble.of(1.0))) {
                    if (width.isPresent()) {
                        RenderSystem.lineWidth((float) width.getAsDouble());
                    } else {
                        RenderSystem.lineWidth(Math.max(2.5F, (float) MinecraftClient.getInstance().getWindow().getFramebufferWidth() / 1920.0F * 2.5F));
                    }
                }

            }, () -> {
                if (!Objects.equals(width, OptionalDouble.of(1.0))) {
                    RenderSystem.lineWidth(1.0F);
                }

            });
            this.width = width;
        }

        public String toString() {
            String var10000 = this.name;
            return var10000 + "[" + (this.width.isPresent() ? this.width.getAsDouble() : "window_scale") + "]";
        }
    }

    @Environment(EnvType.CLIENT)
    private static class Toggleable extends WildRenderPhase {
        private final boolean enabled;

        public Toggleable(String name, Runnable apply, Runnable unapply, boolean enabled) {
            super(name, apply, unapply);
            this.enabled = enabled;
        }

        public String toString() {
            return this.name + "[" + this.enabled + "]";
        }
    }
}