package frozenblock.wild.mod.mixins;

import com.google.common.collect.ImmutableList;
import frozenblock.wild.mod.registry.RegisterBlocks;
import frozenblock.wild.mod.registry.RegisterWorldgen;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import net.minecraft.world.gen.surfacebuilder.VanillaSurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.gen.surfacebuilder.VanillaSurfaceRules.class)
public class VanillaSurfaceRulesInjector {
    @ModifyVariable(method = "createDefaultRule", at = @At("STORE"), ordinal = 8)
    private static MaterialRules.MaterialRule injected(MaterialRules.MaterialRule x) {
        return MaterialRules.sequence(MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR, MaterialRules.sequence(MaterialRules.condition(MaterialRules.biome(RegisterWorldgen.MANGROVE_SWAMP), VanillaSurfaceRulesBlockInvoker.invokeBlock(RegisterBlocks.MUD_BLOCK)), MaterialRules.condition(MaterialRules.biome(BiomeKeys.SWAMP), MaterialRules.condition(MaterialRules.aboveY(YOffset.fixed(62), 0), MaterialRules.condition(MaterialRules.not(MaterialRules.aboveY(YOffset.fixed(63), 0)), MaterialRules.condition(MaterialRules.noiseThreshold(NoiseParametersKeys.SURFACE_SWAMP, 0.0), VanillaSurfaceRulesBlockInvoker.invokeBlock(Blocks.WATER))))), x)));
    }

}
