package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.block.entity.SculkCatalystBlockEntity;
import net.frozenblock.wildmod.event.WildGameEvent;
import net.frozenblock.wildmod.fromAccurateSculk.ActivatorGrower;
import net.frozenblock.wildmod.fromAccurateSculk.SculkGrower;
import net.frozenblock.wildmod.misc.Sphere;
import net.frozenblock.wildmod.registry.RegisterAccurateSculk;
import net.frozenblock.wildmod.registry.RegisterBlocks;
import net.frozenblock.wildmod.registry.RegisterTags;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract boolean isUsingItem();

    @Shadow public abstract ItemStack getStackInHand(Hand hand);

    @Inject(method = "setHealth", at = @At("HEAD"))
    private void setHealth(float f, CallbackInfo info) {
        LivingEntity entity = LivingEntity.class.cast(this);
        if (entity.getType() == EntityType.ENDER_DRAGON && f == 0.0F) {
            entity.emitGameEvent(RegisterAccurateSculk.DEATH, entity, entity.getBlockPos().down(20));
            entity.emitGameEvent(RegisterAccurateSculk.DEATH, entity, entity.getBlockPos().down(14));
            entity.emitGameEvent(RegisterAccurateSculk.DEATH, entity, entity.getBlockPos().down(7));
            entity.emitGameEvent(RegisterAccurateSculk.DEATH, entity, entity.getBlockPos());
            entity.emitGameEvent(RegisterAccurateSculk.DEATH, entity, entity.getBlockPos().up(7));
            entity.emitGameEvent(RegisterAccurateSculk.DEATH, entity, entity.getBlockPos().up(14));
            entity.emitGameEvent(RegisterAccurateSculk.DEATH, entity, entity.getBlockPos().up(20));
        }
    }


    @Inject(method = "updatePostDeath", at = @At("HEAD"))
    private void updatePostDeath(CallbackInfo info) {
        LivingEntity entity = LivingEntity.class.cast(this);
        ++entity.deathTime;
        if (entity.deathTime == 19 && !entity.world.isClient()) {
            BlockPos pos = entity.getBlockPos();
            World world = entity.world;
            if (RegisterTags.entityTagContains(entity.getType(), RegisterTags.DROPSXP)) {
                //SculkSpreadManager sculkSpreadManager = SculkSpreadManager.create();
                int numCatalysts = Sphere.blocksInSphere(pos, 9, RegisterBlocks.SCULK_CATALYST, world);
                if (numCatalysts > 0) {
                    entity.emitGameEvent(RegisterAccurateSculk.DEATH, entity, pos);
                    //sculkSpreadManager.spread(new BlockPos((Vec3d.ofCenter(pos).withBias(Direction.UP, 0.5))), 5);
                    SculkGrower.sculk(pos, world, entity, numCatalysts);
                    int rVal2 = getHighestRadius(world, pos);
                    ActivatorGrower.startGrowing(rVal2, rVal2, pos, world);
                }
            }
        }
    }

    public int getHighestRadius(World world, BlockPos pos) {
        int current = 3;
        for (BlockPos blockPos : Sphere.blockPosSphere(pos, 9, RegisterBlocks.SCULK_CATALYST, world)) {
            BlockEntity catalyst = world.getBlockEntity(blockPos);
            if (catalyst instanceof SculkCatalystBlockEntity sculkCatalystBlockEntity) {
                current = Math.max(current, sculkCatalystBlockEntity.lastSculkRange);
            }
        }
        return current;
    }

    @Inject(method = "setCurrentHand", at = @At("TAIL"))
    private void setCurrentHand(Hand hand, CallbackInfo ci) {
        ItemStack itemStack = this.getStackInHand(hand);
        if (!itemStack.isEmpty() && !this.isUsingItem()) {
            if (!this.world.isClient) {
                this.emitGameEvent(WildGameEvent.ITEM_INTERACT_START);
            }
        }
    }

    @Inject(method = "clearActiveItem", at = @At("TAIL"))
    private void clearActiveItem(CallbackInfo ci) {
        if (!this.world.isClient) {
            boolean bl = this.isUsingItem();
            if (bl) {
                this.emitGameEvent(WildGameEvent.ITEM_INTERACT_FINISH);
            }
        }
    }

    @Shadow
    protected void initDataTracker() {

    }

    @Shadow
    public void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Shadow
    public void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Shadow
    public Packet<?> createSpawnPacket() {
        return null;
    }
}
