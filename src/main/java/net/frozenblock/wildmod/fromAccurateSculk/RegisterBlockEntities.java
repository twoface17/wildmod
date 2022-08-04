package net.frozenblock.wildmod.fromAccurateSculk;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.block.entity.SculkCatalystBlockEntity;
import net.frozenblock.wildmod.block.entity.SculkShriekerBlockEntity;
import net.frozenblock.wildmod.misc.GoatHornIdFix;
import net.frozenblock.wildmod.registry.RegisterBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class RegisterBlockEntities {

    public static final BlockEntityType<SculkCatalystBlockEntity> SCULK_CATALYST = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "sculk_catalyst"), FabricBlockEntityTypeBuilder.create(SculkCatalystBlockEntity::new, RegisterBlocks.SCULK_CATALYST).build(null));
    public static final BlockEntityType<SculkShriekerBlockEntity> SCULK_SHRIEKER = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "sculk_shrieker"), FabricBlockEntityTypeBuilder.create(SculkShriekerBlockEntity::new, RegisterBlocks.SCULK_SHRIEKER).build(null));

    public static void register() {
    }
}
