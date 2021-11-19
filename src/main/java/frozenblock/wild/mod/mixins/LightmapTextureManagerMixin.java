package frozenblock.wild.mod.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.BitmapFont;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {

    @Shadow @Final private NativeImage image;

    @Shadow @Final private NativeImageBackedTexture texture;

    public float time;

    @Inject(at = @At("TAIL"), method = "update")
    public void update(float delta, CallbackInfo ci) {

        time = time + 0.05f;

        int gv = (int)(128*Math.cos((double) time))+128;

        if(gv > 255) {
            gv = 255;
        }

        int r = gv;
        int g = gv;
        int b = gv;
        int rgb = (((r&0x0ff)<<16)|((g&0x0ff)<<8)|(b&0x0ff));

        for(int x = 0; x < 16; ++x) {
            for(int y = 0; y < 16; ++y) {
                this.image.setColor(x, y, -rgb);
            }
        }
        this.texture.upload();
        //System.out.println("[EXPORTED DATA] - Time Value: " + time + ", Color Value: " + gv);
    }
}