package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.block.MultifaceGrowthBlock;
import net.frozenblock.wildmod.block.SculkShriekerBlock;
import net.frozenblock.wildmod.particle.SculkChargeParticleEffect;
import net.frozenblock.wildmod.particle.ShriekParticleEffect;
import net.frozenblock.wildmod.registry.RegisterParticles;
import net.frozenblock.wildmod.registry.RegisterSounds;
import net.frozenblock.wildmod.registry.RegisterStatusEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Random;
import java.util.function.Supplier;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Shadow @Nullable private ClientWorld world;

    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract void renderEndSky(MatrixStack matrices);

    @Shadow @Nullable private VertexBuffer lightSkyBuffer;

    @Shadow @Final private static Identifier SUN;

    @Shadow @Final private static Identifier MOON_PHASES;

    @Shadow @Nullable private VertexBuffer starsBuffer;

    @Shadow @Nullable private VertexBuffer darkSkyBuffer;

    private boolean method_43788(Camera camera) {
        Entity var3 = camera.getFocusedEntity();
        if (!(var3 instanceof LivingEntity livingEntity)) {
            return false;
        } else {
            return livingEntity.hasStatusEffect(StatusEffects.BLINDNESS) || livingEntity.hasStatusEffect(RegisterStatusEffects.DARKNESS);
        }
    }

    /*@Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At("HEAD"))
    public void renderSky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        runnable.run();
        if (!bl) {
            CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
            if (cameraSubmersionType != CameraSubmersionType.POWDER_SNOW && cameraSubmersionType != CameraSubmersionType.LAVA && !this.method_43788(camera)) {
                if (this.client.world.getDimensionEffects().getSkyType() == DimensionEffects.SkyType.END) {
                    this.renderEndSky(matrices);
                } else if (this.client.world.getDimensionEffects().getSkyType() == DimensionEffects.SkyType.NORMAL) {
                    RenderSystem.disableTexture();
                    Vec3d vec3d = this.world.getSkyColor(this.client.gameRenderer.getCamera().getPos(), tickDelta);
                    float f = (float)vec3d.x;
                    float g = (float)vec3d.y;
                    float h = (float)vec3d.z;
                    BackgroundRenderer.setFogBlack();
                    BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                    RenderSystem.depthMask(false);
                    RenderSystem.setShaderColor(f, g, h, 1.0F);
                    Shader shader = RenderSystem.getShader();
                    this.lightSkyBuffer.bind();
                    this.lightSkyBuffer.setShader(matrices.peek().getPositionMatrix(), projectionMatrix, shader);
                    VertexBuffer.unbind();
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    float[] fs = this.world.getDimensionEffects().getFogColorOverride(this.world.getSkyAngle(tickDelta), tickDelta);
                    float i;
                    float k;
                    float o;
                    float p;
                    float q;
                    if (fs != null) {
                        RenderSystem.setShader(GameRenderer::getPositionColorShader);
                        RenderSystem.disableTexture();
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                        matrices.push();
                        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
                        i = MathHelper.sin(this.world.getSkyAngleRadians(tickDelta)) < 0.0F ? 180.0F : 0.0F;
                        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(i));
                        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
                        float j = fs[0];
                        k = fs[1];
                        float l = fs[2];
                        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
                        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
                        bufferBuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F).color(j, k, l, fs[3]).next();
                        int m = 16;

                        for(int n = 0; n <= 16; ++n) {
                            o = (float)n * 6.2831855F / 16.0F;
                            p = MathHelper.sin(o);
                            q = MathHelper.cos(o);
                            bufferBuilder.vertex(matrix4f, p * 120.0F, q * 120.0F, -q * 40.0F * fs[3]).color(fs[0], fs[1], fs[2], 0.0F).next();
                        }

                        bufferBuilder.end();
                        BufferRenderer.draw(bufferBuilder);
                        matrices.pop();
                    }

                    RenderSystem.enableTexture();
                    RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
                    matrices.push();
                    i = 1.0F - this.world.getRainGradient(tickDelta);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, i);
                    matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
                    matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(this.world.getSkyAngle(tickDelta) * 360.0F));
                    Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
                    k = 30.0F;
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    RenderSystem.setShaderTexture(0, SUN);
                    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
                    bufferBuilder.vertex(matrix4f2, -k, 100.0F, -k).texture(0.0F, 0.0F).next();
                    bufferBuilder.vertex(matrix4f2, k, 100.0F, -k).texture(1.0F, 0.0F).next();
                    bufferBuilder.vertex(matrix4f2, k, 100.0F, k).texture(1.0F, 1.0F).next();
                    bufferBuilder.vertex(matrix4f2, -k, 100.0F, k).texture(0.0F, 1.0F).next();
                    bufferBuilder.end();
                    BufferRenderer.draw(bufferBuilder);
                    k = 20.0F;
                    RenderSystem.setShaderTexture(0, MOON_PHASES);
                    int r = this.world.getMoonPhase();
                    int s = r % 4;
                    int m = r / 4 % 2;
                    float t = (float)(s + 0) / 4.0F;
                    o = (float)(m + 0) / 2.0F;
                    p = (float)(s + 1) / 4.0F;
                    q = (float)(m + 1) / 2.0F;
                    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
                    bufferBuilder.vertex(matrix4f2, -k, -100.0F, k).texture(p, q).next();
                    bufferBuilder.vertex(matrix4f2, k, -100.0F, k).texture(t, q).next();
                    bufferBuilder.vertex(matrix4f2, k, -100.0F, -k).texture(t, o).next();
                    bufferBuilder.vertex(matrix4f2, -k, -100.0F, -k).texture(p, o).next();
                    bufferBuilder.end();
                    BufferRenderer.draw(bufferBuilder);
                    RenderSystem.disableTexture();
                    float u = this.world.method_23787(tickDelta) * i;
                    if (u > 0.0F) {
                        RenderSystem.setShaderColor(u, u, u, u);
                        BackgroundRenderer.clearFog();
                        this.starsBuffer.bind();
                        this.starsBuffer.setShader(matrices.peek().getPositionMatrix(), projectionMatrix, GameRenderer.getPositionShader());
                        VertexBuffer.unbind();
                        runnable.run();
                    }

                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderSystem.disableBlend();
                    matrices.pop();
                    RenderSystem.disableTexture();
                    RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
                    double d = this.client.player.getCameraPosVec(tickDelta).y - this.world.getLevelProperties().getSkyDarknessHeight(this.world);
                    if (d < 0.0) {
                        matrices.push();
                        matrices.translate(0.0, 12.0, 0.0);
                        this.darkSkyBuffer.bind();
                        this.darkSkyBuffer.setShader(matrices.peek().getPositionMatrix(), projectionMatrix, shader);
                        VertexBuffer.unbind();
                        matrices.pop();
                    }

                    if (this.world.getDimensionEffects().isAlternateSkyColor()) {
                        RenderSystem.setShaderColor(f * 0.2F + 0.04F, g * 0.2F + 0.04F, h * 0.6F + 0.1F, 1.0F);
                    } else {
                        RenderSystem.setShaderColor(f, g, h, 1.0F);
                    }

                    RenderSystem.enableTexture();
                    RenderSystem.depthMask(true);
                }
            }
        }
    }

    */@Inject(method = "processWorldEvent", at = @At("TAIL"))
    public void processWorldEvent(PlayerEntity source, int eventId, BlockPos pos, int data, CallbackInfo ci) {
        Random random = this.world.random;
        int i;
        int j;
        float w;
        float ac;
        double g;
        float v;
        int k;
        double aj;
        double s;
        double d;
        double ak;
        int t;
        double e;
        double al;
        double f;
        double af;
        switch (eventId) {
            case 3006:
                i = data >> 6;
                float ae;
                if (i > 0) {
                    if (random.nextFloat() < 0.3F + (float)i * 0.1F) {
                        v = 0.15F + 0.02F * (float)i * (float)i * random.nextFloat();
                        w = 0.4F + 0.3F * (float)i * random.nextFloat();
                        this.world.playSound(pos, RegisterSounds.BLOCK_SCULK_CHARGE, SoundCategory.BLOCKS, v, w, false);
                    }

                    byte b = (byte)(data & 63);
                    IntProvider intProvider = UniformIntProvider.create(0, i);
                    ac = 0.005F;
                    Supplier<Vec3d> supplier = () -> {
                        return new Vec3d(MathHelper.nextDouble(random, -0.004999999888241291, 0.004999999888241291), MathHelper.nextDouble(random, -0.004999999888241291, 0.004999999888241291), MathHelper.nextDouble(random, -0.004999999888241291, 0.004999999888241291));
                    };
                    if (b == 0) {
                        Direction[] var11 = Direction.values();
                        int var12 = var11.length;

                        for(int var13 = 0; var13 < var12; ++var13) {
                            Direction direction2 = var11[var13];
                            float ad = direction2 == Direction.DOWN ? 3.1415927F : 0.0F;
                            g = direction2.getAxis() == Direction.Axis.Y ? 0.65 : 0.57;
                            spawnParticles(this.world, pos, new SculkChargeParticleEffect(ad), intProvider, direction2, supplier, g);
                        }

                        return;
                    } else {
                        Iterator var47 = MultifaceGrowthBlock.flagToDirections(b).iterator();

                        while(var47.hasNext()) {
                            Direction direction3 = (Direction)var47.next();
                            ae = direction3 == Direction.UP ? 3.1415927F : 0.0F;
                            af = 0.35;
                            spawnParticles(this.world, pos, new SculkChargeParticleEffect(ae), intProvider, direction3, supplier, 0.35);
                        }

                        return;
                    }
                } else {
                    this.world.playSound(pos, RegisterSounds.BLOCK_SCULK_CHARGE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                    boolean bl = this.world.getBlockState(pos).isFullCube(this.world, pos);
                    k = bl ? 40 : 20;
                    ac = bl ? 0.45F : 0.25F;
                    float ag = 0.07F;

                    for(t = 0; t < k; ++t) {
                        float ah = 2.0F * random.nextFloat() - 1.0F;
                        ae = 2.0F * random.nextFloat() - 1.0F;
                        float ai = 2.0F * random.nextFloat() - 1.0F;
                        this.world.addParticle(RegisterParticles.SCULK_CHARGE_POP, (double)pos.getX() + 0.5 + (double)(ah * ac), (double)pos.getY() + 0.5 + (double)(ae * ac), (double)pos.getZ() + 0.5 + (double)(ai * ac), (double)(ah * 0.07F), (double)(ae * 0.07F), (double)(ai * 0.07F));
                    }

                    return;
                }
            case 3007:
                for(j = 0; j < 10; ++j) {
                    this.world.addParticle(new ShriekParticleEffect(j * 5), false, (double)pos.getX() + 0.5, (double)pos.getY() + SculkShriekerBlock.TOP, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0);
                }

                this.world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + SculkShriekerBlock.TOP, (double)pos.getZ() + 0.5, RegisterSounds.BLOCK_SCULK_SHRIEKER_SHRIEK, SoundCategory.BLOCKS, 2.0F, 0.6F + this.world.random.nextFloat() * 0.4F, false);
        }
    }

    private static void spawnParticles(World world, BlockPos pos, ParticleEffect effect, IntProvider count, Direction direction, Supplier<Vec3d> velocity, double offsetMultiplier) {
        int i = count.get(world.random);

        for(int j = 0; j < i; ++j) {
            spawnParticle(world, pos, direction, effect, velocity.get(), offsetMultiplier);
        }

    }

    private static void spawnParticle(World world, BlockPos pos, Direction direction, ParticleEffect effect, Vec3d velocity, double offsetMultiplier) {
        Vec3d vec3d = Vec3d.ofCenter(pos);
        int i = direction.getOffsetX();
        int j = direction.getOffsetY();
        int k = direction.getOffsetZ();
        double d = vec3d.x + (i == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double)i * offsetMultiplier);
        double e = vec3d.y + (j == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double)j * offsetMultiplier);
        double f = vec3d.z + (k == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double)k * offsetMultiplier);
        double g = i == 0 ? velocity.getX() : 0.0;
        double h = j == 0 ? velocity.getY() : 0.0;
        double l = k == 0 ? velocity.getZ() : 0.0;
        world.addParticle(effect, d, e, f, g, h, l);
    }
}
