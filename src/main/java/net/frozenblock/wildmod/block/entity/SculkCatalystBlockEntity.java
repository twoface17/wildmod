package net.frozenblock.wildmod.block.entity;

import com.google.common.annotations.VisibleForTesting;
import net.frozenblock.wildmod.block.SculkCatalystBlock;
import net.frozenblock.wildmod.event.GameEventListener;
import net.frozenblock.wildmod.fromAccurateSculk.WildBlockEntityType;
import net.frozenblock.wildmod.liukrastapi.TickCriterion;
import net.frozenblock.wildmod.world.gen.SculkSpreadManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import org.jetbrains.annotations.Nullable;

public class SculkCatalystBlockEntity extends BlockEntity implements GameEventListener {
    private final BlockPositionSource positionSource = new BlockPositionSource(this.pos);
    private final SculkSpreadManager spreadManager = SculkSpreadManager.create();

    public SculkCatalystBlockEntity(BlockPos pos, BlockState state) {
        super(WildBlockEntityType.SCULK_CATALYST, pos, state);
    }

    public boolean method_43723() {
        return true;
    }

    public PositionSource getPositionSource() {
        return this.positionSource;
    }

    public int getRange() {
        return 8;
    }

    @Override
    public boolean listen(World world, GameEvent event, @Nullable Entity entity, BlockPos pos) {
        return false;
    }

    @Override
    public boolean listen(ServerWorld world, net.frozenblock.wildmod.event.GameEvent.class_7447 arg) {
        if (this.isRemoved()) {
            return false;
        } else {
            net.frozenblock.wildmod.event.GameEvent.Emitter emitter = arg.method_43727();
            if (arg.method_43724() == GameEvent.ENTITY_KILLED) {
                Entity i = emitter.sourceEntity();
                if (i instanceof WildLivingEntity livingEntity) {
                    if (!livingEntity.isExperienceDroppingDisabled()) {
                        int sus = livingEntity.getXpToDrop();
                        if (livingEntity.shouldDropXp() && sus > 0) {
                            this.spreadManager.spread(new BlockPos(arg.method_43726().withBias(Direction.UP, 0.5)), sus);
                        }

                        livingEntity.disableExperienceDropping();
                        LivingEntity livingEntity2 = livingEntity.getAttacker();
                        if (livingEntity2 instanceof ServerPlayerEntity serverPlayerEntity) {
                            DamageSource damageSource = livingEntity.getRecentDamageSource() == null
                                    ? DamageSource.player(serverPlayerEntity)
                                    : livingEntity.getRecentDamageSource();
                            TickCriterion.KILL_MOB_NEAR_SCULK_CATALYST.trigger(serverPlayerEntity, emitter.sourceEntity(), damageSource);
                        }

                        SculkCatalystBlock.bloom(world, this.pos, this.getCachedState(), world.getRandom());
                   }

                    return true;
                }
            }

            return false;
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, SculkCatalystBlockEntity blockEntity) {
        blockEntity.spreadManager.tick(world, pos, world.getRandom(), true);
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.spreadManager.readNbt(nbt);
    }

    protected void writeNbt(NbtCompound nbt) {
        this.spreadManager.writeNbt(nbt);
        super.writeNbt(nbt);
    }

    @VisibleForTesting
    public SculkSpreadManager getSpreadManager() {
        return this.spreadManager;
    }
}
