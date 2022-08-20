package net.frozenblock.wildmod.mixins;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.frozenblock.wildmod.event.WildGameEvent;
import net.frozenblock.wildmod.registry.RegisterEntities;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SculkSensorBlock.class)
public abstract class SculkSensorBlockMixin extends BlockWithEntity {
    //FROM ACCURATE SCULK

    public SculkSensorBlockMixin(AbstractBlock.Settings properties) {
        super(properties);
    }

    // allows to add additional things to the sculk sensor frequency list
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2IntMaps;unmodifiable(Lit/unimi/dsi/fastutil/objects/Object2IntMap;)Lit/unimi/dsi/fastutil/objects/Object2IntMap;"))
    private static <K> Object2IntMap<K> makeFrequenciesMapModifiable(Object2IntMap<? extends K> m) {
        return (Object2IntMap<K>) m;
    }

    @Override
    public void onSteppedOn(World world, BlockPos blockPos, BlockState blockState, Entity entity) {
        if (world instanceof ServerWorld && SculkSensorBlock.isInactive(blockState) && entity.getType() != RegisterEntities.WARDEN) {
            SculkSensorBlock.setActive(world, blockPos, blockState, 1);
            world.emitGameEvent(entity, WildGameEvent.SCULK_SENSOR_TENDRILS_CLICKING, blockPos);
        }

        super.onSteppedOn(world, blockPos, blockState, entity);
    }

    @Override
    public void onStacksDropped(BlockState state, ServerWorld serverWorld, BlockPos pos, ItemStack stack) {
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
            this.dropExperience(serverWorld, pos, 5);
        }
    }
}
