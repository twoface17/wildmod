package net.frozenblock.wildmod.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import net.frozenblock.wildmod.block.entity.SculkShriekerWarningManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class WildPlayerEntity extends PlayerEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected SculkShriekerWarningManager sculkShriekerWarningManager = new SculkShriekerWarningManager(0, 0, 0);
    public WildPlayerEntity(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient) {
            this.sculkShriekerWarningManager.tick();
        }
    }

    @Override
    public abstract boolean isSpectator();

    @Override
    public abstract boolean isCreative();

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("warden_spawn_tracker", 10)) {
            SculkShriekerWarningManager.CODEC
                    .parse(new Dynamic<>(NbtOps.INSTANCE, nbt.get("warden_spawn_tracker")))
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(sculkShriekerWarningManager -> this.sculkShriekerWarningManager = sculkShriekerWarningManager);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        SculkShriekerWarningManager.CODEC
                .encodeStart(NbtOps.INSTANCE, this.sculkShriekerWarningManager)
                .resultOrPartial(LOGGER::error)
                .ifPresent(nbtElement -> nbt.put("warden_spawn_tracker", nbtElement));
    }

    public SculkShriekerWarningManager getSculkShriekerWarningManager() {
        return this.sculkShriekerWarningManager;
    }
}
