package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.registry.RegisterBlocks;
import net.frozenblock.wildmod.registry.RegisterWorldgen;
import net.minecraft.block.Block;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import net.minecraft.world.gen.surfacebuilder.VanillaSurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(VanillaSurfaceRules.class)
public abstract class VanillaSurfaceRulesInjector {

    @Shadow
    private static MaterialRules.MaterialRule block(Block block) {
        return MaterialRules.block(block.getDefaultState());
    }

    private static final MaterialRules.MaterialRule MUD = block(RegisterBlocks.MUD);


    @ModifyVariable(method = "createDefaultRule", at = @At("STORE"), ordinal = 8)
    private static MaterialRules.MaterialRule injected(MaterialRules.MaterialRule materialRule) {
        return MaterialRules.sequence(
                MaterialRules.condition(MaterialRules.biome(RegisterWorldgen.MANGROVE_SWAMP), MUD), materialRule);
    }
}
