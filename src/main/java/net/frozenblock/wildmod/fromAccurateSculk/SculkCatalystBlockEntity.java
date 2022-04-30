package net.frozenblock.wildmod.fromAccurateSculk;

import net.frozenblock.wildmod.block.SculkCatalystBlock;
import net.frozenblock.wildmod.event.PositionSource;
import net.frozenblock.wildmod.liukrastapi.Criteria;
import net.frozenblock.wildmod.liukrastapi.Vec3d;
import net.frozenblock.wildmod.world.gen.SculkSpreadManager;
import net.frozenblock.wildmod.world.gen.random.WildAbstractRandom;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.gen.random.AbstractRandom;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Random;

public class SculkCatalystBlockEntity extends BlockEntity implements net.frozenblock.wildmod.event.GameEventListener {
    private final net.frozenblock.wildmod.event.BlockPositionSource positionSource = new net.frozenblock.wildmod.event.BlockPositionSource(this.pos);
    private final SculkSpreadManager spreadManager = SculkSpreadManager.create();


    public SculkCatalystBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(WildBlockEntityType.SCULK_CATALYST, blockPos, blockState);
    }

    public PositionSource getPositionSource() {
        return this.positionSource;
    }

    public int getRange() {
        return 8;
    }

    public boolean listen(ServerWorld world, net.frozenblock.wildmod.event.GameEvent event, net.frozenblock.wildmod.event.GameEvent.Emitter emitter, Vec3d pos) {
        if (event == GameEvent.ENTITY_KILLED) {
            Entity livingEntity2 = emitter.sourceEntity();
            if (livingEntity2 instanceof LivingEntity livingEntity) {
                if (!livingEntity.isExperienceDroppingDisabled()) {
                    this.spreadManager.spread(new BlockPos(pos.withBias(Direction.UP, 0.5)), livingEntity.getXpToDrop);
                    livingEntity.disableExperienceDropping();
                    LivingEntity livingEntity1 = livingEntity.getAttacker();
                    if (livingEntity1 instanceof ServerPlayerEntity serverPlayerEntity) {
                        DamageSource damageSource = livingEntity.getRecentDamageSource() == null ? DamageSource.player(serverPlayerEntity) : livingEntity.getRecentDamageSource();
                        Criteria.KILL_MOB_NEAR_SCULK_CATALYST.trigger(serverPlayerEntity, emitter.sourceEntity(), damageSource);
                    }

                    SculkCatalystBlock.bloom(world, this.pos, this.getCachedState(), (AbstractRandom)world.getRandom());
                }

                return true;
            }
        }

        return false;
    }

    public static void tick(World world, BlockPos pos, BlockState state, SculkCatalystBlockEntity blockEntity) {
        blockEntity.spreadManager.tick(world, pos, (WildAbstractRandom)world.getRandom(), true);
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
