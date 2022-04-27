package net.frozenblock.wildmod;

import net.frozenblock.wildmod.block.SculkShriekerBlock;
import net.frozenblock.wildmod.block.SculkVeinBlock;
import net.frozenblock.wildmod.data.DataGenerator;
import net.frozenblock.wildmod.entity.chestboat.ChestBoatEntityModel;
import net.frozenblock.wildmod.entity.chestboat.ChestBoatEntityRenderer;
import net.frozenblock.wildmod.event.GameEventTagProvider;
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
import net.frozenblock.wildmod.entity.*;
import net.frozenblock.wildmod.fromAccurateSculk.*;
import net.frozenblock.wildmod.registry.*;
import net.minecraft.GameVersion;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.data.SnbtProvider;
import net.minecraft.data.validate.StructureValidatorProvider;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.nio.file.Path;
import java.util.Collection;

@Environment(EnvType.CLIENT)
public class WildModClient implements ClientModInitializer {

    public static final EntityModelLayer WARDEN = new EntityModelLayer(new Identifier(WildMod.MOD_ID, "warden"), "main");
    public static final EntityModelLayer MODEL_ALLAY_LAYER = new EntityModelLayer(new Identifier(WildMod.MOD_ID, "allay"), "main");
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

        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((spriteAtlasTexture, registry) -> {
            registry.register(new Identifier(WildMod.MOD_ID, "particle/sculk_shriek"));
            registry.register(new Identifier(WildMod.MOD_ID, "particle/sculk_soul"));
//            registry.register(new Identifier(WildMod.MOD_ID, "particle/sculk_rf_0"));
//            registry.register(new Identifier(WildMod.MOD_ID, "particle/sculk_rf_1"));
//            registry.register(new Identifier(WildMod.MOD_ID, "particle/sculk_rf_0"));
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
        ParticleFactoryRegistry.getInstance().register(RegisterParticles.SONIC_BOOM, ExplosionLargeParticle.Factory::new);

        BlockRenderLayerMap.INSTANCE.putBlock(MangroveWoods.MANGROVE_LEAVES, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MangroveWoods.MANGROVE_ROOTS, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(MangroveWoods.MANGROVE_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MangroveWoods.MANGROVE_TRAPDOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MangroveWoods.MANGROVE_PROPAGULE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(SculkVeinBlock.SCULK_VEIN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.FROGSPAWN, RenderLayer.getCutout());

        EntityRendererRegistry.register(RegisterEntities.WARDEN, WardenEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(WARDEN, WardenEntityModel::getTexturedModelData);

        EntityRendererRegistry.register(RegisterEntities.ALLAY, AllayEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_ALLAY_LAYER, AllayEntityModel::getTexturedModelData);

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
            client.execute(() -> SculkParticleHandler.shriekerShriek(client.world, shrieker, direction));
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
                assert client.world != null;
                SculkParticleHandler.shriekerGargle2(client.world, shrieker);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(WARDEN_DIG_PACKET, (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            int ticks = buf.readInt();
            client.execute(() -> {
                assert client.world != null;
                SculkParticleHandler.wardenDig(client.world, pos, (160-ticks));
            });
        });
    }

    public static DataGenerator create(final Path output, final Collection<Path> inputs, final boolean includeClient, final boolean includeServer, final boolean includeDev, final boolean includeReports, final boolean validate, final GameVersion gameVersion, final boolean ignoreCache) {
        final DataGenerator dataGenerator = new DataGenerator(output, inputs, gameVersion, ignoreCache);
        dataGenerator.addProvider(includeClient || includeServer, new SnbtProvider(dataGenerator).addWriter(new StructureValidatorProvider()));
        dataGenerator.addProvider(includeServer, new GameEventTagProvider(dataGenerator));
        return dataGenerator;
    }
}
