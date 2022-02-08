package frozenblock.wild.mod.fromAccurateSculk;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class SculkTags {
    public static final Tag<Block> SCULK_VEIN_REPLACEABLE = TagFactory.BLOCK.create(new Identifier("twm", "sculk_vein_replaceable"));
    public static final Tag<Block> BLOCK_REPLACEABLE = TagFactory.BLOCK.create(new Identifier("twm", "sculk_block_replaceable"));
    public static final Tag<Block> SCULK_UNTOUCHABLE = TagFactory.BLOCK.create(new Identifier("twm", "sculk_untouchable"));
    public static final Tag<Block> SCULK = TagFactory.BLOCK.create(new Identifier("twm", "sculk"));
    public static final Tag.Identified<EntityType<?>> THREE = TagFactory.ENTITY_TYPE.create(new Identifier("twm", "3xp"));
    public static final Tag.Identified<EntityType<?>> FIVE = TagFactory.ENTITY_TYPE.create(new Identifier("twm", "5xp"));
    public static final Tag.Identified<EntityType<?>> TEN = TagFactory.ENTITY_TYPE.create(new Identifier("twm", "10xp"));
    public static final Tag.Identified<EntityType<?>> TWENTY = TagFactory.ENTITY_TYPE.create(new Identifier("twm", "20xp"));
    public static final Tag.Identified<EntityType<?>> FIFTY = TagFactory.ENTITY_TYPE.create(new Identifier("twm", "50xp"));
    public static final Tag.Identified<EntityType<?>> ONEHUNDRED = TagFactory.ENTITY_TYPE.create(new Identifier("twm", "100xp"));
    public static final Tag.Identified<EntityType<?>> DROPSXP = TagFactory.ENTITY_TYPE.create(new Identifier("twm", "dropsxp"));
    public static final Tag.Identified<Block> ALWAYS_WATER = TagFactory.BLOCK.create(new Identifier("twm", "always_water_sculk"));
    public static final Tag.Identified<Block> WARDEN_UNSPAWNABLE = TagFactory.BLOCK.create(new Identifier("twm", "warden_unspawnable"));
    public static final Tag.Identified<Block> WARDEN_NON_COLLIDE = TagFactory.BLOCK.create(new Identifier("twm", "warden_non_collide"));
}
