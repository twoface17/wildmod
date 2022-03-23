package frozenblock.wild.mod.registry;

import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegisterTags {
    public static final TagKey<Block> CONVERTABLE_TO_MUD = TagKey.of(Registry.BLOCK_KEY, new Identifier("twm", "convertable_to_mud"));
    public static final TagKey<Block> FROG_PREFER_JUMP_TO = TagKey.of(Registry.BLOCK_KEY, new Identifier("twm", "frog_prefer_jump_to"));
}
