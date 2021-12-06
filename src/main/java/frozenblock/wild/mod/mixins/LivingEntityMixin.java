package frozenblock.wild.mod.mixins;


import frozenblock.wild.mod.blocks.SculkCatalystBlock;
import frozenblock.wild.mod.liukrastapi.GenerateSculk;
import frozenblock.wild.mod.liukrastapi.Sphere;
import frozenblock.wild.mod.registry.RegisterBlocks;
import frozenblock.wild.mod.registry.RegisterParticles;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onDeath", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void callCatalyst(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) ((Object) this);
        if(entity != null) {
            if (entity instanceof MobEntity) {
                BlockState blockState = RegisterBlocks.SCULK_CATALYST.getDefaultState();
                if (Sphere.checkSphereWithPLaceAndParticles(blockState, this.getEntityWorld() , this.getBlockPos(), 10, blockState.with(SculkCatalystBlock.BLOOM, true), RegisterParticles.SCULK_SOUL, 0, 2, 0, 1, 1)) {
                    GenerateSculk.generateSculk(this.getEntityWorld(), this.getBlockPos());
                }
            }
        }
    }
}
