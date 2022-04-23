package frozenblock.wild.mod.fromAccurateSculk;

import frozenblock.wild.mod.block.SculkCatalystBlock;
import frozenblock.wild.mod.block.SculkShriekerBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;


public class WildBlockEntityType {

    public static BlockEntityType<SculkCatalystBlockEntity> SCULK_CATALYST;
    public static BlockEntityType<SculkShriekerBlockEntity> SCULK_SHRIEKER;

    public static void init() {
        SCULK_CATALYST = Registry.register(Registry.BLOCK_ENTITY_TYPE, "twm:sculk_catalyst", FabricBlockEntityTypeBuilder.create(SculkCatalystBlockEntity::new, SculkCatalystBlock.SCULK_CATALYST_BLOCK).build(null));

        SCULK_SHRIEKER = Registry.register(Registry.BLOCK_ENTITY_TYPE, "twm:sculk_shrieker", FabricBlockEntityTypeBuilder.create(SculkShriekerBlockEntity::new, SculkShriekerBlock.SCULK_SHRIEKER_BLOCK).build(null));
    }
}
