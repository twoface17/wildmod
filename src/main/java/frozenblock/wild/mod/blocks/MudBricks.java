package frozenblock.wild.mod.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

public class MudBricks extends Block {
    public MudBricks() {
        super(FabricBlockSettings
                .of(Material.STONE)
                .sounds(BlockSoundGroup.DEEPSLATE_BRICKS)
                .strength(1.5f, 10f)
        );
    }
}
