package frozenblock.wild.mod;

import frozenblock.wild.mod.blocks.mangrove.MangroveWood;
import frozenblock.wild.mod.entity.*;
import frozenblock.wild.mod.registry.MangroveWoods;
import frozenblock.wild.mod.registry.RegisterBlocks;
import frozenblock.wild.mod.registry.RegisterEntities;
import frozenblock.wild.mod.registry.RegisterParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class WildModClient implements ClientModInitializer {

    public static final EntityModelLayer MODEL_WARDEN_LAYER = new EntityModelLayer(new Identifier(WildMod.MOD_ID, "warden"), "main");
    public static final EntityModelLayer MODEL_FROG_LAYER = new EntityModelLayer(new Identifier(WildMod.MOD_ID, "frog"), "main");
    public static final EntityModelLayer MODEL_TADPOLE_LAYER = new EntityModelLayer(new Identifier(WildMod.MOD_ID, "tadpole"), "main");

    @Override
    public void onInitializeClient() {

        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
            registry.register(new Identifier(WildMod.MOD_ID, "particle/firefly"));
        }));

        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
            registry.register(new Identifier(WildMod.MOD_ID, "particle/shrieker"));
        }));

        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
            registry.register(new Identifier(WildMod.MOD_ID, "particle/sculk_soul"));
        }));


        ParticleFactoryRegistry.getInstance().register(RegisterParticles.FIREFLY, FlameParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(RegisterParticles.SHRIEK, FlameParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(RegisterParticles.SCULK_SOUL, FlameParticle.Factory::new);

        BlockRenderLayerMap.INSTANCE.putBlock(MangroveWoods.MANGROVE_LEAVES, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MangroveWoods.MANGROVE_ROOTS, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(MangroveWoods.MANGROVE_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MangroveWoods.MANGROVE_TRAPDOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MangroveWoods.MANGROVE_PROPAGULE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.SCULK_VEIN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.SCULK_SHRIEKER, RenderLayer.getCutout());


        EntityRendererRegistry.register(RegisterEntities.WARDEN, WardenEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_WARDEN_LAYER, WardenEntityModel::getTexturedModelData);

        EntityRendererRegistry.register(RegisterEntities.FROG, FrogEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_FROG_LAYER, FrogEntityModel::getTexturedModelData);

        EntityRendererRegistry.register(RegisterEntities.TADPOLE, TadpoleEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_TADPOLE_LAYER, TadpoleEntityModel::getTexturedModelData);

        ColorProviderRegistry.BLOCK.register(((state, world, pos, tintIndex) -> {
            assert world != null;
            return BiomeColors.getFoliageColor(world, pos);
        }), MangroveWoods.MANGROVE_LEAVES);

        ColorProviderRegistry.ITEM.register(((stack, tintIndex) -> FoliageColors.getDefaultColor()), MangroveWoods.MANGROVE_LEAVES);


    }
}
