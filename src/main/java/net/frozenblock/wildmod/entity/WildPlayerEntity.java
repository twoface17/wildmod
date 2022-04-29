/*package net.frozenblock.wildmod.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import net.frozenblock.wildmod.block.entity.SculkShriekerWarningManager;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.Objects;
import java.util.UUID;

public class WildPlayerEntity extends PlayerEntity {
    private static final Logger field_38197 = LogUtils.getLogger();
    private final GameProfile gameProfile;

    public SculkShriekerWarningManager sculkShriekerWarningManager = new SculkShriekerWarningManager(0, 0, 0);

    public WildPlayerEntity(World world, @Nullable BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
        this.setUuid(getUuidFromProfile(profile));
        this.gameProfile = profile;
        this.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, yaw, 0.0F);
    }

    public static UUID getUuidFromProfile(GameProfile profile) {
        UUID uUID = profile.getId();
        if (uUID == null) {
            uUID = getOfflinePlayerUuid(profile.getName());
        }

        return uUID;
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setUuid(getUuidFromProfile(this.gameProfile));
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
    }

    public Text getName() {
        return super.getName();
    }



    public SculkShriekerWarningManager getSculkShriekerWarningManager() {
        return this.sculkShriekerWarningManager;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }
}
*/