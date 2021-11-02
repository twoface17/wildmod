package frozenblock.wild.mod.registry;

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

public class RegisterDispenser {

    public static void RegisterDispenser() {
        DispenserBlock.registerBehavior(Items.POTION, new FallibleItemDispenserBehavior() {
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                Direction direction = (Direction)pointer.getBlockState().get(DispenserBlock.FACING);
                BlockPos blockPos = pointer.getPos().offset(direction);
                World world = pointer.getWorld();
                BlockState blockState = world.getBlockState(blockPos);
                this.setSuccess(true);
                if (blockState.isOf(Blocks.DIRT)) {
                    if (PotionUtil.getPotion(stack) == Potions.WATER) {
                        world.setBlockState(blockPos, RegisterBlocks.MUD_BLOCK.getDefaultState());
                        return new ItemStack(Items.GLASS_BOTTLE);

                    } else {
                        return super.dispenseSilently(pointer, stack);
                    }
                } else {
                    return super.dispenseSilently(pointer, stack);
                }
            }
        });
    }
}
