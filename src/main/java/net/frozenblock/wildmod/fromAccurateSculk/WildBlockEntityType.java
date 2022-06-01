package net.frozenblock.wildmod.fromAccurateSculk;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.frozenblock.wildmod.block.entity.SculkCatalystBlockEntity;
import net.frozenblock.wildmod.block.entity.SculkShriekerBlockEntity;
import net.frozenblock.wildmod.registry.RegisterBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;


public class WildBlockEntityType {

    public static BlockEntityType<SculkCatalystBlockEntity> SCULK_CATALYST;
    public static BlockEntityType<SculkShriekerBlockEntity> SCULK_SHRIEKER;

    public static void register() {
        SCULK_CATALYST = Registry.register(Registry.BLOCK_ENTITY_TYPE, "twm:sculk_catalyst", FabricBlockEntityTypeBuilder.create(SculkCatalystBlockEntity::new, RegisterBlocks.SCULK_CATALYST).build(null));
        SCULK_SHRIEKER = Registry.register(Registry.BLOCK_ENTITY_TYPE, "twm:sculk_shrieker", FabricBlockEntityTypeBuilder.create(SculkShriekerBlockEntity::new, RegisterBlocks.SCULK_SHRIEKER).build(null));
    }
}
