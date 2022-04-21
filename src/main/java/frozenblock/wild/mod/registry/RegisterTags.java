package frozenblock.wild.mod.registry;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.entity.FrogVariant;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegisterTags {
    public static final TagKey<Block> CONVERTABLE_TO_MUD = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "convertable_to_mud"));
    public static final TagKey<Block> FROG_PREFER_JUMP_TO = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "frog_prefer_jump_to"));
    public static final TagKey<Block> MANGROVE_LOGS_CAN_GROW_THROUGH = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "mangrove_logs_can_grow_through"));
    public static final TagKey<Block> MANGROVE_ROOTS_CAN_GROW_THROUGH = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "mangrove_roots_can_grow_through"));
    public static final TagKey<FrogVariant> TEMPERATE = TagKey.of(FrogVariant.FROG_VARIANT_KEY, new Identifier(WildMod.MOD_ID, "textures/entity/frog/temperate_frog.png"));
    public static final TagKey<FrogVariant> WARM = TagKey.of(FrogVariant.FROG_VARIANT_KEY, new Identifier(WildMod.MOD_ID, "textures/entity/frog/warm_frog.png"));
    public static final TagKey<FrogVariant> COLD = TagKey.of(FrogVariant.FROG_VARIANT_KEY, new Identifier(WildMod.MOD_ID, "textures/entity/frog/cold_frog.png"));
    public static final TagKey<EntityType<?>> FROG_FOOD = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(WildMod.MOD_ID, "frog_food"));
}
