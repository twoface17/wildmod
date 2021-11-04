package frozenblock.wild.mod.mixins;


import frozenblock.wild.mod.blocks.SculkCatalystBlock;
import frozenblock.wild.mod.liukrastapi.GenerateSculk;
import frozenblock.wild.mod.liukrastapi.Sphere;
import frozenblock.wild.mod.registry.RegisterBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
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


@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public abstract Vec3d getPos();

    @Shadow
    public abstract World getEntityWorld();

    @Shadow
    public abstract BlockPos getBlockPos();

    @Shadow
    public abstract boolean isConnectedThroughVehicle(Entity entity);

    @Inject(method = "remove", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onRemove(CallbackInfo ci) {
        Entity entity = (Entity) ((Object) this);
        // LIUKRAST NOTE - THIS LINE BELOW SAYS THAT IS ALWAYS FALSE, IT IS NOT!! PLEASE DON'T EDIT
        if (entity instanceof LivingEntity) {
            if (!(entity instanceof ArmorStandEntity || entity instanceof PlayerEntity)) {
                BlockState blockState = RegisterBlocks.SCULK_CATALYST.getDefaultState();
                if (Sphere.checkSphereWithPLaceAndParticles(blockState, this.getEntityWorld(), this.getBlockPos(), 10, blockState.with(SculkCatalystBlock.BLOOM, true).with(SculkCatalystBlock.COOLDOWN, 2), ParticleTypes.SOUL, 0.2, 0.2, 0.2, 1, 1)) {
                    GenerateSculk.generateSculk(this.getEntityWorld(), this.getBlockPos());
                }
            }
        }
    }
}
