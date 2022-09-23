package net.frozenblock.wildmod.block.wild;

import net.frozenblock.wildmod.WildMod;
import net.minecraft.block.WallSignBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.SignType;

public class WildWallSignBlock extends WallSignBlock {
    public WildWallSignBlock(Settings settings, SignType signType) {
        super(settings, signType);
    }

    @Override
    public final Identifier getLootTableId() {
        Identifier correctedLootTableId = new Identifier(WildMod.MOD_ID, "blocks/" + this.getSignType().getName() + "_sign");
        if (this.lootTableId != correctedLootTableId) {
            this.lootTableId = correctedLootTableId;
        }

        return this.lootTableId;
    }
}
