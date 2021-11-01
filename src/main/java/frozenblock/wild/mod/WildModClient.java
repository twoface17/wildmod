package frozenblock.wild.mod;

import frozenblock.wild.mod.entity.FrogEntityModel;
import frozenblock.wild.mod.entity.FrogEntityRenderer;
import frozenblock.wild.mod.entity.WardenEntityRenderer;
import frozenblock.wild.mod.registry.MangroveWoods;
import frozenblock.wild.mod.registry.RegisterBlocks;
import frozenblock.wild.mod.registry.RegisterEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WildModClient implements ClientModInitializer {

    public static final EntityModelLayer MODEL_FROG_LAYER = new EntityModelLayer(new Identifier(WildMod.MOD_ID, "frog"), "main");
    public static final EntityModelLayer MODEL_WARDEN_LAYER = new EntityModelLayer(new Identifier(WildMod.MOD_ID, "warden"), "main");

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(MangroveWoods.MANGROVE_LEAVES, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MangroveWoods.MANGROVE_ROOTS, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(MangroveWoods.MANGROVE_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MangroveWoods.MANGROVE_TRAPDOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MangroveWoods.MANGROVE_SAPLING, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.SCULK_VEIN, RenderLayer.getCutout());

        EntityRendererRegistry.register(RegisterEntities.FROG, FrogEntityRenderer::new);
        EntityRendererRegistry.register(RegisterEntities.WARDEN, WardenEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(MODEL_FROG_LAYER, FrogEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MODEL_WARDEN_LAYER, FrogEntityModel::getTexturedModelData);



    }
}
