package frozenblock.wild.mod;

import frozenblock.wild.mod.blocks.SculkShriekerBlock;
import frozenblock.wild.mod.blocks.SculkVeinBlock;
import frozenblock.wild.mod.entity.*;
import frozenblock.wild.mod.entity.chestboat.ChestBoatEntityModel;
import frozenblock.wild.mod.entity.chestboat.ChestBoatEntityRenderer;
import frozenblock.wild.mod.fromAccurateSculk.*;
import frozenblock.wild.mod.registry.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class WildModClient implements ClientModInitializer {

    public static final EntityModelLayer MODEL_WARDEN_LAYER = new EntityModelLayer(new Identifier(WildMod.MOD_ID, "warden"), "main");
    public static final EntityModelLayer MODEL_FROG_LAYER = new EntityModelLayer(new Identifier(WildMod.MOD_ID, "frog"), "main");
    public static final EntityModelLayer MODEL_TADPOLE_LAYER = new EntityModelLayer(new Identifier(WildMod.MOD_ID, "tadpole"), "main");
    public static final EntityModelLayer MODEL_MANGROVE_BOAT_LAYER = new EntityModelLayer(new Identifier(WildMod.MOD_ID, "mangrove_boat"), "main");
    public static final EntityModelLayer MODEL_CHEST_BOAT_LAYER = new EntityModelLayer(new Identifier(WildMod.MOD_ID, "chest_boat"), "main");

    public static final Identifier SHRIEKER_SHRIEK_PACKET = new Identifier("shriek_packet");
    public static final Identifier SHRIEKER_GARGLE1_PACKET = new Identifier("gargle1_packet");
    public static final Identifier SHRIEKER_GARGLE2_PACKET = new Identifier("gargle2_packet");
    public static final Identifier CATALYST_PARTICLE_PACKET = new Identifier("catalyst_packet");
    public static final Identifier WARDEN_DIG_PACKET = new Identifier("warden_dig_packet");

    @Override
    public void onInitializeClient() {

        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
            registry.register(new Identifier(WildMod.MOD_ID, "particle/firefly"));
        }));

        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((spriteAtlasTexture, registry) -> {
            registry.register(new Identifier(WildMod.MOD_ID, "particle/sculk_shriek"));
            registry.register(new Identifier(WildMod.MOD_ID, "particle/sculk_shriek2"));
            registry.register(new Identifier(WildMod.MOD_ID, "particle/sculk_shriekz"));
            registry.register(new Identifier(WildMod.MOD_ID, "particle/sculk_shriekz2"));
            registry.register(new Identifier(WildMod.MOD_ID, "particle/sculk_shrieknx"));
            registry.register(new Identifier(WildMod.MOD_ID, "particle/sculk_shrieknx2"));
            registry.register(new Identifier(WildMod.MOD_ID, "particle/sculk_shriekx"));
            registry.register(new Identifier(WildMod.MOD_ID, "particle/sculk_shriekx2"));
            registry.register(new Identifier(WildMod.MOD_ID, "particle/sculk_soul"));
        }));
        ParticleFactoryRegistry.getInstance().register(RegisterAccurateSculk.SCULK_SHRIEK, ShriekParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(RegisterAccurateSculk.SCULK_SHRIEK2, ShriekParticle2.Factory::new);
        ParticleFactoryRegistry.getInstance().register(RegisterAccurateSculk.SCULK_SHRIEKZ, ShriekParticlePosZ.Factory::new);
        ParticleFactoryRegistry.getInstance().register(RegisterAccurateSculk.SCULK_SHRIEKZ2, ShriekParticle2PosZ.Factory::new);
        ParticleFactoryRegistry.getInstance().register(RegisterAccurateSculk.SCULK_SHRIEKNX, ShriekParticleNX.Factory::new);
        ParticleFactoryRegistry.getInstance().register(RegisterAccurateSculk.SCULK_SHRIEKNX2, ShriekParticleNX2.Factory::new);
        ParticleFactoryRegistry.getInstance().register(RegisterAccurateSculk.SCULK_SHRIEKX, ShriekParticleX.Factory::new);
        ParticleFactoryRegistry.getInstance().register(RegisterAccurateSculk.SCULK_SHRIEKX2, ShriekParticleX2.Factory::new);
        ParticleFactoryRegistry.getInstance().register(RegisterParticles.SCULK_SOUL, SculkSoul.Factory::new);

        BlockRenderLayerMap.INSTANCE.putBlock(MangroveWoods.MANGROVE_LEAVES, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MangroveWoods.MANGROVE_ROOTS, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(MangroveWoods.MANGROVE_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MangroveWoods.MANGROVE_TRAPDOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MangroveWoods.MANGROVE_PROPAGULE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(SculkVeinBlock.SCULK_VEIN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.FROG_SPAWN, RenderLayer.getCutout());


        EntityRendererRegistry.register(RegisterEntities.WARDEN, WardenEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_WARDEN_LAYER, WardenEntityModel::getTexturedModelData);

        EntityRendererRegistry.register(RegisterEntities.FROG, FrogEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_FROG_LAYER, FrogEntityModel::getTexturedModelData);

        EntityRendererRegistry.register(RegisterEntities.TADPOLE, TadpoleEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_TADPOLE_LAYER, TadpoleEntityModel::getTexturedModelData);

        EntityRendererRegistry.register(RegisterEntities.MANGROVE_BOAT, MangroveBoatEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_MANGROVE_BOAT_LAYER, MangroveBoatEntityModel::getTexturedModelData);

        EntityRendererRegistry.register(RegisterEntities.CHEST_BOAT, ChestBoatEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_CHEST_BOAT_LAYER, ChestBoatEntityModel::getTexturedModelData);

        EntityRendererRegistry.register(RegisterEntities.FIREFLY, FireflyEntityRenderer::new);



        ColorProviderRegistry.BLOCK.register(((state, world, pos, tintIndex) -> {
            assert world != null;
            return BiomeColors.getFoliageColor(world, pos);
        }), MangroveWoods.MANGROVE_LEAVES);

        ColorProviderRegistry.ITEM.register(((stack, tintIndex) -> FoliageColors.getDefaultColor()), MangroveWoods.MANGROVE_LEAVES);

        BlockRenderLayerMap.INSTANCE.putBlock(SculkShriekerBlock.SCULK_SHRIEKER_BLOCK, RenderLayer.getCutout());

        ClientPlayNetworking.registerGlobalReceiver(CATALYST_PARTICLE_PACKET, (client, handler, buf, responseSender) -> {
            BlockPos catalyst = buf.readBlockPos();
            client.execute(() -> {
                assert client.world != null;
                SculkParticleHandler.catalystSouls(client.world, catalyst);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SHRIEKER_SHRIEK_PACKET, (client, handler, buf, responseSender) -> {
            BlockPos shrieker = buf.readBlockPos();
            int direction = buf.readInt();
            client.execute(() -> {
                SculkParticleHandler.shriekerShriek(client.world, shrieker, direction);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SHRIEKER_GARGLE1_PACKET, (client, handler, buf, responseSender) -> {
            BlockPos shrieker = buf.readBlockPos();
            client.execute(() -> {
                assert client.world != null;
                SculkParticleHandler.shriekerGargle1(client.world, shrieker);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SHRIEKER_GARGLE2_PACKET, (client, handler, buf, responseSender) -> {
            BlockPos shrieker = buf.readBlockPos();
            client.execute(() -> {
                SculkParticleHandler.shriekerGargle2(client.world, shrieker);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(WARDEN_DIG_PACKET, (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            int ticks = buf.readInt();
            client.execute(() -> {
                SculkParticleHandler.wardenDig(client.world, pos, ticks);
            });
        });
    }
}
