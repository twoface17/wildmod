package frozenblock.wild.mod.fromAccurateSculk;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.*;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SculkTags {
    public static final TagKey<Block> SCULK_VEIN_REPLACEABLE = TagKey.of(Registry.BLOCK_KEY, new Identifier("twm", "sculk_vein_replaceable"));
    public static final TagKey<Block> BLOCK_REPLACEABLE = TagKey.of(Registry.BLOCK_KEY, new Identifier("twm", "sculk_block_replaceable"));
    public static final TagKey<Block> SCULK_UNTOUCHABLE = TagKey.of(Registry.BLOCK_KEY, new Identifier("twm", "sculk_untouchable"));
    public static final TagKey<Block> SCULK = TagKey.of(Registry.BLOCK_KEY, new Identifier("twm", "sculk"));
    public static final TagKey<EntityType<?>> THREE = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier("twm", "3xp"));
    public static final TagKey<EntityType<?>> FIVE = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier("twm", "5xp"));
    public static final TagKey<EntityType<?>> TEN = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier("twm", "10xp"));
    public static final TagKey<EntityType<?>> TWENTY = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier("twm", "20xp"));
    public static final TagKey<EntityType<?>> FIFTY = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier("twm", "50xp"));
    public static final TagKey<EntityType<?>> ONEHUNDRED = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier("twm", "100xp"));
    public static final TagKey<EntityType<?>> DROPSXP = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier("twm", "dropsxp"));
    public static final TagKey<Block> ALWAYS_WATER = TagKey.of(Registry.BLOCK_KEY, new Identifier("twm", "always_water_sculk"));
    public static final TagKey<Block> WARDEN_UNSPAWNABLE = TagKey.of(Registry.BLOCK_KEY, new Identifier("twm", "warden_unspawnable"));
    public static final TagKey<Block> WARDEN_NON_COLLIDE = TagKey.of(Registry.BLOCK_KEY, new Identifier("twm", "warden_non_collide"));
    //ACTIVATORS
    public static final TagKey<Block> ACTIVATORS = TagKey.of(Registry.BLOCK_KEY, new Identifier("twm", "sculk_activators"));
    public static final TagKey<Block> COMMON_ACTIVATORS = TagKey.of(Registry.BLOCK_KEY, new Identifier("twm", "common_activators"));
    public static final TagKey<Block> RARE_ACTIVATORS = TagKey.of(Registry.BLOCK_KEY, new Identifier("twm", "rare_activators"));
    public static final TagKey<Block> GROUND_ACTIVATORS = TagKey.of(Registry.BLOCK_KEY, new Identifier("twm", "ground_activators"));
}
