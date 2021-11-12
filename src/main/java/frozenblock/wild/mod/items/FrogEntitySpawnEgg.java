package frozenblock.wild.mod.items;

import frozenblock.wild.mod.entity.FrogEntity;
import frozenblock.wild.mod.liukrastapi.CustomMath;
import frozenblock.wild.mod.registry.RegisterEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class FrogEntitySpawnEgg extends SpawnEggItem {
    public FrogEntitySpawnEgg(EntityType<? extends MobEntity> type, int primaryColor, int secondaryColor, Settings settings) {
        super(type, primaryColor, secondaryColor, settings);
    }


    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        ItemStack itemStack = context.getStack();
        BlockPos blockPos = context.getBlockPos();
        Direction direction = context.getSide();
        BlockState blockState = world.getBlockState(blockPos);

        if (!(world instanceof ServerWorld)) {
            return ActionResult.SUCCESS;
        } else {
            if (blockState.isOf(Blocks.SPAWNER)) {
                BlockEntity blockEntity = world.getBlockEntity(blockPos);
                if (blockEntity instanceof MobSpawnerBlockEntity) {
                    MobSpawnerLogic mobSpawnerLogic = ((MobSpawnerBlockEntity)blockEntity).getLogic();
                    EntityType<?> entityType = this.getEntityType(itemStack.getNbt());
                    mobSpawnerLogic.setEntityId(entityType);
                    blockEntity.markDirty();
                    world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
                    itemStack.decrement(1);
                    return ActionResult.CONSUME;
                }
            }
            FrogEntity frogEntity = new FrogEntity(RegisterEntities.FROG, world);
            frogEntity.setYaw(CustomMath.random() * 360 * (float) Math.PI/90);
            System.out.println(CustomMath.random() * 360 * (float) Math.PI/90);


            BlockPos blockPos3;
            if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
                blockPos3 = blockPos;
            } else {
                blockPos3 = blockPos.offset(direction);
            }
            if(FrogEntity.canColdSpawn(world, blockPos3)) {
                frogEntity.setVariant(FrogEntity.Variant.COLD);
            } else if(FrogEntity.canTropicalSpawn(world, blockPos3)) {
                frogEntity.setVariant(FrogEntity.Variant.TROPICAL);
            }
            frogEntity.setPos(blockPos3.getX()+0.5, blockPos3.getY()+0.1, blockPos3.getZ()+0.5);
            world.spawnEntity(frogEntity);
            itemStack.decrement(1);
            world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);

            return ActionResult.CONSUME;
        }
    }


}
