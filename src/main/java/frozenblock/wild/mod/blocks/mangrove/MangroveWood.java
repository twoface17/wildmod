package frozenblock.wild.mod.blocks.mangrove;

import frozenblock.wild.mod.registry.MangroveWoods;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public class MangroveWood extends PillarBlock {
    public MangroveWood(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if(itemStack.isIn(FabricToolTags.AXES)) {
            itemStack.damage(1, (LivingEntity)player, p -> p.sendToolBreakStatus(hand));
            BlockState blockState = MangroveWoods.STRIPPED_MANGROVE_WOOD.getDefaultState();
            world.setBlockState(pos, (BlockState) blockState.with(AXIS, state.get(AXIS)));
            world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_AXE_STRIP, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            return ActionResult.success(true);
        } else {
            return ActionResult.PASS;
        }
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        List<ItemStack> dropsOriginal = super.getDroppedStacks(state, builder);
        if (!dropsOriginal.isEmpty())
            return dropsOriginal;
        return Collections.singletonList(new ItemStack(this, 1));
    }
}
