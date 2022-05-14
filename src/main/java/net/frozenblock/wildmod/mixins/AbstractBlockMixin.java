package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.block.SculkShriekerBlock;
import net.frozenblock.wildmod.entity.WardenEntity;
import net.frozenblock.wildmod.fromAccurateSculk.SensorLastEntity;
import net.frozenblock.wildmod.registry.RegisterBlocks;
import net.frozenblock.wildmod.registry.RegisterEntities;
import net.frozenblock.wildmod.registry.RegisterTags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
    @Inject(at = @At("TAIL"), method = "onStacksDropped")
    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack, CallbackInfo info) {
        if (state.getBlock()==Blocks.SCULK_SENSOR) {
            if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
                if (world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
                    ExperienceOrbEntity.spawn(world, Vec3d.ofCenter(pos), new Random().nextInt(3, 5));
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "onUse")
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.getBlockState(hit.getBlockPos()).isIn(RegisterTags.CONVERTABLE_TO_MUD)) {
            ItemStack itemStack = player.getStackInHand(hand);

            if (itemStack.isItemEqual(Items.POTION.getDefaultStack())) {
                if (PotionUtil.getPotion(itemStack) == Potions.WATER) {
                    world.setBlockState(pos, RegisterBlocks.MUD.getDefaultState());
                    if (!world.isClient) {
                        world.playSound(
                                null,
                                pos,
                                SoundEvents.ITEM_BOTTLE_EMPTY,
                                SoundCategory.BLOCKS,
                                1f,
                                1f
                        );
                        ((ServerWorld) world).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, RegisterBlocks.MUD.getDefaultState()),
                                hit.getBlockPos().getX() + 0.5,
                                hit.getBlockPos().getY() + 0.5,
                                hit.getBlockPos().getZ() + 0.5,
                                100,
                                0.2,
                                0.2,
                                0.2,
                                10
                        );

                    }
                    if (player.canModifyBlocks()) {
                        if (!player.isCreative()) {
                            ItemStack removeItem = new ItemStack(Items.GLASS_BOTTLE);
                            player.setStackInHand(hand, removeItem);
                        }
                    }
                }
            }
        }
    }

    @Inject(at =  @At("TAIL"), method = "onEntityCollision")
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (!world.isClient) {
            if (world.getBlockState(pos) == Blocks.SCULK_SENSOR.getDefaultState() && entity != null && entity.getType() != RegisterEntities.WARDEN) {
                if (entity instanceof LivingEntity) {
                    SensorLastEntity.addEntity(entity, pos, entity.getBlockPos(), null);
                    int lastEntity = SensorLastEntity.getLastEntity(pos);
                    LivingEntity target = (LivingEntity)world.getEntityById(lastEntity);
                    BlockPos lastEventPos = SensorLastEntity.getLastPos(pos);
                    if (lastEventPos != null) {
                        Box box = new Box(pos.getX() - 18, pos.getY() - 18, pos.getZ() - 18, pos.getX() + 18, pos.getY() + 18, pos.getZ() + 18);
                        List<WardenEntity> list = world.getNonSpectatingEntities(WardenEntity.class, box);
                        Iterator<WardenEntity> var11 = list.iterator();
                        WardenEntity wardenEntity;
                        while (var11.hasNext()) {
                            wardenEntity = var11.next();
                            if (wardenEntity.getBlockPos().isWithinDistance(pos, 16)) {
                                wardenEntity.listen(lastEventPos, wardenEntity.getWorld(), target, 35, pos);
                            }
                        }
                    }
                }
                SculkSensorBlock.setActive(world, pos, state, 15);
            }
            assert entity != null;
            if (entity instanceof PlayerEntity && world.getBlockState(pos) == RegisterBlocks.SCULK_SHRIEKER.getDefaultState() || world.getBlockState(pos) == RegisterBlocks.SCULK_SHRIEKER.getDefaultState().with(Properties.WATERLOGGED, true)) {
                if (!SculkShriekerBlock.findWarden(world, pos)) {
                    ((SculkShriekerBlock) Objects.requireNonNull(world.getBlockState(pos)).getBlock()).writeDir(world, pos, entity.getBlockPos());
                    SculkShriekerBlock.setStepActive(world, pos, state);
                }
            }
        }
    }

}
