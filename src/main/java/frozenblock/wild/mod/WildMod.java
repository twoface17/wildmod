package frozenblock.wild.mod;

import frozenblock.wild.mod.registry.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WildMod implements ModInitializer {

    public static final String MOD_ID = "twm";

    @Override
    public void onInitialize() {
        RegisterBlocks.RegisterBlocks();
        RegisterItems.RegisterItems();
        RegisterWorldgen.RegisterWorldgen();
        RegisterEntities.RegisterEntities();
        RegisterDispenser.RegisterDispenser();
    }
}
