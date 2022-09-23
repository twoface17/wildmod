package net.frozenblock.wildmod.block.entity;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.wildmod.registry.RegisterAccurateSculk;
import net.frozenblock.wildmod.registry.RegisterTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.SculkSensorListener;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SculkCatalystListener implements GameEventListener {
    protected final PositionSource positionSource;
    protected final int range;
    protected final SculkSensorListener.Callback callback;
    protected Optional<GameEvent> event = Optional.empty();
    protected int distance;
    protected int delay = 0;

    public SculkCatalystListener(PositionSource positionSource, int range, SculkSensorListener.Callback callback) {
        this.positionSource = positionSource;
        this.range = range;
        this.callback = callback;
    }

    public void tick(World world) {
        if (this.event.isPresent()) {
            --this.delay;
            if (this.delay <= 0) {
                this.delay = 0;
                this.callback.accept(world, this, this.event.get(), this.distance);
                this.event = Optional.empty();
            }
        }
    }

    @Override
    public PositionSource getPositionSource() {
        return this.positionSource;
    }

    @Override
    public int getRange() {
        return this.range;
    }

    @Override
    public boolean listen(World world, GameEvent gameEvent, @Nullable Entity entity, BlockPos blockPos) {
        if (!this.shouldActivate(gameEvent, entity, world)) {
            return false;
        }
        Optional<BlockPos> optional = this.positionSource.getPos(world);
        if (optional.isEmpty()) {
            return false;
        }
        BlockPos blockPos2 = optional.get();
        if (!this.callback.accepts(world, this, blockPos, gameEvent, entity)) {
            return false;
        }
        this.listen(world, gameEvent, blockPos, blockPos2, entity);
        return true;
    }

    private boolean shouldActivate(GameEvent gameEvent, @Nullable Entity entity, World world) {
        return entity != null && gameEvent == RegisterAccurateSculk.DEATH && RegisterTags.entityTagContains(entity.getType(), RegisterTags.DROPSXP);
    }

    public void pseudoSculk(World world, @Nullable Entity entity) {
        Optional<BlockPos> optional = this.positionSource.getPos(world);
        if (optional.isPresent()) {
            if (entity != null) {
                BlockPos thisPos = optional.get();
                if (RegisterTags.entityTagContains(entity.getType(), RegisterTags.THREE)) {
                    ExperienceOrbEntity.spawn((ServerWorld) world, Vec3d.ofCenter(thisPos.add(0, 0.5, 0)), UniformIntProvider.create(1, 3).get(world.getRandom()));
                } else if (RegisterTags.entityTagContains(entity.getType(), RegisterTags.FIVE)) {
                    ExperienceOrbEntity.spawn((ServerWorld) world, Vec3d.ofCenter(thisPos).add(0, 0.5, 0), UniformIntProvider.create(3, 5).get(world.getRandom()));
                } else if (RegisterTags.entityTagContains(entity.getType(), RegisterTags.TEN)) {
                    ExperienceOrbEntity.spawn((ServerWorld) world, Vec3d.ofCenter(thisPos).add(0, 0.5, 0), UniformIntProvider.create(7, 10).get(world.getRandom()));
                } else if (RegisterTags.entityTagContains(entity.getType(), RegisterTags.TWENTY)) {
                    ExperienceOrbEntity.spawn((ServerWorld) world, Vec3d.ofCenter(thisPos).add(0, 0.5, 0), UniformIntProvider.create(15, 20).get(world.getRandom()));
                } else if (RegisterTags.entityTagContains(entity.getType(), RegisterTags.FIFTY)) {
                    ExperienceOrbEntity.spawn((ServerWorld) world, Vec3d.ofCenter(thisPos).add(0, 0.5, 0), UniformIntProvider.create(30, 50).get(world.getRandom()));
                } else if (RegisterTags.entityTagContains(entity.getType(), RegisterTags.ONEHUNDRED)) {
                    ExperienceOrbEntity.spawn((ServerWorld) world, Vec3d.ofCenter(thisPos).add(0, 0.5, 0), 200);
                }
            }
        }
    }


    public void listen(World world, GameEvent gameEvent, BlockPos blockPos, BlockPos blockPos2, @Nullable Entity entity) {
        this.event = Optional.of(gameEvent);
        if (world instanceof ServerWorld) {
            this.delay = 2;
            // ChunkGenerator manager = ((ServerWorld) world).toServerWorld().getChunkManager().getChunkGenerator();
            // Random random = new Random();
            // SculkSpreadFeatures.SCULK_PATCH_SPREAD.generate((ServerWorld) world, manager, random, blockPos.up());
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(blockPos2);
            for (ServerPlayerEntity player : PlayerLookup.around((ServerWorld) world, blockPos2, 32)) {
                ServerPlayNetworking.send(player, RegisterAccurateSculk.CATALYST_PARTICLE_PACKET, buf);
            }
        }
    }


    public interface Callback {
        boolean accepts(World var1, GameEventListener var2, BlockPos var3, GameEvent var4, @Nullable Entity var5);

        void accept(World var1, GameEventListener var2, GameEvent var3, int var4);
    }
}
