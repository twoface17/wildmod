package net.frozenblock.wildmod.block.wild;

import net.frozenblock.wildmod.WildMod;
import net.minecraft.block.SignBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.SignType;

public class WildSignBlock extends SignBlock {
    public WildSignBlock(Settings settings, SignType signType) {
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
