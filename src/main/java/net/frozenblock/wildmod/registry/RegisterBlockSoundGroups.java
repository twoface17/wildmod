package net.frozenblock.wildmod.registry;

import net.minecraft.sound.BlockSoundGroup;

public class RegisterBlockSoundGroups {

    public static final BlockSoundGroup MUDDY_MANGROVE_ROOTS = new BlockSoundGroup(
            1.0F,
            1.0F,
            RegisterSounds.BLOCK_MUDDY_MANGROVE_ROOTS_BREAK,
            RegisterSounds.BLOCK_MUDDY_MANGROVE_ROOTS_STEP,
            RegisterSounds.BLOCK_MUDDY_MANGROVE_ROOTS_PLACE,
            RegisterSounds.BLOCK_MUDDY_MANGROVE_ROOTS_HIT,
            RegisterSounds.BLOCK_MUDDY_MANGROVE_ROOTS_FALL
    );

    public static final BlockSoundGroup SCULK = new BlockSoundGroup(
            1.0F,
            1.0F,
            RegisterSounds.BLOCK_SCULK_BREAK,
            RegisterSounds.BLOCK_SCULK_STEP,
            RegisterSounds.BLOCK_SCULK_PLACE,
            RegisterSounds.BLOCK_SCULK_HIT,
            RegisterSounds.BLOCK_SCULK_FALL
    );

    public static final BlockSoundGroup SCULK_VEIN = new BlockSoundGroup(
            1.0F,
            1.0F,
            RegisterSounds.BLOCK_SCULK_VEIN_BREAK,
            RegisterSounds.BLOCK_SCULK_VEIN_STEP,
            RegisterSounds.BLOCK_SCULK_VEIN_PLACE,
            RegisterSounds.BLOCK_SCULK_VEIN_HIT,
            RegisterSounds.BLOCK_SCULK_VEIN_FALL
    );

    public static final BlockSoundGroup SCULK_CATALYST = new BlockSoundGroup(
            1.0F,
            1.0F,
            RegisterSounds.BLOCK_SCULK_CATALYST_BREAK,
            RegisterSounds.BLOCK_SCULK_CATALYST_STEP,
            RegisterSounds.BLOCK_SCULK_CATALYST_PLACE,
            RegisterSounds.BLOCK_SCULK_CATALYST_HIT,
            RegisterSounds.BLOCK_SCULK_CATALYST_FALL
    );

    public static final BlockSoundGroup SCULK_SHRIEKER = new BlockSoundGroup(
            1.0F,
            1.0F,
            RegisterSounds.BLOCK_SCULK_SHRIEKER_BREAK,
            RegisterSounds.BLOCK_SCULK_SHRIEKER_STEP,
            RegisterSounds.BLOCK_SCULK_SHRIEKER_PLACE,
            RegisterSounds.BLOCK_SCULK_SHRIEKER_HIT,
            RegisterSounds.BLOCK_SCULK_SHRIEKER_FALL
    );

    public static void init() {
    }
}
